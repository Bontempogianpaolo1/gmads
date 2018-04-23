package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
    private WebView bookImage;//profile image
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
    TextView vCategories;
    TextView vDescription;
    TextView vPublisher;
    Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_book);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //isbn = prefs.getString(EXTRA_ISBN,null);
        isbn = "9788886982405";
        user = prefs.getString(EXTRA_PROFILE_KEY,null);
        database=FirebaseManagement.getDatabase();
        storage=FirebaseManagement.getStorage();

        if(isbn !=null) {
            mProfileReference = FirebaseDatabase.getInstance().getReference().child("books").child(isbn);
            storageReference= storage.getReference().child("books").child(isbn).child("image.jpg");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbarSaveBook);
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
        bookImage = findViewById(R.id.bookimage);
        vTitle = findViewById(R.id.title);
        vDate = findViewById(R.id.dataPubblicazione);
        vAuthor = findViewById(R.id.autore);
        vCategories = findViewById(R.id.categorie);
        vPublisher= findViewById(R.id.editore);
        vDescription=findViewById(R.id.descrizione);
    }

    public void getjson(Context c,  String isbn) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=ISBN:<";
        url = url + isbn + ">";
        RequestQueue queue = Volley.newRequestQueue(c);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //setto tutto con stringhe di default
                        String title = getString(R.string.notFound);
                        String author;
                        String publisher;
                        String publishdate;
                        String categories;
                        String urlimage;
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
                                vTitle.setText(getString(R.string.notFound));
                            }
                            try{
                                author = volumeObject.getString("authors");
                                author = author.replaceAll("[\"\\[\\]]","");
                                vAuthor.setText(author);
                            }catch (Exception e){
                                vAuthor.setText(getString(R.string.notFound));
                            }
                            try{

                            if(!volumeObject.isNull("publisher")&& volumeObject.has("publisher")){
                                publisher = volumeObject.getString("publisher");
                                vPublisher.setText(publisher);
                            }else{
                                vPublisher.setText(getString(R.string.notFound));
                            }
                            }catch (Exception e){
                                vPublisher.setText(getString(R.string.notFound));
                            }
                            try{
                                publishdate= volumeObject.getString("publishedDate");
                                vDate.setText(publishdate);
                            }catch (Exception e){
                                vDate.setText(getString(R.string.notFound));
                            }
                            try{
                                categories = volumeObject.getString("categories");
                                categories = categories.replaceAll("[\"\\[\\]]","");
                                vCategories.setText(categories);
                            }catch (Exception e){
                                vCategories.setText(getString(R.string.notFound));
                            }

                            try{
                                urlimage = "https://process.filestackapi.com/AhTgLagciQByzXpFGRI0Az/resize=width:128,height:200/"+volumeObject.getJSONObject("imageLinks").getString("thumbnail");
                            }catch (Exception e){
                                urlimage="";
                            }
                            try {
                                if(!volumeObject.isNull("description")&& volumeObject.has("description")) {
                                    description = volumeObject.getString("description");
                                    vDescription.setText(description);
                                }else{
                                    vDescription.setText(R.string.notFound);
                                }
                            }catch(Exception e){
                                vDescription.setText(R.string.notFound);
                            }

                            bookImage.loadUrl(urlimage);

                            prefs = PreferenceManager.getDefaultSharedPreferences(c);
                            String owner = prefs.getString("post_key",null);
                            book = new Book(isbn, (String)vTitle.getText(), "", urlimage,(String) vDate.getText(),(String) vAuthor.getText(),(String)vCategories.getText(),(String)vPublisher.getText(),"");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!","Error: "+error);
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onStart(){
        super.onStart();
        getjson(getApplicationContext(), isbn);
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

           // bookImage.setImageBitmap(newBitMapBookImage);
            //manage request image from gallery
        } else if ( requestCode==REQUEST_IMAGE_LIBRARY && resultCode == RESULT_OK) {
            imagechanged=true;
            try{
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                newBitMapBookImage = BitmapFactory.decodeStream(imageStream);
                //bookImage.setImageBitmap(newBitMapBookImage);
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

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
