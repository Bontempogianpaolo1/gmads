package gmads.it.gmads_lab1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddReview extends AppCompatActivity {

    TextView owner,bookname;
    EditText recensione;
    Button invia;
    RatingBar rating;
    CircleImageView photo;
    public void findviews(){
        owner = findViewById(R.id.owner);
        bookname= findViewById(R.id.bookname);
        recensione= findViewById(R.id.recensione);
        invia= findViewById(R.id.invia_button);
        rating = findViewById(R.id.rating);

    }
    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recensione);
        findviews();
        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            default://caso Save

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

}
