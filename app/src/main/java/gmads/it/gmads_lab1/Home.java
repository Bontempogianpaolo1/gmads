package gmads.it.gmads_lab1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.algolia.search.saas.AbstractQuery;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import gmads.it.gmads_lab1.Map.main.MapActivity;
import gmads.it.gmads_lab1.model.Book;
import gmads.it.gmads_lab1.model.Profile;
import gmads.it.gmads_lab1.fragments.Home_1;

public class  Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private List<Book> books;
    SearchView searchview;
    Client algoClient;
    Index algoIndex;
    TextView navName;
    TextView navMail;
    ImageView navImage;
    RecyclerView recyclerView;
    String query="";
    String searchquery ="";
    NavigationView navigationView;
    DrawerLayout drawer;
    private Profile profile;
    private Bitmap myProfileBitImage;
    View headerView;
    Home_1 tab1= new Home_1();
    Home_1 categ1= new Home_1();
    Home_1 categ2= new Home_1();
    Tools tools;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView notfound = findViewById(R.id.not_found);
        TextView tnf = findViewById(R.id.textnotfound);
        tools = new Tools();

        setSupportActionBar(toolbar);
        setNavViews();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //SearchView mSearchView = (SearchView) findViewById(R.id.searchView); // initiate a search view
        //mSearchView.attachNavigationDrawerToMenuButton(findViewById(R.id.drawer_layout));
        //mSearchView.setIconifiedByDefault(false);  // set the default or resting state of the search field
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        initCollapsingToolbar();
        /*bookList = new ArrayList<>();
        adapter = new BookAdapter(this, bookList);*/
        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(adapter);
//

        ViewPager pager= findViewById(R.id.viewPager);
        FragmentViewPagerAdapter vpadapter= new FragmentViewPagerAdapter(getSupportFragmentManager());
        vpadapter.addFragment(tab1);
       // vpadapter.addFragment(categ1);
        /*
        TODO riempire frammenti
         */
       // vpadapter.addFragment(categ2);
        //vpadapter.addFragment(categ2);
       // vpadapter.addFragment(categ2);
        pager.setAdapter(vpadapter);
        TabLayout tableLayout= findViewById(R.id.tabs);
        tableLayout.setupWithViewPager(pager);
        tableLayout.getTabAt(0).setText(getString(R.string.tab1));
        /*
        todo settare titoli
         */
        //tableLayout.getTabAt(1).setText(getString(R.string.tab2));
        //tableLayout.getTabAt(2).setText(getString(R.string.tab3));
        //tableLayout.getTabAt(3).setText("thriller");
        //tableLayout.getTabAt(4).setText("drama");
        progressbar = findViewById(R.id.progress_bar);
