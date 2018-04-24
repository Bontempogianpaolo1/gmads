package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ShowProfile extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    ImageView profileImage;
    ImageView drawerImage;

    private static final String EXTRA_PROFILE_KEY="post_key";
    private DatabaseReference mProfileReference;
    FirebaseDatabase database;
    private ValueEventListener mProfileListener;
    private Profile profile;
    private TextView navName;
    private TextView navMail;
    private ImageView navImage;
    SharedPreferences prefs;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    View headerView;
    TextView vName;
    TextView vSurname;
    TextView vEmail;
    TextView vBio;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //mProfile= prefs.getString(EXTRA_PROFILE_KEY,null);
        database= FirebaseManagement.mDatabase;
        /*if(mProfile==null){
            database.setPersistenceEnabled((true));

        }
        if(mProfile!=null) {

            mProfileReference = FirebaseDatabase.getInstance().getReference().child("users").child(mProfile);
            mProfileReference.keepSynced(true);
        }*/

        //settare toolbar + titolo + navbar
        toolbar = (Toolbar) findViewById(R.id.toolbarShowP);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        profileImage = findViewById(R.id.profile_image);
        headerView = navigationView.getHeaderView(0);
        navName = (TextView) headerView.findViewById(R.id.navName);
        navMail = (TextView) headerView.findViewById(R.id.navMail);
        navImage = (ImageView) headerView.findViewById(R.id.navImage);
        vName = findViewById(R.id.name);
        vEmail = findViewById(R.id.email);
        vBio = findViewById(R.id.bio);
        toolbar.setTitle(getString(R.string.showProfile));
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        headerView.setBackgroundResource(R.color.colorPrimaryDark);

        profileImage.setImageDrawable(getDrawable(R.drawable.default_profile));
        navImage.setImageDrawable(getDrawable(R.drawable.default_profile));

        vBio.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseManagement.mDatabase.getReference().child("users").child(FirebaseManagement.mUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profile = dataSnapshot.getValue(Profile.class);

                        vName.setText(profile.name + " " + profile.surname);
                        vEmail.setText(profile.email);
                        vBio.setText(profile.description);
                        URL url = null;

                        if(profile.getImage()!=null) {
                            try {
                                url = new URL(profile.getImage());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            try {
                                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                profileImage.setImageBitmap(image);
                                navImage.setImageBitmap(image);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("loadPost:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        Toast.makeText(ShowProfile.this, "Failed to load profile.",
                                Toast.LENGTH_SHORT).show();
                        // [END_EXCLUDE]

                    }
        });



        if(profile==null){
            vName.setText(getString(R.string.name));
            vName.append(" " + getString(R.string.surname));
            navName.setText(getString(R.string.name));
            navName.append(" " + getString(R.string.surname));
            vEmail.setText(getString(R.string.email));
            navMail.setText(getString(R.string.email));
            vBio.setText(getString(R.string.description));
        }
    }

    @Override
    public void onStop(){

        super.onStop();

        /*if(mProfileListener!=null){
            mProfileReference.removeEventListener(mProfileListener);

        }*/
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
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}