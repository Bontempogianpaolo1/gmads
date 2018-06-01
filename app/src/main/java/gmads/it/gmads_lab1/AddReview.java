package gmads.it.gmads_lab1;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gmads.it.gmads_lab1.Chat.glide.GlideApp;
import gmads.it.gmads_lab1.model.Profile;
import gmads.it.gmads_lab1.model.Request;
import gmads.it.gmads_lab1.model.Review;


public class AddReview extends AppCompatActivity {

    TextView owner,bookname;
    EditText recensione;
    Button invia;
    Profile profile;
    RatingBar rating;
    CircleImageView photo;
    String id;
    Toolbar toolbar;

    public void findviews(){
        owner = findViewById(R.id.owner);
        bookname= findViewById(R.id.bookname);
        recensione= findViewById(R.id.recensione);
        invia= findViewById(R.id.invia_button);
        rating = findViewById(R.id.rating);
        toolbar = findViewById(R.id.toolbar);

        photo=findViewById(R.id.photo);
    }
    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recensione);
        findviews();
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.review);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        id= getIntent().getStringExtra("userid");
        String name=getIntent().getStringExtra("bookname");
        bookname.setText(name);
        invia.setOnClickListener(v -> {
            Review r= new Review(FirebaseManagement.getUser().getDisplayName(),recensione.getText().toString(),rating.getRating());
            List<Review> reviews=profile.getReviews();
            reviews.add(r);
            profile.setReviews(reviews);
            FirebaseManagement
                    .getDatabase()
                    .getReference()
                    .child("users")
                    .child(id)
                    .setValue(profile)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(),R.string.messenger_send_button_text,Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),RequestActivity.class));
                    });

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getUserinfo();
    }

    private void getUserinfo() {
        photo.setImageDrawable(getDrawable(R.drawable.default_picture));
        FirebaseManagement.getDatabase().getReference().child("users").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot ) {
                        profile = dataSnapshot.getValue(Profile.class);
                        if(profile!=null){
                            owner.setText(profile.getName());
                            StorageReference profileImageRef =
                                    FirebaseManagement
                                            .getStorage()
                                            .getReference()
                                            .child("users")
                                            .child(profile.getId())
                                            .child("profileimage.jpg");
                            try {
                                File localFile = File.createTempFile("images", "jpg");
                                profileImageRef.getFile(localFile)
                                        .addOnSuccessListener(taskSnapshot ->{
                                            photo.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));
                                        })
                                        .addOnFailureListener(e -> {
                                                    Log.d("ERROR", e.toString());
                                                    photo.setImageDrawable(getDrawable(R.drawable.default_picture));
                                        }
                                        );
                            }catch (Exception e){

                            }


                        }
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError ) {

                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
