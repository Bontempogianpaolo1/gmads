package gmads.it.gmads_lab1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
public class Tools extends AppCompatActivity {

    public Tools(){

    }
    public AlertDialog.Builder showPopup(Activity element, String title, String msg1, String msg2) {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(element);
        TextView msg = new TextView(element);
        msg.setText(title);
        //msg.setGravity(Gravity.TEXT_ALIGNMENT_CENTER);
        msg.setGravity(Gravity.CENTER);
        alertDlg.setView(msg);
        alertDlg.setCancelable(true);
        alertDlg.setPositiveButton(msg1, (dialog, which) -> {});
        alertDlg.setNegativeButton(msg2, (dialog, which) -> {});
       // alertDlg.show();

        return alertDlg;
    }

    public void getjson(Context c) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:<ISBN>";

        RequestQueue queue = Volley.newRequestQueue(c);


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("messaggio","Response is: " +response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!","Error: "+error);
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
