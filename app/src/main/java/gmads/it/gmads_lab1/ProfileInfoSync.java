package gmads.it.gmads_lab1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by giorgiocrepaldi on 24/04/18.
 */

public class ProfileInfoSync {
    public static ProfileInfoSync pISInstance = new ProfileInfoSync();

    public Bitmap profileImage = null;
    public Profile myProfile = null;

    public ProfileInfoSync(){

    }

    public void loadProfileInfo(){
        FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myProfile = dataSnapshot.getValue(Profile.class);

                        if(myProfile.getImage()!=null) {
                            try {
                                File localFile = File.createTempFile("images", "jpg");

                                StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                                        .child("users")
                                        .child(FirebaseManagement.getUser().getUid())
                                        .child("profileimage.jpg");

                                profileImageRef.getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



}
