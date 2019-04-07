/**
 * AlertActivity
 *      By: George W. Jensen III
 *      Last Update: 4/04/19
 *      This screen shows an alert message for an upcoming course.
 */

package com.myjensenfamily.mobilewguapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class AlertActivity extends AppCompatActivity {

    static final String ALERT_MESSAGE = "ALERT_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Bundle bundle = getIntent().getExtras();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setMessage(bundle.getString(ALERT_MESSAGE));
        alertDialog.setTitle("Upcoming Course Alert");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                dialog.dismiss();
                finish();
            }
        });

        alertDialog.show();
    }
}
