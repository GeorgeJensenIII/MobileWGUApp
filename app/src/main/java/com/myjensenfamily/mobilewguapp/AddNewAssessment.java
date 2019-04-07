/**
 * AddNewAssessment
 *      By: George W. Jensen III
 *      Last Update: 4/04/19
 *      This screen facilitates the UI elements and controls in order add a new assessment to a course.
 */

package com.myjensenfamily.mobilewguapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewAssessment extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.OnDateSetListener {

    private Locale locale;
    private DialogFragment datePicker;
    private TimePickerDialog timePickerDialog;

    private EditText editTextDate;
    private EditText assessmentName;
    private EditText editTextStartTime;
    private Calendar calendar;
    private String format = "";
    private ArrayList<Assessment> assessments;
    private final int DIALOG_ID = 22222;
    private int min_x;
    private int hour_x;
    private String assessmentStringType;
    private int focus;
    private int courseId;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_assessment);

        this.database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        assessments = new ArrayList<>();
        calendar = Calendar.getInstance();
        assessments.addAll(bundle.<Assessment>getParcelableArrayList(CourseDetail.ASSESSMENT_LISTING));
        courseId = bundle.getInt("courseId");

        locale = getResources().getConfiguration().locale;
        datePicker = new DatePickerFragment();

        assessmentName = findViewById(R.id.assessmentName);

        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextDate.setOnClickListener(this);

        editTextStartTime = findViewById(R.id.editTextStartTime);

        editTextStartTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                showTImePickerDialog();
                return true;
            }
        });


        Spinner dropdown = findViewById(R.id.assessmentType);

        String[] items = new String[]{"OBJECTIVE_ASSESSMENT", "PERFORMANCE_ASSESSMENT"};


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddNewAssessment.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setSelection(0);


        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

                switch (position) {
                    case 0:
                        setAssessmentStringType("OBJECTIVE_ASSESSMENT");
                        break;
                    case 1:
                        setAssessmentStringType("PERFORMANCE_ASSESSMENT");
                        break;

                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        Button addAssessmentButton = findViewById(R.id.addAssessmentButton);

        /**
         * addAssessmentButton -- When clicked, grabs date and time from pickers, then adds the new assessment to the course.
         * this method also sets an alarm for 5 min before the assessment's scheduled start time.
         */

        addAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent data = new Intent();
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");

                    String date = editTextDate.getText().toString()+" "+hour_x+":"+min_x;
                    Date startDate = formatter.parse(date);

                    Log.i("Info", "Parsed Date");
                    Assessment newAssessment = new Assessment(assessmentName.getText().toString(), assessmentStringType, 0.00, startDate, courseId);
                    newAssessment.setId((int)database.assessmentDao().addAssessment(newAssessment));
                    assessments.add(newAssessment);


                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    Intent alertIntent = new Intent(AddNewAssessment.this, AlertActivity.class);

                    alertIntent.putExtra(AlertActivity.ALERT_MESSAGE, "The assessment: "+newAssessment.getName()+" is about to start");

                    PendingIntent pendingIntent = PendingIntent.getActivity(AddNewAssessment.this, 0, alertIntent, 0);

                    Calendar c = Calendar.getInstance();

                    c.setTime(newAssessment.getStartTime());
                    c.add(Calendar.MINUTE, -5);

                    Date alarmStartTime = c.getTime();

                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime.getTime() , pendingIntent);



                    data.putExtra(CourseDetail.ASSESSMENT_LISTING, getAssessments());
                    setResult(RESULT_OK, data);
                    finish();

                } catch (Exception e) {
                    Log.i("*****Error ", e.toString());
                }
            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        data.putExtra(CourseDetail.ASSESSMENT_LISTING, getAssessments());
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * Listener that opens the Date Picker Dialog Fragment when the TextField editTextDate is clicked
     * @param v the view ID of the editTextDate UI element
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editTextDate: {
                if(!datePicker.isAdded())
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                break;
            }
        }
    }

    /**
     * Listener that updates the editTextDate TextField when the user selects a date
     * from the date picker fragment.
     * @param view the DatePicker view
     * @param year the year value from the selected date
     * @param month the month value form the selected date
     * @param dayOfMonth the day value from the selected date
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance(locale);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
                    calendar.set(year, month, dayOfMonth);
                    editTextDate.setText(dateFormat.format(calendar.getTime()));

    }

    /**
     * Shows the time picker dialog
     */
    public void showTImePickerDialog (){
            if (timePickerDialog == null)
                timePickerDialog = new TimePickerDialog(AddNewAssessment.this, kTimePickerListiner, hour_x, min_x, false);
                timePickerDialog.show();
    }

    /**
     * Listener for the editTextStartTime TextField
     * Opens the TimePickerDialog when the user clicks the editTextStartTime TextField
     */
    protected TimePickerDialog.OnTimeSetListener kTimePickerListiner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            min_x = minute;
            editTextStartTime.setText(hour_x+":"+min_x);
        }
    };




    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }

    public String getAssessmentStringType() {
        return assessmentStringType;
    }

    public void setAssessmentStringType(String assessmentStringType) {
        this.assessmentStringType = assessmentStringType;
    }
}

