package gmads.it.gmads_lab1;

import org.json.JSONObject;

public class BookJsonParser
{
    public Book parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        String bid=jsonObject.optString("bid");
        String isbn=jsonObject.optString("isbn");
        String title = jsonObject.optString("title");
        String description = jsonObject.optString("description");
        String urlimage= jsonObject.optString("urlimage");
        String author= jsonObject.optString("author");
        String publishdate=jsonObject.optString("publishDate");
        String categories=jsonObject.optString("categories");
        String owner= jsonObject.optString("owner");
        String publisher= jsonObject.optString("publisher");
        int rating = jsonObject.optInt("rating", -1);
        int year = jsonObject.optInt("year", 0);
        Double lat= jsonObject.optJSONObject("_geoloc").optDouble("lat",0.0);
        Double lng= jsonObject.optJSONObject("_geoloc").optDouble("lng",0.0);
        if (title != null )
            return new Book(
                    bid,
                    isbn,
                    title,
                    description,
                    urlimage,
                    publishdate,
                    author,
                    categories,
                    publisher,
                    owner,
                    lat,
                    lng);
        return null;
    }
}
