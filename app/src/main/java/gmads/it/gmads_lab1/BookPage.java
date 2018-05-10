package gmads.it.gmads_lab1;

import android.content.ContextWrapper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookPage extends AppCompatActivity {

    ContextWrapper cw;
    Toolbar toolbar;
    String bookId;
    Book book;
    CollapsingToolbarLayout vTitle;
    TextView vAuthor;
    TextView vRating;
    TextView vCategory;
    TextView distance;
    ViewPagerAdapter bookImageAdapter;
    ViewPager viewPager;
    List<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_page);

        toolbar = findViewById(R.id.toolbar);
        findViews();
        getBookInfo();

        viewPager= findViewById(R.id.book_image);
        bookImageAdapter= new ViewPagerAdapter(BookPage.this,images);
        viewPager.setAdapter(bookImageAdapter);

        cw = new ContextWrapper(getApplicationContext());
        //setSupportActionBar(toolbar);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void findViews(){
        vTitle = findViewById(R.id.book_title);
        vAuthor = findViewById(R.id.book_author);
        vRating = findViewById(R.id.book_rating);
        vCategory = findViewById(R.id.book_category);
        distance=findViewById(R.id.distance);
    }

    public void getBookInfo(){
        getIntent().getStringExtra("book_id");
        bookId = getIntent().getExtras().getString("book_id");
        FirebaseManagement.getDatabase().getReference().child("books").child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        book = dataSnapshot.getValue(Book.class);
                        vTitle.setTitle(book.getTitle());
                        vAuthor.setText(book.getAuthor().get(0));
                        vRating.setText(Double.toString(book.getAvgRating()));
                        vCategory.setText(book.getCategories().get(0));
                       // distance.setText(String.valueOf(book.getDistance()/1000));
                       // distance.append(" Km");
                        bookImageAdapter.clearUrl();
                        bookImageAdapter.addUrl(book.getUrlimage());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
