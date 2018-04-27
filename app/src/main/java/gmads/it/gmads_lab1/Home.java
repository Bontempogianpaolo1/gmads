package gmads.it.gmads_lab1;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Home extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    DatabaseReference mProfileReference;
    public ValueEventListener mProfileListener;
    private TextView navName;
    private TextView navMail;
    private ImageView navImage;
    private Profile profile;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    View headerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setActViews();
        setNavViews();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }
    public void setActViews(){
        toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        setSupportActionBar(toolbar);
    }
    public void setNavViews(){
        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navName =  headerView.findViewById(R.id.navName);
        navMail =  headerView.findViewById(R.id.navMail);
        navImage =  headerView.findViewById(R.id.navImage);
        headerView.setBackgroundResource(R.color.colorPrimaryDark);
    }
    @Override
    public void onStart(){
        super.onStart();
        mProfileReference = FirebaseManagement
                .getDatabase()
                .getReference()
                .child("users")
                .child(FirebaseManagement.getUser().getUid());

        mProfileReference.keepSynced(true);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile myuser = dataSnapshot.getValue(Profile.class);
                if(myuser!=null) {
                    //dati navbar
                    navName.setText(myuser.getName());
                    navName.append(" " + myuser.getSurname());
                    navMail.setText(myuser.getEmail());
                    //setto foto
                    if (myuser.getImage() != null) {
                        try {
                            File localFile = File.createTempFile("image", "jpg");
                            StorageReference profileImageRef = FirebaseManagement
                                    .getStorage()
                                    .getReference()
                                    .child("users")
                                    .child(FirebaseManagement.getUser().getUid())
                                    .child("profileimage.jpg");

                            profileImageRef
                                    .getFile(localFile)
                                    .addOnSuccessListener(taskSnapshot -> navImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath())))
                                    .addOnFailureListener(e -> Log.d("errore", e.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //default image
                        navImage.setImageDrawable(getDrawable(R.drawable.default_profile));
                    }
                }else{
                    navName.setText(getString(R.string.name));
                    navName.append(" " + getString(R.string.surname));
                    navMail.setText(getString(R.string.email));
                    navImage.setImageDrawable(getDrawable(R.drawable.default_profile));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("error",databaseError.getDetails());
            }
        };
        mProfileReference.addValueEventListener(postListener);

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
        Intent intentMod = new Intent(this, SaveBook.class);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_showProfile) {
            // Handle the camera action
            Intent intentMod = new Intent(this, ShowProfile.class);
            startActivity(intentMod);
            finish();
            return true;
        } else if (id == R.id.nav_addBook) {
            Intent intentMod = new Intent(this, AddBook.class);
            startActivity(intentMod);
            finish();
            return true;
        } else if (id == R.id.nav_home) {
            //deve solo chiudersi la navbar
            drawer.closeDrawers();
            return true;
        }else{
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }
}