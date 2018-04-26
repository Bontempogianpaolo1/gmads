package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Home extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    ImageView profileImage;
    private static final String EXTRA_PROFILE_KEY="my_token";
    private DatabaseReference mProfileReference;
    FirebaseDatabase database;
    public ValueEventListener mProfileListener;
    public String mProfile;
    private TextView navName;
    private TextView navMail;
    private ImageView navImage;
    private Profile profile;
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
        toolbar =  findViewById(R.id.toolbarHome);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navName =  headerView.findViewById(R.id.navName);
        navMail =  headerView.findViewById(R.id.navMail);
        navImage =  headerView.findViewById(R.id.navImage);
    }

    @Override
    public void onStart(){
        super.onStart();
        //settare navbar

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        headerView.setBackgroundResource(R.color.colorPrimaryDark);
        mProfileReference = database.getReference()
                .child("users")
                .child(FirebaseManagement.getUser().getUid());
        mProfileReference.keepSynced(true);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile myuser = dataSnapshot.getValue(Profile.class);
                assert myuser != null;
                //dati navbar
                navName.setText(myuser.getName());
                navName.append(" " + myuser.getSurname());
                navMail.setText(myuser.getEmail());
                //setto foto
                profile = dataSnapshot.getValue(Profile.class);
                URL url = null;

                if(Objects.requireNonNull(profile).getImage()!=null) {
                    try {
                        File localFile = File.createTempFile("image", "jpg");
                        StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                                .child("users")
                                .child(FirebaseManagement.getUser().getUid())
                                .child("profileimage.jpg");

                        profileImageRef.getFile(localFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        navImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("errore",e.toString());
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {//default image
                    File directory = getApplicationContext().getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
                    String path = directory.getPath();
                    File f = new File(path, "profileimage.jpg");
                    if (f.exists()) {
                        try {
                            Bitmap image = BitmapFactory.decodeStream(new FileInputStream(f));
                            navImage.setImageBitmap(image);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mProfileReference.addValueEventListener(postListener);
        mProfileListener = postListener;
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
            DrawerLayout mDrawerLayout;
            mDrawerLayout = findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawers();
            return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}