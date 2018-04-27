package gmads.it.gmads_lab1;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AddBook extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_ISBN_IMAGE = 1888;
    private static final int ZBAR_CAMERA_PERMISSION = 1;

    private String ISBNcode = null;
    //private TextView textViewISBN;
    private Button ISBNbutton;
    private ImageView next;
    private EditText editISBN;
    //private Bitmap barcodeBitmap;
    Toolbar toolbar;
    SharedPreferences prefs;
    DrawerLayout drawer;

    private static final String EXTRA_PROFILE_KEY="my_token";
    private DatabaseReference mProfileReference;
    FirebaseDatabase database;
    private ValueEventListener mProfileListener;
    private String mProfile;
    private TextView navName;
    private TextView navMail;
    private ImageView navImage;
    NavigationView navigationView;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mProfile = prefs.getString(EXTRA_PROFILE_KEY, null);
        database = FirebaseManagement.getDatabase();
        if (mProfile == null) {
            database.setPersistenceEnabled((true));
        }
        if (mProfile != null) {
            mProfileReference = FirebaseDatabase.getInstance().getReference().child("users").child(mProfile);
            mProfileReference.keepSynced(true);
        }

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddBook);
        toolbar.setTitle("Scanner");
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar = (Toolbar) findViewById(R.id.toolbarAddBook);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        this.editISBN = (EditText) findViewById(R.id.tvIsbn);
        this.ISBNbutton = findViewById(R.id.scan);
        this.next = findViewById(R.id.next);

        next.setOnClickListener(this::onNextClick);
        ISBNbutton.setOnClickListener(this::onGetISBNClick);
    }
    @Override
    public void onStart() {
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
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setISBNcode(String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editISBN.addTextChangedListener(textWatcher);
        /*
        editISBN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    setISBNcode(editISBN.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });
        */

    }

    public void setISBNcode(String isbn){
        this.ISBNcode = isbn;
    }

    public void onNextClick(View v){

            if(this.ISBNcode== null || this.ISBNcode.length()!=13){
                Tools t= new Tools();
                t.showPopup(this,getString(R.string.isbnerror),"", "Ok").show();
            }else {
                Intent pickIntent = new Intent(this, SaveBook.class);
                // pickIntent.putExtra(EXTRA_PROFILE_KEY,mProfile).;
                prefs.edit().putString("ISBN",ISBNcode).apply();
                // database.setPersistenceEnabled(false);
                startActivity(pickIntent);
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout mDrawerLayout;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.nav_showProfile) {
            // Handle the camera action
            Intent intentMod = new Intent(this, ShowProfile.class);
            startActivity(intentMod);
            return true;
        } else if (id == R.id.nav_addBook) {
            //deve solo chiudersi la navbar


            mDrawerLayout.closeDrawers();
        } else if(id == R.id.nav_home){
            Intent intentMod = new Intent(this, Home.class);
            startActivity(intentMod);
            return true;
        }


        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onGetISBNClick(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, Scanner.class);
            startActivityForResult(intent, REQUEST_ISBN_IMAGE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ZBAR_CAMERA_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_ISBN_IMAGE);

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == REQUEST_ISBN_IMAGE) {
           if(resultCode == RESULT_OK) {
               this.ISBNcode = data.getData().toString();
               editISBN.setText(this.ISBNcode);
           }
       }
    }
    /*

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
    */
}
