package gmads.it.gmads_lab1;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import android.net.Uri;
import android.widget.Toast;

import org.json.JSONObject;

import gmads.it.gmads_lab1.model.Profile;

import static android.graphics.Color.RED;

public class EditProfile extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    static final int MY_CAMERA_REQUEST_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1888;
    static final int REQUEST_IMAGE_LIBRARY = 1889;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView coverImage;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    //private SimpleDraweeView avatar;
    private ImageView avatar;

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;

    private Profile profile;
    Bitmap newBitMapProfileImage; //temp for new image
    private Uri uriProfileImage;
    private String profileImageUrl;
    boolean imagechanged=false;
    File tempFile;
    ContextWrapper cw;
    Tools tools;
    File directory;
    String path;
    LinearLayout ll;
    LinearLayout l2;
    TextView vName;
    TextView vSurname;
    TextView vEmail;
    TextView vBio;
    TextView vCAP;
    Spinner vCountry;
    String country = null;
    String country_l;
    ArrayAdapter<String> adapter;


    private void findViews() {
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        coverImage = (ImageView) findViewById(R.id.imageview_placeholder);
        framelayoutTitle = (FrameLayout) findViewById(R.id.framelayout_title);
        linearlayoutTitle = (LinearLayout) findViewById(R.id.linearlayout_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textviewTitle = (TextView) findViewById(R.id.textview_title);
        //avatar = (SimpleDraweeView) findViewById(R.id.avatar);
        avatar = findViewById(R.id.avatar);
        avatar.setImageDrawable(getDrawable(R.drawable.default_picture));
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);

        //progressbar = findViewById(R.id.progressBar);
        l2= findViewById(R.id.linearlayout);
        //l2= findViewById(R.id.linearlayout2);
        vName = findViewById(R.id.name);
        vSurname = findViewById(R.id.surname);
        vEmail = findViewById(R.id.email);
        vBio = findViewById(R.id.bio);
        vCountry = findViewById(R.id.country);

        vCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> arg0, View arg1,
                                          int position, long id) {
                   country = vCountry.getSelectedItem().toString();
               }
               @Override
               public void onNothingSelected(AdapterView<?> arg0) {
               }
           });

        vCAP = findViewById(R.id.cap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fresco.initialize(this);
        setContentView(R.layout.activity_edit_profile);

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String countryt;
        for( Locale loc : locale ){
            countryt = loc.getDisplayCountry();
            if( countryt.length() > 0 && !countries.contains(countryt) ) {
                countries.add(countryt);
            }
        }
        country_l = getResources().getString(R.string.choose_country);
        countries.add(country_l);
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        findViews();
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, countries);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        vCountry.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(country_l);
        vCountry.setSelection(spinnerPosition);


        setupUI(findViewById(R.id.linearlayout));
        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);
        textviewTitle.setText(getString(R.string.editProfile));
        setSupportActionBar(toolbar);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);

        tools = new Tools();

        //set avatar and cover
        avatar.setImageResource(R.drawable.default_picture);
        coverImage.setImageResource(R.drawable.cover_edit);

        cw = new ContextWrapper(getApplicationContext());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // path to /data/data/yourapp/app_data/imageDir
        directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        path = directory.getPath();
        //inizialize  layout
        //l2.setOnClickListener(this::setFocusOnClick);
        //inizialize  user data
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //set image
        avatar.setImageDrawable(getDrawable(R.drawable.default_picture));
        avatar.setOnClickListener(this::onClickImage);
        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //save data on click save
    private void onSaveClick() {

        //check on email using a regex
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        Pattern pat = Pattern.compile("^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$");
        if (!pat.matcher(vEmail.getText()).matches()) {
            vEmail.setText("");
            vEmail.setHint(R.string.errorEmail);
            vEmail.setHintTextColor(RED);
            vEmail.requestFocus();
            return;
        }

        if(vCAP.getText().toString().isEmpty()){
            vCAP.setError(getString(R.string.cap_required));
            vCAP.requestFocus();
            return;
        }

        if(country == null||country.isEmpty() || vCountry.getSelectedItem().toString().equals(country_l) ){
            Toast.makeText(getApplicationContext(), getString(R.string.country_required), Toast.LENGTH_LONG).show();
            vCountry.requestFocus();
            return;
        }

        if(tools.isOnline(getApplicationContext())) {
            //set popup
            android.app.AlertDialog.Builder ad = tools.showPopup(this, getString(R.string.saveQuestion), "", getString(R.string.cancel));
            ad.setPositiveButton("Ok", (vi, w) -> updateUserInfo());
            ad.show();
        } else {
            android.app.AlertDialog.Builder ad = tools.showPopup(this, getString(R.string.noInternet), "", "");
            ad.setNegativeButton(getString(R.string.cancel), (vi, w) -> onStart());
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onSaveClick());
            ad.setCancelable(false);
            ad.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_editp, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void setFocusOnClick(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void onClickImage(View v) {
        Tools t= new Tools();
        //set popup
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        v.getContext();
        android.app.AlertDialog.Builder ad=t.showPopup(this,getString(R.string.takeImage),getString(R.string.selectGallery),getString(R.string.selectFromCamera));
        ad.setPositiveButton("gallery",(vi,w)->{
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, REQUEST_IMAGE_LIBRARY);
        });
        ad.setNegativeButton("photo",(vi,w)->{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
        ad.show();
        //-->
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            }

        }
    }


    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    //-->function activated when a request is terminated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //manage request image capture
        if (requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == RESULT_OK) {
            imagechanged=true;
            Bundle imageUri = data.getExtras();
            newBitMapProfileImage = (Bitmap) Objects.requireNonNull(imageUri).get("data");
            tempFile = saveImage(newBitMapProfileImage);
            uriProfileImage = Uri.fromFile(tempFile);
            avatar.setImageBitmap(newBitMapProfileImage);
            //manage request image from gallery
        } else if ( requestCode==REQUEST_IMAGE_LIBRARY && resultCode == RESULT_OK) {
            imagechanged=true;
            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(Objects.requireNonNull(imageUri));
                newBitMapProfileImage = BitmapFactory.decodeStream(imageStream);
                uriProfileImage = imageUri;
                avatar.setImageBitmap(newBitMapProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //function used to save the image in the correct path
    private File saveImage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        // Create imageDir
        tempFile = new File(directory,"profile.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempFile);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tempFile;
    }
    //animation back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            default://caso Save
                onSaveClick();
        }
        return super.onOptionsItemSelected(item);
    }
    //animation back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    public void updateUserInfo(){
        String name = vName.getText().toString();
        String surname = vSurname.getText().toString();
        String email = vEmail.getText().toString();
        String bio = vBio.getText().toString();
        String cap = vCAP.getText().toString();

        //String country = vCountry.getText().toString();





        if(name.isEmpty()){
            vName.setError(getString(R.string.name_require));
            vName.requestFocus();
            return;
        }
        if(surname.isEmpty()){
            vSurname.setError(getString(R.string.surname_required));
            vSurname.requestFocus();
            return;
        }
        if(email.isEmpty()){
            vEmail.setError(getString(R.string.email_required));
            vEmail.requestFocus();
            return;
        }
        if(cap.isEmpty()){
            vCAP.setError("@string/cap_required");
            vCAP.requestFocus();
            return;
        }
        if(country.isEmpty() || vCountry.getSelectedItem().toString().equals(country_l)){
            //vCountry.setError("@string/country_required");
            Toast.makeText(getApplicationContext(), getString(R.string.country_required), Toast.LENGTH_LONG).show();
            vCountry.requestFocus();
            return;
        }

        progressBarHolder.setVisibility(View.VISIBLE);

        StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                .child("users")
                .child(FirebaseManagement.getUser().getUid())
                .child("profileimage.jpg");
        Intent pickIntent = new Intent(this, ShowProfile.class);
        if(uriProfileImage != null){

            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(taskSnapshot -> {
                        profileImageUrl = Objects.requireNonNull(taskSnapshot.getDownloadUrl()).toString();
                        profile.setName(name);
                        profile.setSurname(surname);
                        profile.setEmail(email);
                        profile.setDescription(bio);
                        profile.setImage(profileImageUrl);
                        profile.setCAP(cap+", "+country);
                        FirebaseManagement.updateUserData(profile);

                        startActivity(pickIntent);
                        //progressbar.setVisibility(View.GONE);
                    });
                   // .addOnFailureListener(e -> //progressbar.setVisibility(View.GONE)// );
        }else{
            profileImageUrl = "";
            profile.setName(name);
            profile.setSurname(surname);
            profile.setEmail(email);
            profile.setDescription(bio);
            profile.setImage(profileImageUrl);
            String s = cap + ", " + country;
            profile.setCAP(s);
            //piglio coordinate
            getCoords(s);

            startActivity(pickIntent);
        }

    }

    private void getUserInfo(){
        //progressbar.setVisibility(View.VISIBLE);
        //avatar.setVisibility(View.GONE);

        if(tools.isOnline(getApplicationContext())) {
            FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            profile = dataSnapshot.getValue(Profile.class);
                            if (profile != null) {
                                vName.setText(profile.getName());
                                vSurname.setText(profile.getSurname());
                                vEmail.setText(profile.getEmail());
                                vBio.setText(profile.getDescription());
                                //controllo che ci sia il CAP
                                if (profile.getCAP().length() != 0) {
                                    String[] tmp = profile.getCAP().split(", ");
                                    vCAP.setText(tmp[0]);
                                    int pos = adapter.getPosition(tmp[1]);
                                    vCountry.setSelection(pos);
                                }

                                if(vCAP.getText().toString().isEmpty()){
                                    vCAP.setError(getString(R.string.cap_required));
                                    vCAP.requestFocus();
                                    return;
                                }

                                if(country != null) {
                                    if (country.isEmpty() || vCountry.getSelectedItem().toString().equals(country_l)) {
                                        Toast.makeText(getApplicationContext(), getString(R.string.country_required), Toast.LENGTH_LONG).show();
                                        vCountry.requestFocus();
                                        return;
                                    }
                                }

                                if (profile.getImage() != null) {
                                    try {
                                        File localFile = File.createTempFile("images", "jpg");

                                        StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                                                .child("users")
                                                .child(FirebaseManagement.getUser().getUid())
                                                .child("profileimage.jpg");

                                        profileImageRef.getFile(localFile)
                                                .addOnSuccessListener(taskSnapshot -> {
                                                    //progressbar.setVisibility(View.GONE);
                                                    //avatar.setVisibility(View.VISIBLE);
                                                    avatar.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));

                                                }).addOnFailureListener(e -> {
                                            //progressbar.setVisibility(View.GONE);
                                            //avatar.setVisibility(View.VISIBLE);
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    // progressbar.setVisibility(View.GONE);
                                    //avatar.setVisibility(View.VISIBLE);
                                }
                            } else {
                                vName.setHint(getString(R.string.name));
                                vSurname.setHint(getString(R.string.surname));
                                vEmail.setHint(getString(R.string.email));
                                vBio.setHint(getString(R.string.bioEditP));
                                //progressbar.setVisibility(View.GONE);
                                //avatar.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w("loadPost:onCancelled", databaseError.toException());
                            // [START_EXCLUDE]
                            Toast.makeText(EditProfile.this, R.string.Failed_to_load_profile,
                                    Toast.LENGTH_SHORT).show();
                            // [END_EXCLUDE]
                        }
                    });
        } else {
            android.app.AlertDialog.Builder ad = tools.showPopup(this, getString(R.string.noInternet), "", "");
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onStart());
            ad.setCancelable(false);
            ad.show();
        }
    }

    private void getCoords(String CAP) {//CAP = cap, country
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+CAP+"&key=AIzaSyAheBkNImDIqf4oQZ_A_hiNEug28vFw7A8";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {//Display the first 500 characters of the response string.

                    JSONObject resultObject;
                    String formatted_address = CAP;
                    Double lat;
                    Double lng;

                    try {
                        //piglio Json
                        resultObject = new JSONObject(response);
                        lat = resultObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        lng = resultObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    }catch (Exception e){
                        lat = 0.0;
                        lng = 0.0;
                    }
                    profile.setCAP(formatted_address);
                    profile.setLat(lat);
                    profile.setLng(lng);
                    FirebaseManagement.updateUserData(profile);
                }, error -> Log.d("That didn't work!","Error: "+error));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(EditProfile.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}