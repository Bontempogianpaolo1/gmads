package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Home extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    ImageView profileImage;
    ImageView drawerImage;

    private static final String EXTRA_PROFILE_KEY="post_key";
    private DatabaseReference mProfileReference;
    FirebaseDatabase database;
    private ValueEventListener mProfileListener;
    private String mProfile;
    private TextView navName;
    private TextView navMail;
    private ImageView navImage;
    SharedPreferences prefs;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    View headerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //profilo
        mProfile= prefs.getString(EXTRA_PROFILE_KEY,null);
        database= FirebaseManagement.getDatabase();
        if(mProfile==null){
            database.setPersistenceEnabled((true));
        }
        if(mProfile!=null) {
            mProfileReference = FirebaseDatabase.getInstance().getReference().child("users").child(mProfile);
            mProfileReference.keepSynced(true);
        }
        //settare toolbar + titolo
        toolbar = (Toolbar) findViewById(R.id.toolbarHome);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        //settare navbar
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navName = (TextView) headerView.findViewById(R.id.navName);
        navMail = (TextView) headerView.findViewById(R.id.navMail);
        navImage = (ImageView) headerView.findViewById(R.id.navImage);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        headerView.setBackgroundResource(R.color.colorPrimaryDark);
        //--fine navbar
        //gestire file online
        File directory = getApplicationContext().getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        String path = directory.getPath();
        File f=new File(path,"profile.jpg");
        if(f.exists()) {
            try {
                Bitmap image=BitmapFactory.decodeStream(new FileInputStream(f));
                navImage.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        if(mProfile!=null) {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Profile myuser = dataSnapshot.getValue(Profile.class);
                    assert myuser != null;
                    //dati navbar
                    navName.setText(myuser.getName());
                    navName.append(" " + myuser.getSurname());
                    navMail.setText(myuser.getEmail());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mProfileReference.addValueEventListener(postListener);
            mProfileListener = postListener;
        }else{
            //dati navbar
            navName.setText(getString(R.string.nameExample));
            navName.append(" " + getString(R.string.surnameExample));
            navMail.setText(getString(R.string.emailExample));
        }
    }

    //for EditButton in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.actionbar_empty, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Intent intentMod = new Intent(this, EditProfile.class);
        Intent intentMod = new Intent(this, SaveBook.class);
        //intentMod.putExtra(EXTRA_PROFILE_KEY,mProfile);
        startActivity(intentMod);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

        return true;
    }
    //
    //to close the application on back button
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_showProfile) {
            // Handle the camera action
            Intent intentMod = new Intent(this, ShowProfile.class);
            //intentMod.putExtra(EXTRA_PROFILE_KEY,mProfile);
            startActivity(intentMod);

            return true;
        } else if (id == R.id.nav_addBook) {
            Intent intentMod = new Intent(this, AddBook.class);
            //intentMod.putExtra(EXTRA_PROFILE_KEY,mProfile);
            startActivity(intentMod);

            return true;
        } else if (id == R.id.nav_home) {
            //deve solo chiudersi la navbar
            DrawerLayout mDrawerLayout;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawers();
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}