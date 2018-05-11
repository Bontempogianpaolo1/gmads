package gmads.it.gmads_lab1;

import android.content.Context;
import android.location.Location;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


import java.util.Objects;

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

    public static void updateUserData(Profile profile){
        if(User != null) {
            Database.getReference().child("users").child(User.getUid()).setValue(profile);
        }
    }

    public static void createUser(Context context, String email){
        User = Auth.getCurrentUser();
        String name[] = Objects.requireNonNull(getUser().getDisplayName()).split(" ");
        Profile newProfile;

        if(name[0]!=null && name[1]!=null) {
            newProfile = new Profile(name[0], name[1], email, context.getString(R.string.bioExample));
        } else {
            newProfile = new Profile(context.getString(R.string.name), context.getString(R.string.surname), email, context.getString(R.string.bioExample));
        }

        Database.getReference().child("users").child(User.getUid()).setValue(newProfile);

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
