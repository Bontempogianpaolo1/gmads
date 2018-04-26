package gmads.it.gmads_lab1;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
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
import java.io.IOException;
import java.io.InputStream;

public class AddBook extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    private Bitmap barcodeBitmap;
    private TextView isbnText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mProfile= prefs.getString(EXTRA_PROFILE_KEY,null);
        database= FirebaseManagement.getDatabase();
        if(mProfile==null){
            database.setPersistenceEnabled((true));
        }
        if(mProfile!=null) {
            mProfileReference = FirebaseDatabase.getInstance().getReference().child("users").child(mProfile);
            mProfileReference.keepSynced(true);
        }

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddBook);
        toolbar.setTitle(getString(R.string.addBook));
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //--fine navbar

        Button barcodeButton = (Button) findViewById(R.id.buttonGet);
        barcodeButton.setOnClickListener(this::onGetISBNClick);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            //deve solo chiudersi la navbar
            DrawerLayout mDrawerLayout;
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawers();
        } else if(id == R.id.nav_home){
            Intent intentMod = new Intent(this, Home.class);
            startActivity(intentMod);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onGetISBNClick(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, Scanner.class);
            startActivity(intent);
        }
    }

    @Override
    //-->function activated when a request is terminated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //manage request image capture
        if (requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == RESULT_OK) {

            isbnText = (TextView) findViewById(R.id.textViewISBN);
            Bundle imageUri = data.getExtras();
            assert imageUri != null;

            barcodeBitmap = (Bitmap) imageUri.get("data");
            ImageManagement m = new ImageManagement();
            isbnText.setText(m.getIsbnFromBarcode(barcodeBitmap));
            //manage request image from gallery
        }
    }
}
