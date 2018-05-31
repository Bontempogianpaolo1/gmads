package gmads.it.gmads_lab1;

import org.json.JSONObject;

public class RequestJsonParser
{
    public ReferenceRequest parse(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;
        String bId=jsonObject.optString("bookid");
        String title = jsonObject.optString("bookname");
        String urlimage= jsonObject.optString("imgurl");
        String nomerichiedente= jsonObject.optString("nomerichiedente");
        String requestid=jsonObject.optString("requestid");
        String objectID= jsonObject.optString("objectID");

        // JSONArray categories=jsonObject.optJSONArray("categories");
        //for(int i=0;i<categories.length();i++){
            /*
            todo:riempire liste di category e author e settare la mappa
             */
        //}

            if (title != null ){
            ReferenceRequest b= new ReferenceRequest(
                    title,
                    urlimage,
                    title,
                    requestid,
                    bId);
            return b;
        }
        return null;
    }
}
