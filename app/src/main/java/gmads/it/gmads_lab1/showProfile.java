package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
        Tools t= new Tools();
        ImageManagement im= new ImageManagement();
        setContentView(R.layout.activity_show_profile);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //prefs.edit().putString("Name", findViewById(R.id.name));
        String name = prefs.getString("name", getResources().getString(R.string.name));
        String surname = prefs.getString("surname", getResources().getString(R.string.surname));
        String email = prefs.getString("email", "example@gmail.com");
        String bio = prefs.getString("address", getResources().getString(R.string.description));
        Boolean reset = prefs.getBoolean("reset", false);
        Boolean save = prefs.getBoolean("save", false);

        Context context = getApplicationContext();

        profileImage = findViewById(R.id.profile_image);

        /*Bitmap bitProfileImage = new ImageSaver(context).
                setFileName("myProfile.png").
                setDirectoryName("images").
                load();

        if(bitProfileImage == null)*/
            //profileImage.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
        /*else
            profileImage.setImageBitmap(bitProfileImage);*/

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
        }
        else{
            vName.setText(name);
            vName.append(" " + surname);
        }

        if(email.compareTo("")==0){
            vEmail.setText("example@gmail.com");
        }
        else {
            vEmail.setText(email);
        }

        if(bio.compareTo("")==0){
            vAddress.setText(getResources().getString(R.string.description));
        }
        else {
            vAddress.setText(bio);
        }

        if(reset){
        //if(name.compareTo("")==0){
            android.app.AlertDialog.Builder ab= t.showPopup(this,getResources().getString(R.string.alertResetDone),"ok","");
            //showPopupReset();
            ab.show();
            prefs.edit().putBoolean("reset", false).apply();
        }
        if(save){
            android.app.AlertDialog.Builder ab= t.showPopup(this,getResources().getString(R.string.alertSave),"ok","");
            ab.show();
            //showPopupSave();
            prefs.edit().putBoolean("save", false).apply();
        }
    }

    //for EditButton in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.actionbar1, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentMod = new Intent(this, editProfile.class);
        startActivity(intentMod);
        return true;
    }
    //
    private void loadImage(String path)
    {
        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            profileImage = findViewById(R.id.profile_image);
            profileImage.setImageBitmap(b);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //per uscire dall'app quando si preme back
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

