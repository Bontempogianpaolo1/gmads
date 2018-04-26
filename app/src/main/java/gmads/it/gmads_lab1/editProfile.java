package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;
import static android.graphics.Color.RED;


import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfile extends AppCompatActivity {
    private static final String EXTRA_PROFILE_KEY="my_token";
    private DatabaseReference mProfileReference;
    private StorageReference storageReference;
    private ValueEventListener mProfileListener;
    private String mProfile;
    private Profile profile;
    static final int REQUEST_IMAGE_CAPTURE = 1888;
    static final int REQUEST_IMAGE_LIBRARY = 1889;
    private ImageView profileImage;//profile image
    private ProgressBar progressbar;
    private Bitmap newBitMapProfileImage; //temp for new image
    private Uri uriProfileImage;
    private String profileImageUrl;
    private SharedPreferences prefs;
    private boolean imagechanged=false;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        mProfile=prefs.getString(EXTRA_PROFILE_KEY,null);
        toolbar = (Toolbar) findViewById(R.id.toolbarEditP);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cw = new ContextWrapper(getApplicationContext());
        progressbar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // path to /data/data/yourapp/app_data/imageDir
        directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        path = directory.getPath();
        //inizialize  layout
        ll= findViewById(R.id.linearLayout1);
        l2= findViewById(R.id.linearlayout2);
        ll.setOnClickListener(this::setFocusOnClick);
        l2.setOnClickListener(this::setFocusOnClick);
        //inizialize  user data

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //set image
        profileImage = findViewById(R.id.profile_image);
        profileImage.setImageDrawable(getDrawable(R.drawable.default_profile));
        profileImage.setOnClickListener(this::onClickImage);
        findViewById(R.id.selectimage).setOnClickListener(this::onClickImage);
        //set text components

        vName = findViewById(R.id.name_input);
        //vName.setText(Name);

        vSurname = findViewById(R.id.surname_input);
       // vSurname.setText(Surname);

        vEmail = findViewById(R.id.email_input);
       // vEmail.setText(Email);

        vBio = findViewById(R.id.address_input);
       // vBio.setText(Address);

        getUserInfo();

    }
    @Override
    public void onStart(){
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
            // vEmail.setLinkTextColor(RED);

            vEmail.setText("");
            vEmail.setHint(R.string.errorEmail);
            vEmail.setHintTextColor(RED);
            vEmail.requestFocus();
            return;
        }
        Tools t = new Tools();
        //set popup
        android.app.AlertDialog.Builder ad = t.showPopup(this, getString(R.string.saveQuestion), "", getString(R.string.cancel));
        ad.setPositiveButton("Ok", (vi, w) -> {
            //prefs.edit().putString("name", vName.getText().toString()).apply();
            //prefs.edit().putString("surname", vSurname.getText().toString()).apply();
           // prefs.edit().putString("email", vEmail.getText().toString()).apply();
           // prefs.edit().putString("address", vBio.getText().toString()).apply();
           // prefs.edit().putBoolean("save", false).apply();

            updateUserInfo();

        });
        ad.show();
    }

    public void onStop(){

        super.onStop();

        /*if(mProfileListener!=null){
            mProfileReference.removeEventListener(mProfileListener);

        }*/
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
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        });
        ad.show();
        //-->
    }

    @Override
    //-->function activated when a request is terminated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //manage request image capture
        if (requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == RESULT_OK) {
            imagechanged=true;
            Bundle imageUri = data.getExtras();
            assert imageUri != null;
            newBitMapProfileImage = (Bitmap) imageUri.get("data");
            uriProfileImage = data.getData();
            profileImage.setImageBitmap(newBitMapProfileImage);
            //manage request image from gallery
        } else if ( requestCode==REQUEST_IMAGE_LIBRARY && resultCode == RESULT_OK) {
            imagechanged=true;
            try{
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                newBitMapProfileImage = BitmapFactory.decodeStream(imageStream);
                uriProfileImage = imageUri;
                profileImage.setImageBitmap(newBitMapProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //function used to save the image in the correct path
    private void saveImage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory,"profile.jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
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

        progressbar.setVisibility(View.VISIBLE);

        if(name.isEmpty()){
            vName.setError("Name required");
            vName.requestFocus();
            return;
        }
        if(surname.isEmpty()){
            vSurname.setError("Surname required");
            vSurname.requestFocus();
            return;
        }
        if(email.isEmpty()){
            vEmail.setError("Email required");
            vEmail.requestFocus();
            return;
        }

        StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                .child("users")
                .child(FirebaseManagement.getUser().getUid())
                .child("profileimage.jpg");
        Intent pickIntent = new Intent(this, ShowProfile.class);
        if(uriProfileImage != null){


            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                            profile.setName(name);
                            profile.setSurname(surname);
                            profile.setEmail(email);
                            profile.setDescription(bio);
                            profile.setImage(profileImageUrl);

                            FirebaseManagement.updateUserData(profile);

                            // pickIntent.putExtra(EXTRA_PROFILE_KEY,mProfile).;
                            //prefs.edit().putString(EXTRA_PROFILE_KEY,mProfile).apply();
                            // database.setPersistenceEnabled(false);
                            startActivity(pickIntent);
                            progressbar.setVisibility(View.GONE);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressbar.setVisibility(View.GONE);
                        }
                    });
        }else{
            profileImageUrl = "";
            profile.setName(name);
            profile.setSurname(surname);
            profile.setEmail(email);
            profile.setDescription(bio);
            profile.setImage(profileImageUrl);

            FirebaseManagement.updateUserData(profile);
            startActivity(pickIntent);
            progressbar.setVisibility(View.GONE);
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

                        vName.setText(profile.name);
                        vSurname.setText(profile.surname);
                        vEmail.setText(profile.email);
                        vBio.setText(profile.description);
                        URL url = null;

                        if(profile.getImage()!=null) {
                            try {
                                File localFile = File.createTempFile("images", "jpg");

                                StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                                        .child("users")
                                        .child(FirebaseManagement.getUser().getUid())
                                        .child("profileimage.jpg");

                                profileImageRef.getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                progressbar.setVisibility(View.GONE);
                                                profileImage.setVisibility(View.VISIBLE);
                                                profileImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressbar.setVisibility(View.GONE);
                                                profileImage.setVisibility(View.VISIBLE);
                                        }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            progressbar.setVisibility(View.GONE);
                            profileImage.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("loadPost:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        Toast.makeText(EditProfile.this, "Failed to load profile.",
                                Toast.LENGTH_SHORT).show();
                        // [END_EXCLUDE]

                    }
                });


        if(profile==null){
            vName.setText(getString(R.string.name));
            vName.append(" " + getString(R.string.surname));
            vEmail.setText(getString(R.string.email));
            vBio.setText(getString(R.string.description));
        }
    }

}