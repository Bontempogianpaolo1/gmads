package gmads.it.gmads_lab1;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;


import java.util.Objects;

import gmads.it.gmads_lab1.model.Profile;
import gmads.it.gmads_lab1.service.MyFirebaseInstanceIDService;

public class FirebaseManagement {

    private static volatile FirebaseManagement firebaseManagementInstance = new FirebaseManagement();
    private static FirebaseAuth Auth;
    private static FirebaseDatabase Database;
    private static FirebaseStorage Storage;
    private static FirebaseUser User;
    private static FirebaseStorage storage;

    public static FirebaseAuth getAuth() {
        return Auth;
    }

    public static FirebaseUser getUser() {
        if(User==null){
            User=Auth.getCurrentUser();
        }
        return User;
    }

    public static FirebaseStorage getStorage(){
        if(storage==null){
            storage= FirebaseStorage.getInstance();
        }
        return storage;
    }

    public static FirebaseDatabase getDatabase(){
        if(Database ==null){
            Database =FirebaseDatabase.getInstance();
            Database.setPersistenceEnabled(true);
            return Database;
        }
        else return Database;
}
    //private constructor
    public FirebaseManagement() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Database = FirebaseDatabase.getInstance();
        Database.setPersistenceEnabled(true);
        Auth = FirebaseAuth.getInstance();
        Storage = FirebaseStorage.getInstance();
    }

    public static Task<Void> updateUserData( Profile profile){
        if(User != null) {
            return Database.getReference().child("users").child(User.getUid()).setValue(profile);
        }
        return null;
    }

    public static void createUser(Context context, String email){
        User = Auth.getCurrentUser();
        String name[] = Objects.requireNonNull(getUser().getDisplayName()).split(" ");
        Profile newProfile;
        newProfile = new Profile(User.getUid(),getUser().getDisplayName(),"surname da togliere", email, context.getString(R.string.bioExample));


        Database.getReference().child("users").child(User.getUid()).setValue(newProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                MyFirebaseInstanceIDService fInstance = new MyFirebaseInstanceIDService();

                fInstance.addToken(FirebaseInstanceId.getInstance().getToken());
            }
        });

        /*
        LocationProvider locationProvider = new LocationProvider();
        Location loc = locationProvider.getLocation(context);

        if(loc != null) {
            DatabaseReference ref = Database.getReference().child("geofire").child(User.getUid());
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation("location", new GeoLocation(loc.getLatitude(), loc.getLongitude()));
        }
        */
    }

    public static void loginUser(){
        User = Auth.getCurrentUser();
        //ProfileInfoSync.pISInstance.loadProfileInfo();
    }
}
