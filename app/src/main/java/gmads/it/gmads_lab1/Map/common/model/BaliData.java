package gmads.it.gmads_lab1.Map.common.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import gmads.it.gmads_lab1.Book;

public class BaliData {

    @SerializedName("places")
    List<Book> placesList= new ArrayList();

    public List<Book> getPlacesList() {
        return placesList;
    }
    public void setPlacesList(List<Book> books){this.placesList=books;}
    public boolean isEmpty(){return placesList.isEmpty();}
}