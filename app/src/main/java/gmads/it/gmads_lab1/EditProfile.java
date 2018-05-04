package gmads.it.gmads_lab1;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.regex.Pattern;
import static android.graphics.Color.RED;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

public class EditProfile extends AppCompatActivity {

    private Profile profile;
    static final int MY_CAMERA_REQUEST_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1888;
    static final int REQUEST_IMAGE_LIBRARY = 1889;
    private ImageView profileImage;//profile image
    private ProgressBar progressbar;
    Bitmap newBitMapProfileImage; //temp for new image
    private Uri uriProfileImage;
    private String profileImageUrl;
    boolean imagechanged=false;
    File tempFile;
    Toolbar toolbar;
    ContextWrapper cw;
    File directory;
    String path;
    LinearLayout ll;
    LinearLayout l2;
    TextView vName;
    TextView vSurname;
    TextView vEmail;
    TextView vBio;
    TextView vCAP;
    TextView vCountry;
    //TextView changeIm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        findViews();
        cw = new ContextWrapper(getApplicationContext());
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // path to /data/data/yourapp/app_data/imageDir
        directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        path = directory.getPath();
        //inizialize  layout
        l2.setOnClickListener(this::setFocusOnClick);
        //inizialize  user data
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //set image
        profileImage.setImageDrawable(getDrawable(R.drawable.default_profile));
        profileImage.setOnClickListener(this::onClickImage);
        getUserInfo();
    }
    public void findViews(){
        toolbar = findViewById(R.id.toolbar);
        progressbar = findViewById(R.id.progressBar);
        //ll= findViewById(R.id.linearLayout1);
        l2= findViewById(R.id.linearlayout2);
        profileImage = findViewById(R.id.profile_image);
        vName = findViewById(R.id.name_input);
        vSurname = findViewById(R.id.surname_input);
        vEmail = findViewById(R.id.email_input);
        vBio = findViewById(R.id.address_input);
        vCountry = findViewById(R.id.country);
        vCAP = findViewById(R.id.CAP);
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
        Tools t = new Tools();
        //set popup
        android.app.AlertDialog.Builder ad = t.showPopup(this, getString(R.string.saveQuestion), "", getString(R.string.cancel));
        ad.setPositiveButton("Ok", (vi, w) -> updateUserInfo());
        ad.show();
    }
    //for SaveButton in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.actionbar_editp, menu);
        return true;
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
            profileImage.setImageBitmap(newBitMapProfileImage);
            //manage request image from gallery
        } else if ( requestCode==REQUEST_IMAGE_LIBRARY && resultCode == RESULT_OK) {
            imagechanged=true;
            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(Objects.requireNonNull(imageUri));
                newBitMapProfileImage = BitmapFactory.decodeStream(imageStream);
                uriProfileImage = imageUri;
                profileImage.setImageBitmap(newBitMapProfileImage);
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
        String country = vCountry.getText().toString();

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
        if(country.isEmpty()){
            vCountry.setError("@string/country_required");
            vCountry.requestFocus();
            return;
        }

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
                        FirebaseManagement.updateUserData(profile);
                        startActivity(pickIntent);
                        progressbar.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> progressbar.setVisibility(View.GONE));
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
        progressbar.setVisibility(View.VISIBLE);
        profileImage.setVisibility(View.GONE);
        FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profile = dataSnapshot.getValue(Profile.class);
                        if(profile != null) {
                            vName.setText(profile.getName());
                            vSurname.setText(profile.getSurname());
                            vEmail.setText(profile.getEmail());
                            vBio.setText(profile.getDescription());
                            if (profile.getImage() != null) {
                                try {
                                    File localFile = File.createTempFile("images", "jpg");

                                    StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                                            .child("users")
                                            .child(FirebaseManagement.getUser().getUid())
                                            .child("profileimage.jpg");

                                    profileImageRef.getFile(localFile)
                                            .addOnSuccessListener(taskSnapshot -> {
                                                progressbar.setVisibility(View.GONE);
                                                profileImage.setVisibility(View.VISIBLE);
                                                profileImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));

                                            }).addOnFailureListener(e -> {
                                        progressbar.setVisibility(View.GONE);
                                        profileImage.setVisibility(View.VISIBLE);
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                progressbar.setVisibility(View.GONE);
                                profileImage.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            vName.setHint(getString(R.string.name));
                            vSurname.setHint(getString(R.string.surname));
                            vEmail.setHint(getString(R.string.email));
                            vBio.setHint(getString(R.string.bioExample));
                            progressbar.setVisibility(View.GONE);
                            profileImage.setVisibility(View.VISIBLE);
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
}