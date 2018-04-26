package gmads.it.gmads_lab1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tools extends AppCompatActivity {

    public Tools(){

    }


    public boolean isOnline(Context c) {
        ConnectivityManager conMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
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

}
