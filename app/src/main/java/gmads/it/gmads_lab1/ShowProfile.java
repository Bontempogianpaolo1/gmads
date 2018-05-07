package gmads.it.gmads_lab1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class ShowProfile extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView coverImage;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    //private SimpleDraweeView avatar;
    private ImageView avatar;

    private void findViews() {
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        coverImage = (ImageView) findViewById(R.id.imageview_placeholder);
        framelayoutTitle = (FrameLayout) findViewById(R.id.framelayout_title);
        linearlayoutTitle = (LinearLayout) findViewById(R.id.linearlayout_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textviewTitle = (TextView) findViewById(R.id.textview_title);
        //avatar = (SimpleDraweeView) findViewById(R.id.avatar);
        avatar = findViewById(R.id.avatar);
        avatar.setImageDrawable(getDrawable(R.drawable.default_picture));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.content_show_profile);
        findViews();

        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);

        setSupportActionBar(toolbar);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);

        //set avatar and cover
        avatar.setImageResource(R.drawable.default_picture);
        coverImage.setImageResource(R.drawable.cover);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_showp, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}

/*public class ShowProfile extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    //nav
    TextView navName;
    TextView navMail;
    ImageView navImage;
    NavigationView navigationView;
    DrawerLayout drawer;
    //view della attività
    ImageView profileImage;
    ProgressBar progressbar;
    Toolbar toolbar;
    View headerView;
    TextView vName;
    TextView vEmail;
    TextView vBio;
    private Profile profile;
    private Bitmap myProfileBitImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        profile = Datasource.getInstance().getMyProfile();
        myProfileBitImage = Datasource.getInstance().getMyProfileBitImage();
        setNavViews();
        setActViews();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    public void setActViews(){
        profileImage = findViewById(R.id.profile_image);
        toolbar =  findViewById(R.id.toolbar);
        vName = findViewById(R.id.name);
        vEmail = findViewById(R.id.email);
        vBio = findViewById(R.id.bio);
        progressbar = findViewById(R.id.progressBar);
        toolbar.setTitle(getString(R.string.showProfile));
        setSupportActionBar(toolbar);
        vBio.setMovementMethod(new ScrollingMovementMethod());

        profileImage.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);

        if(profile!=null) {
            vName.setText(profile.getName());
            vName.append(" " + profile.getSurname());
            vEmail.setText(profile.getEmail());
            vBio.setText(profile.getDescription());

            if (myProfileBitImage != null) {
                profileImage.setImageBitmap(myProfileBitImage);
            } else {
                profileImage.setImageDrawable(getDrawable(R.drawable.default_profile));
            }
        }

        progressbar.setVisibility(View.GONE);
        profileImage.setVisibility(View.VISIBLE);
    }
    public void setNavViews(){
        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navName =  headerView.findViewById(R.id.navName);
        navMail =  headerView.findViewById(R.id.navMail);
        navImage =  headerView.findViewById(R.id.navImage);
        headerView.setBackgroundResource(R.color.colorPrimaryDark);

        if(profile!=null) {
            navName.setText(profile.getName());
            navName.append(" " + profile.getSurname());
            navMail.setText(profile.getEmail());

            if (myProfileBitImage != null) {
                navImage.setImageBitmap(myProfileBitImage);
            } else {
                navImage.setImageDrawable(getDrawable(R.drawable.default_profile));
            }
        }
    }
protected void onStart(){
        super.onStart();
        getUserInfo();
}
    //for EditButton in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.actionbar_showp, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentMod = new Intent(this, EditProfile.class);
        startActivity(intentMod);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        return true;
    }

    //to close the application on back button
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_showProfile) {
            //deve solo chiudersi la navbar
            drawer.closeDrawers();
            return true;
        } else if (id == R.id.nav_addBook) {
            Intent intentMod = new Intent(this, AddBook.class);
            startActivity(intentMod);
            finish();
            return true;
        } else if (id == R.id.nav_home) {
            Intent intentMod = new Intent(this, Home.class);
            startActivity(intentMod);
            finish();
            return true;
        }else if(id == R.id.nav_logout){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(v->{
                startActivity(new Intent(this,Login.class));
                finish();
            });
            return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserInfo(){
        progressbar.setVisibility(View.VISIBLE);
        profileImage.setVisibility(View.GONE);
        FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profile = dataSnapshot.getValue(Profile.class);
                        if (profile != null) {
                            vName.setText(profile.getName());
                            vName.append(" " + profile.getSurname());
                            navName.setText(profile.getName());
                            navName.append(" " + profile.getSurname());
                            vEmail.setText(profile.getEmail());
                            navMail.setText(profile.getEmail());
                            vBio.setText(profile.getDescription());
                            if (profile.getImage() != null) {
                                try {
                                    File localFile = File.createTempFile("images", "jpg");
                                    StorageReference profileImageRef =
                                            FirebaseManagement
                                                    .getStorage()
                                                    .getReference()
                                                    .child("users")
                                                    .child(FirebaseManagement.getUser().getUid())
                                                    .child("profileimage.jpg");

                                    profileImageRef.getFile(localFile)
                                            .addOnSuccessListener(taskSnapshot -> {
                                                progressbar.setVisibility(View.GONE);
                                                profileImage.setVisibility(View.VISIBLE);
                                                profileImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));
                                                navImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));
                                            }).addOnFailureListener(e -> {
                                        progressbar.setVisibility(View.GONE);
                                        profileImage.setVisibility(View.VISIBLE);
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                progressbar.setVisibility(View.GONE);
                                profileImage.setVisibility(View.VISIBLE);
                                navImage.setImageDrawable(getDrawable(R.drawable.default_profile));
                            }
                        }else{
                            vName.setText(getString(R.string.name));
                            vName.append(" " + getString(R.string.surname));
                            navName.setText(getString(R.string.name));
                            navName.append(" " + getString(R.string.surname));
                            vEmail.setText(getString(R.string.email));
                            navMail.setText(getString(R.string.email));
                            vBio.setText(getString(R.string.description));
                            progressbar.setVisibility(View.GONE);
                            profileImage.setVisibility(View.VISIBLE);
                            navImage.setImageDrawable(getDrawable(R.drawable.default_profile));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });

    }
}*/