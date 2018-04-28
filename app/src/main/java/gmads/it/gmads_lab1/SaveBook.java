package gmads.it.gmads_lab1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SaveBook extends AppCompatActivity{

    private static final String EXTRA_ISBN ="ISBN";
    private DatabaseReference mProfileReference;
    private DatabaseReference mBooksReference;
    private StorageReference storageReference;
    private String isbn;
    private String urlimage=null;
    static final int MY_CAMERA_REQUEST_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1888;
    static final int REQUEST_IMAGE_LIBRARY = 1889;
    private ImageView bookImage;//profile image
    private Bitmap newBitMapBookImage; //temp for new image
    private SharedPreferences prefs;
    boolean imagechanged=false;
    Toolbar toolbar;
    ContextWrapper cw;
    File directory;
    String path;
    LinearLayout ll;
    EditText vTitle;
    EditText vDate;
    EditText vAuthor;
    EditText vCategories;
    EditText vDescription;
    EditText vPublisher;
    Tools t1;
    Button add;
    Book book;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_book);
        findActViews();
        t1 = new Tools();
        if (!(t1.isOnline(getApplicationContext()))){
            //rendo invisibile l'xml
            ll.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            android.app.AlertDialog.Builder ad = t1.showPopup(this, getString(R.string.noInternet), "", "");
            //tasto retry rimanda ad addbook
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onBackPressed());
            ad.setCancelable(false);
            ad.show();
        }
        setReferences();
        cw = new ContextWrapper(getApplicationContext());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        path = directory.getPath();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        boolean isRawData = getIntent().getBooleanExtra("rawData", false);
        if(!isRawData) {
            getjson(getApplicationContext(), isbn);
        } else {
            isbn = null;
        }
    }
   public void setReferences(){
       prefs = PreferenceManager.getDefaultSharedPreferences(this);
       isbn = prefs.getString(EXTRA_ISBN, null);
       if (isbn != null) {
           mProfileReference =FirebaseManagement.getDatabase().getReference()
                   .child("users")
                   .child(FirebaseManagement.getUser().getUid());
           mBooksReference = FirebaseManagement.getDatabase().getReference()
                   .child("books");
       }
    }
