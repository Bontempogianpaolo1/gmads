package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class editProfile extends AppCompatActivity {

    private String Name;
    private String Surname;
    private String Email;
    private String Address;
    private Context context;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context = getApplicationContext();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Button s = findViewById(R.id.save_profile);
        s.setOnClickListener(v -> onSaveClick(v, prefs));

        Button c = findViewById(R.id.reset_profile);
        c.setOnClickListener(v -> onResetClick(v, prefs));

        Name = prefs.getString("name", "es: Giorgio");
        Surname = prefs.getString("surname", "es: Crepaldi");
        Email = prefs.getString("email", "es: example@gmail.com");
        Address = prefs.getString("address", "es: c.so Francia 47");

        //ImageView profileImage = findViewById(R.id.profile_image);

        //profileImage.setOnClickListener(v -> onClickImage(v));

        /*Bitmap bitProfileImage = new ImageSaver(context).
                setFileName("myProfile.png").
                setDirectoryName("images").
                load();

        if(bitProfileImage == null)*/
        //profileImage.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
        /*else
            profileImage.setImageBitmap(bitProfileImage);*/

        TextView vName = findViewById(R.id.name_input);
        vName.setText(Name);

        TextView vSurname = findViewById(R.id.surname_input);
        vSurname.setText(Surname);

        TextView vEmail = findViewById(R.id.email_input);
        vEmail.setText(Email);

        TextView vAddress = findViewById(R.id.address_input);
        vAddress.setText(Address);
    }

    private void onSaveClick(View v, SharedPreferences prefs) {
        EditText vName = findViewById(R.id.name_input);
        EditText vSurname = findViewById(R.id.surname_input);
        EditText vEmail = findViewById(R.id.email_input);
        EditText vAddress = findViewById(R.id.address_input);

        prefs.edit().putString("name", vName.getText().toString()).apply();
        prefs.edit().putString("surname", vSurname.getText().toString()).apply();
        prefs.edit().putString("email", vEmail.getText().toString()).apply();
        prefs.edit().putString("address", vAddress.getText().toString()).apply();

        prefs.edit().putBoolean("save", true).apply();
        Intent intentMod = new Intent(this, showProfile.class);
        startActivity(intentMod);
    }

    private void onResetClick(View v, SharedPreferences prefs) {
        createDialog();
    }

    private void resetProfileData(){
        EditText vName = findViewById(R.id.name_input);
        EditText vSurname = findViewById(R.id.surname_input);
        EditText vEmail = findViewById(R.id.email_input);
        EditText vAddress = findViewById(R.id.address_input);

        vName.setText("");
        vSurname.setText("");
        vEmail.setText("");
        vAddress.setText("");

        prefs.edit().putString("name", vName.getText().toString()).apply();
        prefs.edit().putString("surname", vSurname.getText().toString()).apply();
        prefs.edit().putString("email", vEmail.getText().toString()).apply();
        prefs.edit().putString("address", vAddress.getText().toString()).apply();
        prefs.edit().putBoolean("reset", true).apply();

        Intent intentMod = new Intent(this, showProfile.class);
        //startActivity(intentMod.setFlags(FLAG_ACTIVITY_NO_ANIMATION));
        startActivity(intentMod);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            Intent intentMod = new Intent(this, showProfile.class);
            startActivity(intentMod);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createDialog(){

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        TextView msg = new TextView(this);
        msg.setText(getResources().getString(R.string.alert1));
        //msg.setGravity(Gravity.TEXT_ALIGNMENT_CENTER);
        msg.setGravity(Gravity.CENTER);
        alertDlg.setView(msg);
        alertDlg.setCancelable(false);
        alertDlg.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetProfileData();
            }
        });
        alertDlg.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDlg.create().show();
    }

    /*private void onClickImage(View v) {

        new ImageSaver(context).
                setFileName("myImage.png").
                setDirectoryName("images").
                save(newProfileImage);
    }*/

}

