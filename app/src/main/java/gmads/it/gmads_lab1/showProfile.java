package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class showProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_profile);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //prefs.edit().putString("Name", findViewById(R.id.name));
        String name = prefs.getString("name", getResources().getString(R.string.name));
        String surname = prefs.getString("surname", getResources().getString(R.string.surname));
        String email = prefs.getString("email", "example@gmail.com");
        String bio = prefs.getString("address", getResources().getString(R.string.description));

        Context context = getApplicationContext();

        ImageView profileImage = findViewById(R.id.profile_image);

        /*Bitmap bitProfileImage = new ImageSaver(context).
                setFileName("myProfile.png").
                setDirectoryName("images").
                load();

        if(bitProfileImage == null)*/
            profileImage.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
        /*else
            profileImage.setImageBitmap(bitProfileImage);*/

        TextView vName = findViewById(R.id.name);
        vName.setText(name);
        vName.append(" " + surname);

        TextView vEmail = findViewById(R.id.email);
        vEmail.setText(email);

        TextView vAddress = findViewById(R.id.bio);
        //vAddress.setText(getResources().getString(R.string.address) + ": " + address);
        vAddress.setText(bio);

        ImageButton modProfileButton = findViewById(R.id.mod_profile_button);

        modProfileButton.setOnClickListener(this::onModProfileClick);

    }

    private void onModProfileClick(View v) {
        Intent intentMod = new Intent(this, editProfile.class);
        startActivity(intentMod);
    }

}

