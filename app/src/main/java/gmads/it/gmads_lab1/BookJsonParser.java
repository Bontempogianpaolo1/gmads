package gmads.it.gmads_lab1;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class BookJsonParser
{
    public Book parse(JSONObject jsonObject)
    {
        List<String> lc= Collections.emptyList();
        List<String> la=Collections.emptyList();

        if (jsonObject == null)
            return null;
        String bid=jsonObject.optString("bid");
        String isbn=jsonObject.optString("isbn");
        String title = jsonObject.optString("title");
        String description = jsonObject.optString("description");
        String urlimage= jsonObject.optString("urlimage");
        String author= jsonObject.optString("author");
        String publishdate=jsonObject.optString("publishDate");

        JSONArray categories=jsonObject.optJSONArray("categories");
        for(int i=0;i<categories.length();i++){
            /*
            todo:riempire liste di category e author e settare la mappa
             */
        }
        String owner= jsonObject.optString("owner");
        String publisher= jsonObject.optString("publisher");
        int rating = jsonObject.optInt("rating", -1);
        int year = jsonObject.optInt("year", 0);
        if (title != null )
            return new Book(
                    bid,
                    isbn,
                    title,
                    description,
                    urlimage,
                    publishdate,
                    la,
                    lc,
                    publisher,
                    owner,
                    0.0,
                    0.0);
        return null;
    }
}
