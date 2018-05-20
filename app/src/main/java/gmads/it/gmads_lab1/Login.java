package gmads.it.gmads_lab1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;
import java.util.Objects;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import gmads.it.gmads_lab1.model.Profile;
import gmads.it.gmads_lab1.service.MyFirebaseInstanceIDService;

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
            Datasource.getInstance().sincMyProfile();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            //finish();
        } else {
            //se non è loggato mi loggo attraverso l'attività di firebase
            /*
            TODO assegnare login a facebook
            */

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()
                                    ,new AuthUI.IdpConfig.FacebookBuilder().build()
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
            Intent intent = new Intent(this, ShowProfile.class);

            FirebaseManagement.getDatabase().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() == null){
                                //FirebaseManagement.createUser(getApplicationContext(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());

                                FirebaseUser User = FirebaseManagement.getUser();
                                String name[] = Objects.requireNonNull(FirebaseManagement.getUser().getDisplayName()).split(" ");
                                Profile newProfile;
                                newProfile = new Profile(User.getUid(),
                                        FirebaseManagement.getUser().getDisplayName(),
                                        "surname da togliere",
                                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),
                                        getApplicationContext().getString(R.string.bioExample));


                                FirebaseManagement.getDatabase().getReference().child("users").child(User.getUid()).setValue(newProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        MyFirebaseInstanceIDService fInstance = new MyFirebaseInstanceIDService();
                                        fInstance.addToken(FirebaseInstanceId.getInstance().getToken());
                                        startActivity(intent);
                                    }
                                });

                            }
                            else{
                                FirebaseManagement.loginUser();
                                startActivity(intent);

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}
