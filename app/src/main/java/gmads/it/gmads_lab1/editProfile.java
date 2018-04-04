package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.inputmethodservice.InputMethodService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;
import android.view.WindowManager;

public class editProfile extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1888;

    private String Name;
    private String Surname;
    private String Email;
    private String Address;
    private Context context;
    private ImageView profileImage;

    private String mCurrentPhotoPath;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context = getApplicationContext();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String path = directory.getPath();

        Button s = findViewById(R.id.save_profile);
        s.setOnClickListener(v -> onSaveClick(v, prefs));

        Button c = findViewById(R.id.reset_profile);
        c.setOnClickListener(v -> onResetClick(v, prefs));
        LinearLayout ll= findViewById(R.id.linearLayout1);
        LinearLayout l2= findViewById(R.id.linearlayout2);
        ll.setOnClickListener(v->setFocusOnClick(v));
        l2.setOnClickListener(v->setFocusOnClick(v));
        Name = prefs.getString("name", "es: Giorgio");
        Surname = prefs.getString("surname", "es: Crepaldi");
        Email = prefs.getString("email", "es: example@gmail.com");
        Address = prefs.getString("address", "es: c.so Francia 47");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //ImageView profileImage = findViewById(R.id.profile_image);

        profileImage = findViewById(R.id.profile_image);

        loadImage(path);

        profileImage.setOnClickListener(v -> onClickImage(v));

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

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
    private void setFocusOnClick(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }
    private void onClickImage(View v) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        /*ImageGetter imgGet = new ImageGetter(this, context);

        imgGet.CapturePhoto("UserProfileImage");*/

        /*new ImageSaver(context).
                setFileName("myImage.png").
                setDirectoryName("images").
                save(newProfileImage);
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(photo);

            saveImage(photo);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu); //your file name
        return super.onCreateOptionsMenu(menu);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir  /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String saveImage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
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
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImage(String path)
    {

        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            profileImage = findViewById(R.id.profile_image);
            profileImage.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

}

