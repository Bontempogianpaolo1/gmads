package gmads.it.gmads_lab1;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class MapSearch extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Client algoClient;
    Index algoIndex;
    LatLngBounds bounds;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
        algoIndex = algoClient.getIndex("BookIndex");
        //SearchView sv = findViewById(R.id.searchView);
       // sv.getLeft()
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady( GoogleMap googleMap ) {
        mMap = googleMap;
        algoIndex.searchAsync(new Query("ha"), ( jsonObject, e ) -> {
            if(e==null){
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                SearchResultsJsonParser search= new SearchResultsJsonParser();
                Log.d("lista",jsonObject.toString());
                List<Book> books= search.parseResults(jsonObject);
                LatLngBounds.Builder builder= new LatLngBounds.Builder();



               for(Book b : books){
                  Marker m= mMap.addMarker(new MarkerOptions().position(new LatLng(b.get_geoloc().getLat(),b.get_geoloc().getLng())).title(b.getTitle()));
                  builder.include(m.getPosition());
               }
               bounds=builder.build();
                if (areBoundsTooSmall(bounds, 300)) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 30));
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
                }
            }
        });
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(45.074045, 7.597933399999988);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    private boolean areBoundsTooSmall(LatLngBounds bounds, int minDistanceInMeter) {
        float[] result = new float[1];
        Location.distanceBetween(bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.latitude, bounds.northeast.longitude, result);
        return result[0] < minDistanceInMeter;
    }
}
