package gmads.it.gmads_lab1;

import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import static gmads.it.gmads_lab1.FirebaseManagement.mAuth;

public class Login extends AppCompatActivity {

    EditText emailView;
    EditText pwdView;
    Button loginButton;
    Button registerButton;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailView = findViewById(R.id.email_input);
        pwdView = findViewById(R.id.pwd_input);
        loginButton = findViewById(R.id.login_b);
        registerButton = findViewById(R.id.register_b);
        progressbar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(view -> loginUser());
        registerButton.setOnClickListener(view -> registerUser());
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

        FirebaseManagement.mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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

        FirebaseManagement.mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
