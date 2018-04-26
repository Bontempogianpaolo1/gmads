package gmads.it.gmads_lab1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Objects;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    EditText emailView;
    EditText pwdView;
    Button loginButton;
    Button registerButton;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login();

    }
    public void login(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            FirebaseManagement.loginUser();
            Intent intent = new Intent(Login.this, Home.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString("my_token", FirebaseManagement.getUser().getUid());
            startActivity(intent);
            finish();
        } else {
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
        if (resultCode == RESULT_OK) {
            FirebaseManagement.createUser(getApplicationContext(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            assert idpResponse != null;
            prefs.edit().putString("my_token",FirebaseManagement.getUser().getUid()).apply();
            startActivity(new Intent(this, Home.class));

        }
    }
    private void loginUser(){

        String email = emailView.getText().toString().trim();
        String pwd = pwdView.getText().toString().trim();

        if(email.isEmpty()){
            emailView.setError("Email is required");
            emailView.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailView.setError("Please enter a valid email");
            emailView.requestFocus();
            return;
        }

        if(pwd.isEmpty()){
            pwdView.setError("Password is required");
            pwdView.requestFocus();
            return;
        }

        if(pwd.length() < 6){
            pwdView.setError("Password must be at least 6 characters long");
            pwdView.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        FirebaseManagement.getAuth().signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressbar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            FirebaseManagement.loginUser();
                            Intent intent = new Intent(Login.this, ShowProfile.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUser() {
        String email = emailView.getText().toString().trim();
        String pwd = pwdView.getText().toString().trim();

        if(email.isEmpty()){
            emailView.setError("Email is required");
            emailView.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailView.setError("Please enter a valid email");
            emailView.requestFocus();
            return;
        }

        if(pwd.isEmpty()){
            pwdView.setError("Password is required");
            pwdView.requestFocus();
            return;
        }

        if(pwd.length() < 6){
            pwdView.setError("Password must be at least 6 characters long");
            pwdView.requestFocus();
            return;
        }

        progressbar.setVisibility(View.VISIBLE);

        FirebaseManagement.getAuth().createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseManagement.createUser(getApplicationContext(), email);
                progressbar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                } else {

                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(Login.this, "You are already registered", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}