public void findActViews(){
    vTitle = findViewById(R.id.title);
    vDate = findViewById(R.id.data);
    vAuthor = findViewById(R.id.autore);
    vCategories = findViewById(R.id.categorie);
    vPublisher = findViewById(R.id.editore);
    vDescription = findViewById(R.id.descrizione);
    bookImage = findViewById(R.id.bookimage);
    progressBar=findViewById(R.id.progressBar);
    ll=findViewById(R.id.ll);
    add = findViewById(R.id.addphoto);
    toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle(getString(R.string.bookTitle));
    setSupportActionBar(toolbar);
    vDescription.setMovementMethod(new ScrollingMovementMethod());
    add.setOnClickListener(this::onAddPhotoClick);
}
    public void getjson(Context c,  String isbn) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=ISBN:<";
        url = url + isbn + ">";
        RequestQueue queue = Volley.newRequestQueue(c);
        ll.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {//Display the first 500 characters of the response string.
                    String title;
                    String author;
                    String publisher;
                    String publishdate;
                    String categories;
                    String description;
                    JSONObject resultObject;
                    JSONObject volumeObject;
                    JSONArray bookArray;
                    JSONObject bookObject;
                    try {
                        //piglio Json
                        resultObject = new JSONObject(response);
                        bookArray = resultObject.getJSONArray("items");
                        bookObject = bookArray.getJSONObject(0);
                        volumeObject = bookObject.getJSONObject("volumeInfo");
                        //piglio stringhe
                    }catch (Exception e){
                        volumeObject= new JSONObject();
                    }
                        try{
                            title = volumeObject.getString("title");
                            vTitle.setText(title);
                        }catch (Exception e){
                            vTitle.setText(getString(R.string.titleNotFound));
                        }
                        try{
                            author = volumeObject.getString("authors");
                            author = author.replaceAll("[\"\\[\\]]","");
                            vAuthor.setText(author);
                        }catch (Exception e){
                            vAuthor.setText(getString(R.string.authorNotFound));
                        }
                        try{
                            if(!volumeObject.isNull("publisher")&& volumeObject.has("publisher")){
                                publisher = volumeObject.getString("publisher");
                                vPublisher.setText(publisher);
                        }else{
                            vPublisher.setText(getString(R.string.publisherNotFound));
                        }
                        }catch (Exception e){
                            vPublisher.setText(getString(R.string.publisherNotFound));
                        }
                        try{
                            publishdate= volumeObject.getString("publishedDate");
                            vDate.setText(publishdate);
                        }catch (Exception e){
                            vDate.setText(getString(R.string.pDateNotFound));
                        }
                        try{
                            categories = volumeObject.getString("categories");
                            categories = categories.replaceAll("[\"\\[\\]]","");
                            vCategories.setText(categories);
                        }catch (Exception e){
                            vCategories.setText(getString(R.string.categoryNotFound));
                        }
                        try{
                            urlimage = volumeObject.getJSONObject("imageLinks").getString("thumbnail");
                        }catch (Exception e){
                            urlimage="";
                        }
                        try {
                            if(!volumeObject.isNull("description")&& volumeObject.has("description")) {
                                description = volumeObject.getString("description");
                                vDescription.setText(description);
                            }else{
                                vDescription.setText(R.string.descriptionNotFound);
                            }
                        }catch(Exception e){
                            vDescription.setText(R.string.descriptionNotFound);
                        }
                        Glide.with(this).load(urlimage).into((ImageView)findViewById(R.id.bookimage));

                        progressBar.setVisibility(View.GONE);
                        ll.setVisibility(View.VISIBLE);
                        prefs = PreferenceManager.getDefaultSharedPreferences(c);
                        book = new Book(
                                null,
                                isbn,
                                vTitle.getText().toString(),
                                "",
                                urlimage,
                                vDate.getText().toString(),
                                vAuthor.getText().toString(),
                                vCategories.getText().toString(),
                                vPublisher.getText().toString(),
                                FirebaseManagement.getUser().getUid()
                        );
                }, error -> Log.d("That didn't work!","Error: "+error));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
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
        book = new Book(
                null,
                isbn,
                vTitle.getText().toString(),
                "",
                urlimage,
                vDate.getText().toString(),
                vAuthor.getText().toString(),
                vCategories.getText().toString(),
                vPublisher.getText().toString(),
                FirebaseManagement.getUser().getUid()
        );
        //set popup
        android.app.AlertDialog.Builder ad = t.showPopup(this, getString(R.string.saveQuestion), "", getString(R.string.cancel));
        ad.setPositiveButton("Ok", (vi, w) -> {
            mBooksReference=FirebaseManagement.getDatabase().getReference().child("books");
            String bookKey = mBooksReference.push().getKey();
            mBooksReference.child(bookKey).setValue(book);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.Uploading));
            progressDialog.show();
            book.setBId(bookKey);
            mProfileReference.child("myBooks").child(bookKey).setValue(book.getIsbn())
                    .addOnFailureListener(e -> Log.v("ERR", e.getMessage()));

            if(newBitMapBookImage!=null) {
                storageReference = FirebaseManagement
                        .getStorage()
                        .getReference()
                        .child("books")
                        .child(this.book.getBId())
                        .child("personal_images")
                        .child("1.jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newBitMapBookImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask
                        .addOnFailureListener(exception -> toastMessage("Upload failed"))
                        .addOnSuccessListener(taskSnapshot -> {
                            toastMessage("Image upload successful");
                            progressDialog.dismiss();
                        });
                storageReference.putFile(Uri.fromFile(new File(path))).addOnSuccessListener(taskSnapshot -> {
                }).addOnFailureListener(e -> Log.d("error",e.toString()));
            }
            Intent pickIntent = new Intent(this, Home.class);
            prefs.edit().putString(EXTRA_ISBN, isbn).apply();
            startActivity(pickIntent);
        });
        ad.show();
    }
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //for SaveButton in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.save_book, menu);
        return true;
    }
    private void setFocusOnClick(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void onAddPhotoClick(View v) {
        //set popup
       setFocusOnClick(Objects.requireNonNull(this.getCurrentFocus()));
        v.getContext();
        android.app.AlertDialog.Builder ad=t1.showPopup(this,getString(R.string.addBookImage),getString(R.string.selectGallery),getString(R.string.selectFromCamera));
        ad.setPositiveButton(getString(R.string.selectGallery),(vi,w)->{
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, REQUEST_IMAGE_LIBRARY);
        });
        ad.setNegativeButton(getString(R.string.selectFromCamera),(vi,w)->{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
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
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
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
            newBitMapBookImage = (Bitmap) Objects.requireNonNull(imageUri).get("data");
            //bookImage.loadUrl(bitmapToUrl(newBitMapBookImage));
            bookImage.setImageBitmap(newBitMapBookImage);
           // bookImage.setImageBitmap(newBitMapBookImage);
            //manage request image from gallery
        } else if ( requestCode==REQUEST_IMAGE_LIBRARY && resultCode == RESULT_OK) {
            imagechanged=true;
            try{
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                newBitMapBookImage = BitmapFactory.decodeStream(imageStream);
               // bookImage.loadUrl(bitmapToUrl(newBitMapBookImage));
                bookImage.setImageBitmap(newBitMapBookImage);
                //bookImage.setImageBitmap(newBitMapBookImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //function used to save the image in the correct path

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
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    public String bitmapToUrl(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return "data:image/png;base64," + imgageBase64;
    }
}
