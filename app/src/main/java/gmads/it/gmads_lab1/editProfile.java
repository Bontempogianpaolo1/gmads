package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

        Button b = findViewById(R.id.save_profile);

        b.setOnClickListener(v -> onSaveClick(v, prefs));

        Name = prefs.getString("name", "Giorgio");
        Surname = prefs.getString("surname", "Crepaldi");
        Email = prefs.getString("email", "example@gmail.com");
        Address = prefs.getString("address", "c.so Francia 47");

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

        Intent intentMod = new Intent(this, showProfile.class);
        startActivity(intentMod);

    }

    /*private void onClickImage(View v) {



        new ImageSaver(context).
                setFileName("myImage.png").
                setDirectoryName("images").
                save(newProfileImage);
    }*/

}

