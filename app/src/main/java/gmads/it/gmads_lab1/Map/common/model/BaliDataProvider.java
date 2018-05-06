package gmads.it.gmads_lab1.Map.common.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

import gmads.it.gmads_lab1.Book;
import gmads.it.gmads_lab1.Map.common.WorkcationApp;
import gmads.it.gmads_lab1.SearchResultsJsonParser;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class BaliDataProvider {
    private final static String JSON_PATH = "bali.json";

    private static BaliDataProvider sInstance;
    Client algoClient;
    Index algoIndex;
    private BaliData mBaliData;

    private BaliDataProvider() {

    }

    public static BaliDataProvider instance() {
        if(sInstance == null) {
            sInstance = new BaliDataProvider();
            return sInstance;
        }
        return sInstance;
    }

    public void initialize(String query) {
        algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
        algoIndex = algoClient.getIndex("BookIndex");

        //algoIndex.searchAsync(new Query(query), ( jsonObject, e ) -> {
              // SearchResultsJsonParser search= new SearchResultsJsonParser();
            //    Log.d("lista",jsonObject.toString());
              //  mBaliData= new BaliData();
               // mBaliData.setPlacesList(search.parseResults(jsonObject));
           // }
        //);mBaliData
     //  while (mBaliData==null||mBaliData.isEmpty()){
       //    Log.d("waiting","waiting");
       //}
        //
        mBaliData=new BaliData();
        Thread t= new Thread(() -> {/*
           algoIndex.searchAsync(new Query(query), new CompletionHandler() {
                @Override
                public void requestCompleted( JSONObject jsonObject, AlgoliaException e ) {
                    SearchResultsJsonParser search= new SearchResultsJsonParser();
                    mBaliData.setPlacesList(search.parseResults(jsonObject));
                    return;
                }
            });*/
            SearchResultsJsonParser search= new SearchResultsJsonParser();
            try {
                mBaliData.setPlacesList(search.parseResults(algoIndex.searchSync(new Query(query))));
            }catch (Exception e){
                e.printStackTrace();
            }

        });
        t.start();
        try {
            t.join();
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public LatLngBounds provideLatLngBoundsForAllPlaces() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Book place : mBaliData.getPlacesList()) {
            builder.include(new LatLng(place.get_geoloc().getLat(), place.get_geoloc().getLng()));
        }
        return builder.build();
    }

    public List<Book> providePlacesList() {
        return mBaliData.getPlacesList();
    }

    public double getLatByPosition(final int position) {
        return mBaliData.getPlacesList().get(position).get_geoloc().getLat();
    }

    public double getLngByPosition(final int position) {
        return mBaliData.getPlacesList().get(position).get_geoloc().getLng();
    }
}
