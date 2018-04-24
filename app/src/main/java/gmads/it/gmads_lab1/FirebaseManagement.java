package gmads.it.gmads_lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseManagement {

    private static volatile FirebaseManagement firebaseManagementInstance = new FirebaseManagement();
    public static FirebaseAuth mAuth;
    public static FirebaseDatabase mDatabase;
    public static FirebaseStorage mStorage;
    public static FirebaseUser mUser;

    private static FirebaseStorage storage ;
    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
            mStorage = FirebaseStorage.getInstance();
            mAuth = FirebaseAuth.getInstance();
        }
        return mDatabase;
    }
    public static FirebaseStorage getStorage(){

        if(storage==null){

            storage= FirebaseStorage.getInstance();

        }
        return storage;
    }

    //private constructor
    public FirebaseManagement() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseManagement getInstance(){

        return firebaseManagementInstance;
    }

    public void registerUser(){

    }

    public void writeonFire(){

       FirebaseDatabase database = FirebaseDatabase.getInstance();
       DatabaseReference myRef = database.getReference("message");
       myRef.setValue("Hello, World!");

    }

    public void readfromFire(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ciao");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: ");
            }
        });

    }

    public static void updateUserData(Profile profile){
        if(mUser != null) {
            mDatabase.getReference().child("users").child(mUser.getUid()).setValue(profile);
        }

    }

    public static void updateUserImage(Bitmap image){

    }

    public static void createUser(Context context, String email){
        FirebaseManagement.mUser = mAuth.getCurrentUser();
        Profile newProfile = new Profile(context.getString(R.string.name), context.getString(R.string.surname), email, context.getString(R.string.description), null);
        FirebaseManagement.mDatabase.getReference().child("users").child(FirebaseManagement.mUser.getUid()).setValue(newProfile);
    }

    public static void loginUser(){
        FirebaseManagement.mUser = mAuth.getCurrentUser();
    }

}