//
        //era per mettere foto libri nell appbar, ma l'abbiamo messa come sfondo per ora
        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        getUserInfo();

    }

    @Override
    protected void onResume() {
        ImageView notfound = findViewById(R.id.not_found);
        TextView tnf = findViewById(R.id.textnotfound);
        super.onResume();
        progressbar.setVisibility(View.GONE);
        notfound.setVisibility(View.GONE);
        tnf.setVisibility(View.GONE);
        /*
        if(searchview != null) {
            if(searchview.getQuery().toString().length() > 0){
                OngoingSearch(searchview.getQuery().toString());
            }
        }
        */
    }

    boolean OngoingSearch(String text) {
        progressbar.setVisibility(View.VISIBLE);
        books.clear();
        tab1.getAdapter().setbooks(books);
        ImageView notfound = findViewById(R.id.not_found);
        TextView tnf = findViewById(R.id.textnotfound);

        query = text;
        Query query = new Query(text)
                .setAroundLatLng(new AbstractQuery.LatLng(profile.getLat(), profile.getLng())).setGetRankingInfo(true);

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e==null){
                SearchResultsJsonParser search= new SearchResultsJsonParser();
                Log.d("lista",jsonObject.toString());
                books= search.parseResults(jsonObject);
                List<Book> books2= new ArrayList<>();
                for (Book b : books) {
                    if (b.getOwner().equals(FirebaseManagement.getUser().getUid())) {
                        books2.add(b);
                    }
                }
                for(Book b: books2){
                    books.remove(b);
                }
            }
            tab1.updateData(books);
            progressbar.setVisibility(View.GONE);
        });
        return true;
    }

    /*@Override
    public boolean onQueryTextSubmit(String query) {
        // do something on text submit
        //parte la query di gogo
        return false;
    }*/

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_showProfile) {
            Intent intentMod = new Intent(this, ShowProfile.class);
            startActivity(intentMod);
            finish();
            return true;
        } else if (id == R.id.nav_addBook) {
            Intent intentMod = new Intent(this, AddBook.class);
            startActivity(intentMod);
            finish();
            return true;
        } else if (id == R.id.nav_home) {
            //deve solo chiudersi la navbar
            drawer.closeDrawers();
            //Intent intent = new Intent(this, Home.class);
            //startActivity(intent);
            return true;
        } else if (id == R.id.nav_chat){
            Intent intent = new Intent(this, ChatList.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.nav_logout){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(v->{
                startActivity(new Intent(this,Login.class));
                finish();
            });
            return true;
        }else if(id == R.id.nav_mylibrary){
            startActivity(new Intent(this,MyLibrary.class));
            finish();

            return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem m= menu.findItem(R.id.search);

        searchview = (android.widget.SearchView)m.getActionView();
        searchview.setIconified(false);
        searchview.setFocusable(true);
        m.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                item.getActionView().requestFocus();
                ((InputMethodManager) Objects.requireNonNull(getSystemService(Context.INPUT_METHOD_SERVICE))).toggleSoftInput(0, 0);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                ((InputMethodManager) Objects.requireNonNull(getSystemService(Context.INPUT_METHOD_SERVICE))).hideSoftInputFromWindow(item.getActionView().getWindowToken(), 0);
                return true;
            }
        });

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit( String text ) {
                return false;
            }

            @Override
            public boolean onQueryTextChange( String newText ) {
                progressbar.setVisibility(View.VISIBLE);
                books.clear();
                tab1.getAdapter().setbooks(books);
                ImageView notfound = findViewById(R.id.not_found);
                TextView tnf = findViewById(R.id.textnotfound);

                query = newText;
                Query query = new Query(newText)
                        .setAroundLatLng(new AbstractQuery.LatLng(profile.getLat(), profile.getLng())).setGetRankingInfo(true);

                algoIndex.searchAsync(query, ( jsonObject, e ) -> {
                    if(e==null){
                        SearchResultsJsonParser search= new SearchResultsJsonParser();
                        Log.d("lista",jsonObject.toString());
                        books= search.parseResults(jsonObject);
                        List<Book> books2= new ArrayList<>();
                        for (Book b : books) {
                            if (b.getOwner().equals(FirebaseManagement.getUser().getUid())) {
                                books2.add(b);
                            }
                        }
                        for(Book b: books2){
                            books.remove(b);
                        }
                    }
                    tab1.getAdapter().setbooks(books);
                    progressbar.setVisibility(View.GONE);
                });
                return true;
            }
        });
        return true;
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Home");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public void mapcreate( View view ) {
        if(tab1.getAdapter().getItemCount()>0) {
            Intent intentMod = new Intent(this, MapActivity.class);
            intentMod.putExtra("query", query);
            intentMod.putExtra("lat", profile.getLat());
            intentMod.putExtra("lng", profile.getLng());
            startActivity(intentMod);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        }
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
            //navName.append(" " + profile.getSurname());
            navMail.setText(profile.getEmail());

            if (myProfileBitImage != null) {
                navImage.setImageBitmap(myProfileBitImage);
            } else {
                navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
            }
        }
    }

    private void getUserInfo(){
        progressbar.setVisibility(View.VISIBLE);
        //avatar.setVisibility(View.GONE);
        FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profile = dataSnapshot.getValue(Profile.class);
                        if (profile != null) {
                            if(profile.getCAP()==null || profile.getCAP().length()==0){
                                Intent i=new Intent(getApplicationContext(), EditProfile.class);
                                startActivity(i);
                            }
                            navName.setText(profile.getName());
                            navMail.setText(profile.getEmail());
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
                                            .addOnSuccessListener(taskSnapshot -> navImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath())))
                                            .addOnFailureListener(e -> Log.d("ERROR",e.toString()));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
                            }

                            getStartingHomeBooks();
                        }else{
                            Intent i=new Intent(getApplicationContext(), EditProfile.class);
                            startActivity(i);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });

    }

    private void getStartingHomeBooks(){

        progressbar.setVisibility(View.VISIBLE);
        if(tools.isOnline(getApplicationContext())) {

            algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
            algoIndex = algoClient.getIndex("BookIndex");

            Query query = new Query()
                    .setAroundLatLng(new AbstractQuery.LatLng(profile.getLat(), profile.getLng())).setGetRankingInfo(true);
            //.setAroundLatLngViaIP(true).setGetRankingInfo(true);
            algoIndex.searchAsync(query, ( jsonObject, e ) -> {
                if (e == null) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                    SearchResultsJsonParser search = new SearchResultsJsonParser();
                    Log.d("lista", jsonObject.toString());
                    books = search.parseResults(jsonObject);
                    List<Book> books2= new ArrayList<>();
                    for (Book b : books) {
                        if (b.getOwner().equals(FirebaseManagement.getUser().getUid())) {
                            books2.add(b);
                        }
                    }
                    for(Book b: books2){
                        books.remove(b);
                    }
                    tab1.getAdapter().setbooks(books);
                    tab1.getAdapter().notifyDataSetChanged();
                    progressbar.setVisibility(View.GONE);
                }
            });
        } else {
            android.app.AlertDialog.Builder ad = tools.showPopup(this, getString(R.string.noInternet), "", "");
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onStart());
            ad.setCancelable(false);
            ad.show();
        }
    }

}
class FragmentViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    FragmentViewPagerAdapter( FragmentManager manager ) {
        super(manager);
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

}




