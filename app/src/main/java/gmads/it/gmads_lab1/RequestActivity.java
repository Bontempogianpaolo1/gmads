package gmads.it.gmads_lab1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;


import gmads.it.gmads_lab1.fragments.FragmentViewPagerAdapter;

public class RequestActivity extends AppCompatActivity{

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    TabLayout tab;
    ViewPager pager;
    Request_1_othersReq r1=new Request_1_othersReq();
    Request_2_myReq r2=new Request_2_myReq();

    private void setViews() {
        expListView = (ExpandableListView) findViewById(R.id.explv);
        tab=findViewById(R.id.tabs);
        pager= findViewById(R.id.viewPager);

    }

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_home);
        setContentView(R.layout.activity_request);
        setViews();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.request));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FragmentViewPagerAdapter vadapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        vadapter.addFragment(r1);
        //vadapter.addFragment(r2);
        pager.setAdapter(vadapter);
        tab.setupWithViewPager(pager);
        tab.getTabAt(0).setText(getText(R.string.others_req));
        //tab.getTabAt(1).setText(getText(R.string.my_req));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {

            }

            @Override
            public void onPageSelected( int position ) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged( int state ) {

            }
        });
    }
/*
    @Override
    protected void onStart () {
        super.onStart();
    }

    @Override
    protected void onStop () {
        super.onStop();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
    }
*/
    public void onClickUsername(View v){

        Toast.makeText(getApplicationContext(), "Link al profilo del tizio",Toast.LENGTH_LONG).show();
    }

    public void onClickYes(View v){

        Toast.makeText(getApplicationContext(), "Richiesta accettata",Toast.LENGTH_LONG).show();
    }

    public void onClickNo(View v){

        Toast.makeText(getApplicationContext(), "Richiesta rifiutata",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
        /*
        Intent intentMod = new Intent(this, Home.class);
        startActivity(intentMod);
        finish();
        */
        //moveTaskToBack(true);
    }
}
