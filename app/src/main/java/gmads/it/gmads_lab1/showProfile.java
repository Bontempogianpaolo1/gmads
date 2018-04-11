package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class showProfile extends AppCompatActivity {
    ImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String name = prefs.getString("name", getResources().getString(R.string.name));
        String surname = prefs.getString("surname", getResources().getString(R.string.surname));
        String email = prefs.getString("email", getString(R.string.description));
        String bio = prefs.getString("address", getResources().getString(R.string.description));
        //settare titolo activity nella action bar
        ActionBar gsab=getSupportActionBar();
        assert gsab != null;
        gsab.setTitle(getString(R.string.showProfile));
        profileImage = findViewById(R.id.profile_image);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        String path = directory.getPath();
        File f=new File(path,"profile.jpg");
        if(f.exists()) {
            try {
                profileImage.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(f)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        TextView vName = findViewById(R.id.name);
        TextView vEmail = findViewById(R.id.email);
        TextView vAddress = findViewById(R.id.bio);
        if(name.compareTo("")==0){
            vName.setText(getResources().getString(R.string.name));
            vName.append(" " + getResources().getString(R.string.surname));
        }else{
            vName.setText(name);
            vName.append(" " + surname);
        }
        if(email.compareTo("")==0){
            vEmail.setText(getString(R.string.emailExample));
        }else{
            vEmail.setText(email);
        }
        if(bio.compareTo("")==0){
            vAddress.setText(getResources().getString(R.string.bioExample));
        }else{
            vAddress.setText(bio);
        }
    }
    //for EditButton in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.actionbar_showp, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentMod = new Intent(this, editProfile.class);
        startActivity(intentMod);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        return true;
    }
    //
    //to close the application on back button
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}