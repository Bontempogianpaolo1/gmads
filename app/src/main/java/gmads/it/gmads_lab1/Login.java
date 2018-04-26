package gmads.it.gmads_lab1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;
import java.util.Objects;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);//prima di aprire il login di firebase immagine di home come sfondo
        login();
    }

    public void login(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //controllo se è già loggato
        if (auth.getCurrentUser() != null) {
            // already signed in
            //se è gia loggato invio alla classe home uid e chiudo l'attività
            FirebaseManagement.loginUser();
            Intent intent = new Intent(Login.this, Home.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString("my_token", FirebaseManagement.getUser().getUid()).apply();
            startActivity(intent);
            finish();
        } else {
            //se non è loggato mi loggo attraverso l'attività di firebase
            /*
            TODO assegnare login a facebook e google
             */
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.FacebookBuilder().build()
                            ))
                            .build(),
                    RC_SIGN_IN);
            // not signed in
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ritorno dall'attività di firebase e se si è loggato vado a home
        if (resultCode == RESULT_OK) {
            FirebaseManagement.createUser(getApplicationContext(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            assert idpResponse != null;
            prefs.edit().putString("my_token",FirebaseManagement.getUser().getUid()).apply();
            startActivity(new Intent(this, Home.class));
        }
    }


}
