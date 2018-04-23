package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import static android.graphics.Color.RED;

public class SaveBook extends AppCompatActivity{

    private static final String EXTRA_ISBN ="isbn";
    private static final String EXTRA_PROFILE_KEY="post_key";
    private DatabaseReference mProfileReference;
    private StorageReference storageReference;
    private ValueEventListener mProfileListener;
    FirebaseDatabase database;
    FirebaseStorage storage;
    private String user;
    private String isbn;
    static final int REQUEST_IMAGE_CAPTURE = 1888;
    static final int REQUEST_IMAGE_LIBRARY = 1889;
    private ImageView BookImage;//profile image
    private Bitmap newBitMapBookImage; //temp for new image
    private SharedPreferences prefs;
    private boolean imagechanged=false;
    Toolbar toolbar;
    ContextWrapper cw;
    File directory;
    String path;
    LinearLayout ll;
    LinearLayout l2;
    TextView vTitle;
    TextView vDate;
    TextView vAuthor;
    TextView vCategory;
    TextView vDescription;
    TextView vEditor;
    Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        isbn =prefs.getString(EXTRA_ISBN,null);
        user =prefs.getString(EXTRA_PROFILE_KEY,null);
        Tools t= new Tools();
        t.getjson(getApplicationContext(),"");
        database=FirebaseManagement.getDatabase();
        storage=FirebaseManagement.getStorage();

        if(isbn !=null) {
            mProfileReference = FirebaseDatabase.getInstance().getReference().child("books").child(isbn);
            storageReference= storage.getReference().child("books").child(isbn).child("image.jpg");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbarEditP);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cw = new ContextWrapper(getApplicationContext());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // path to /data/data/yourapp/app_data/imageDir
        directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        path = directory.getPath();
        //inizialize  layout
       /* ll= findViewById(R.id.linearLayout1);
        l2= findViewById(R.id.linearlayout2);
        ll.setOnClickListener(this::setFocusOnClick);
        l2.setOnClickListener(this::setFocusOnClick);*/
        //inizialize  user data

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //set image
        BookImage = findViewById(R.id.profile_image);
        try {
            newBitMapBookImage = BitmapFactory.decodeStream(new FileInputStream(new File(path,"book.jpg")));
            BookImage.setImageBitmap(newBitMapBookImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BookImage.setOnClickListener(this::onClickImage);
        findViewById(R.id.selectimage).setOnClickListener(this::onClickImage);
        //set text components

        vTitle = findViewById(R.id.name_input);
        //vTitle.setText(Name);

        vDate = findViewById(R.id.surname_input);
        // vDate.setText(Surname);

        vAuthor = findViewById(R.id.email_input);
        // vAuthor.setText(Email);

        vCategory = findViewById(R.id.address_input);
        // vCategory.setText(Address);

    }
    @Override
    public void onStart(){
        super.onStart();

        if(isbn !=null) {
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Profile myuser = dataSnapshot.getValue(Profile.class);
                    assert myuser != null;
                    vTitle.setText(myuser.getName());
                    vDate.setText(myuser.getSurname());
                    vAuthor.setText(myuser.getEmail());
                    vCategory.setText(myuser.getDescription());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mProfileReference.addValueEventListener(postListener);
            mProfileListener = postListener;
        }else{
            vTitle.setText("");
            vDate.setText("");
            vAuthor.setText("");
            vCategory.setText("");
        }
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
        Tools t = new Tools();
        //set popup
        android.app.AlertDialog.Builder ad = t.showPopup(this, getString(R.string.saveQuestion), "", getString(R.string.cancel));
        ad.setPositiveButton("Ok", (vi, w) -> {

            mProfileReference= database.getReference().child("books").child(isbn);
            mProfileReference.setValue(book);

            if(imagechanged) {

                saveImage(newBitMapBookImage);
                storageReference.putFile(Uri.fromFile(new File(path,"image.jpg")));
            }
            Intent pickIntent = new Intent(this, ShowProfile.class);
            // pickIntent.putExtra(EXTRA_ISBN,isbn).;
            prefs.edit().putString(EXTRA_ISBN, isbn).apply();
            // database.setPersistenceEnabled(false);
            startActivity(pickIntent);

        });
        ad.show();
    }

    public void onStop(){

        super.onStop();

        if(mProfileListener!=null){
            mProfileReference.removeEventListener(mProfileListener);

        }
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
            newBitMapBookImage = (Bitmap) imageUri.get("data");
            BookImage.setImageBitmap(newBitMapBookImage);
            //manage request image from gallery
        } else if ( requestCode==REQUEST_IMAGE_LIBRARY && resultCode == RESULT_OK) {
            imagechanged=true;
            try{
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                newBitMapBookImage = BitmapFactory.decodeStream(imageStream);
                BookImage.setImageBitmap(newBitMapBookImage);
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
}
