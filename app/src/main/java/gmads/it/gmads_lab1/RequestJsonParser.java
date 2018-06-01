package gmads.it.gmads_lab1;

import org.json.JSONObject;

import gmads.it.gmads_lab1.model.Request;

public class RequestJsonParser
{
    public Request parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;

        String rId = jsonObject.optString("rId");
        int reviewStatusOwner = jsonObject.optInt("reviewStatusOwner");
        int reviewStatusRenter = jsonObject.optInt("reviewStatusRenter");
        int requestStatus = jsonObject.optInt("requestStatus");
        String ownerId = jsonObject.optString("ownerId");
        String bId = jsonObject.optString("bId");
        String bName = jsonObject.optString("bName");
        String renterId = jsonObject.optString("renterId");
        String ownerName = jsonObject.optString("ownerName");
        String renterName = jsonObject.optString("renterName");
        String urlBookImage = jsonObject.optString("urlBookImage");
        Long objectID = jsonObject.optLong("objectID");

        // JSONArray categories=jsonObject.optJSONArray("categories");
        //for(int i=0;i<categories.length();i++){
            /*
            todo:riempire liste di category e author e settare la mappa
             */
        //}

            if (rId != null ){
                Request r= new Request(
                    rId,
                    reviewStatusOwner,
                    reviewStatusRenter,
                    requestStatus,
                    ownerId,
                    bId,
                    bName,
                    renterId,
                    ownerName,
                    renterName,
                    urlBookImage,
                    objectID);
            return r;
        }
        return null;
    }
}
