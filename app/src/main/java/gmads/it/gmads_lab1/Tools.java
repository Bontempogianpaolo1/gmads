package gmads.it.gmads_lab1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

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
}
