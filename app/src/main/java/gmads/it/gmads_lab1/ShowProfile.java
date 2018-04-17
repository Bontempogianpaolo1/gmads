package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ShowProfile extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    ImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String name = prefs.getString("name", getResources().getString(R.string.name));
        String surname = prefs.getString("surname", getResources().getString(R.string.surname));
        String email = prefs.getString("email", getString(R.string.description));
        String bio = prefs.getString("address", getResources().getString(R.string.description));
        //settare titolo activity nella action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarShowP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.showProfile));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        profileImage = findViewById(R.id.profile_image);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        String path = directory.getPath();
        File f=new File(path,"profile.jpg");
        if(f.exists()) {
            try {
                profileImage.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        TextView vName = findViewById(R.id.name);
        TextView vEmail = findViewById(R.id.email);
        TextView vAddress = findViewById(R.id.bio);
        vAddress.setMovementMethod(new ScrollingMovementMethod());
        if(name.compareTo("")==0){
            vName.setText(getResources().getString(R.string.name));
            vName.append(" " + getResources().getString(R.string.surname));
        }else{
            vName.setText(name);
            vName.append(" " + surname);
        }
        if(email.compareTo("")==0){
            vEmail.setText(getString(R.string.emailExample));
        }else{
            vEmail.setText(email);
        }
        if(bio.compareTo("")==0){
            vAddress.setText(getResources().getString(R.string.bioExample));
        }else{
            vAddress.setText(bio);
        }
    }
    //for EditButton in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.actionbar_showp, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentMod = new Intent(this, EditProfile.class);
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
            startActivity(intentMod);
            return true;
        } else if (id == R.id.nav_addBook) {
            Intent intentMod = new Intent(this, AddBook.class);
            startActivity(intentMod);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}