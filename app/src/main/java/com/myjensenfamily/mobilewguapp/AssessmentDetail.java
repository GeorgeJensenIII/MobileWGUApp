/**
 * AssessmentDetail displays the assessment detail
 * Last Update: 04/04/19
 * By: Goerge W. Jensen III
 */

package com.myjensenfamily.mobilewguapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class AssessmentDetail extends AppCompatActivity {

    private Locale locale;
    private DialogFragment datePicker;

    private TextView assessmentDate;
    private TextView assessmentName;
    private TextView assessmentType;
    private TextView assessmentScore;
    private Calendar calendar;
    private String format = "";
    private Assessment assessment;
    private SimpleDateFormat formatter;
    MenuItem menuItemEdit;
    static final String CURRENT_ASSESSMENT = "CURRENT_ASSESSMENT";
    static final int REQ_ASSESSMENT_DETAIL = 44444;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        setTitle("Assessment Detail");
        Bundle bundle = getIntent().getExtras();

        formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");

        calendar = Calendar.getInstance();
        assessment = bundle.getParcelable(AssessmentListing.SELECTED_ASSESSMENT);

        locale = getResources().getConfiguration().locale;

        assessmentName = findViewById(R.id.assessmentName);
        assessmentName.setText("Assessment: "+assessment.getName());

        assessmentDate = findViewById(R.id.assessmentDate);
        assessmentDate.setText(formatter.format(assessment.getStartTime()));

        assessmentType = findViewById(R.id.assessmentType);
        assessmentType.setText(assessment.getAssessmentType());

        assessmentScore = findViewById(R.id.assessmentScore);
        assessmentScore.setText("" + assessment.getScore()+"%");
    }

    /**
     * onCreateOptionsMenu, inflates the menuItemEdit item and launches new activity
     * and waits for result
     * @param menu the main menu
     * @return when successful always returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem deleteMenu = menu.findItem(R.id.action_delete);
        deleteMenu.setVisible(false);

        menuItemEdit = menu.findItem(R.id.action_edit);

        menuItemEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(AssessmentDetail.this,EditAssessmentDetail.class);
                intent.putExtra(CURRENT_ASSESSMENT, assessment);
                startActivityForResult(intent, REQ_ASSESSMENT_DETAIL);
                return true;
            }
        });
        return true;

    }


    /**
     * onActivityResult returns when the launched activity returns to this activity
     * @param requestCode the requestCode of the activity that is returning
     * @param resultCode the resultCode of the activity that is returning
     * @param data the intent bundle that is returning
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ASSESSMENT_DETAIL) {
            if (resultCode == RESULT_OK) {
                assessment = data.getParcelableExtra(CURRENT_ASSESSMENT);

                assessmentName.setText(assessment.getName());
                assessmentDate.setText(formatter.format(assessment.getStartTime()));
                assessmentType.setText(assessment.getAssessmentType());
                assessmentScore.setText("" + assessment.getScore());

            }

        }
    }

    /**
     * onOptionsItemSelected listens for touch events on menu items
     * @param item the menu item selected
     * @return returns true always
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(AssessmentDetail.this, "Click on the pencil to edit assessment details.", Toast.LENGTH_LONG).show();
                break;
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(AssessmentListing.SELECTED_ASSESSMENT, assessment);
        setResult(RESULT_OK, data);
        finish();
    }
}