/**
 * EditAssessmentDetail creates an activity where users can edit the detail of their assessments
 * By: George W. Jensen III
 * Last Update 04/07/19
 */

package com.myjensenfamily.mobilewguapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class EditAssessmentDetail extends AppCompatActivity implements View.OnTouchListener, DatePickerFragment.OnDateSetListener {

    private Locale locale;
    private DialogFragment datePicker;
    private TimePickerDialog timePickerDialog;
    private Assessment assessment;
    private EditText editTextDate;
    private EditText editAssessmentName;
    private EditText editTextStartTime;
    private EditText editAssessmentScore;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private Calendar calendar;
    private String format = "";
    private final int DIALOG_ID = 22222;
    private int min_x;
    private int hour_x;
    private String assessmentStringType;
    private AppDatabase database;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assessment_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        Bundle bundle = getIntent().getExtras();

        assessment = bundle.getParcelable(AssessmentDetail.CURRENT_ASSESSMENT);


        dateFormatter = new SimpleDateFormat("MMM dd, yyyy");
        timeFormatter = new SimpleDateFormat("HH:mm");

        SimpleDateFormat minHourFormatter = new SimpleDateFormat("HH");
        hour_x = Integer.parseInt(minHourFormatter.format(assessment.getStartTime()));
        minHourFormatter = new SimpleDateFormat("mm");
        min_x = Integer.parseInt(minHourFormatter.format(assessment.getStartTime()));

        calendar = Calendar.getInstance();

        locale = getResources().getConfiguration().locale;
        datePicker = new DatePickerFragment();

        editAssessmentName = findViewById(R.id.assessmentName);
        editAssessmentName.setText(assessment.getName());

        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextDate.setText(dateFormatter.format(assessment.getStartTime()).toString());
        editTextDate.setOnTouchListener(this);

        editTextStartTime = findViewById(R.id.editTextStartTime);
        editTextStartTime.setText(timeFormatter.format(assessment.getStartTime()).toString());

        editAssessmentScore = findViewById(R.id.editAssessmentScore);
        editAssessmentScore.setText("" + assessment.getScore());

        editAssessmentScore.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        Spinner dropdown = findViewById(R.id.assessmentType);

        String[] items = new String[]{"OBJECTIVE_ASSESSMENT", "PERFORMANCE_ASSESSMENT"};


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditAssessmentDetail.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        if (assessment.getAssessmentType() != null) {
            int spinnerPosition = adapter.getPosition(assessment.getAssessmentType());
            dropdown.setSelection(spinnerPosition);
        }

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


        editTextStartTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                showTImePickerDialog();
                return true;
            }
        });


        Button saveAssessmentButton = findViewById(R.id.saveAssessmentButton);

        saveAssessmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String score = editAssessmentScore.getText().toString();
                    if(!score.contains("."))
                    {
                        editAssessmentScore.setText(score+".0");
                    }

                    if (Double.parseDouble(editAssessmentScore.getText().toString()) > 100.0 | Double.parseDouble(editAssessmentScore.getText().toString()) < 0.0) {
                        Toast.makeText(EditAssessmentDetail.this, "Score must be between 0.0 and 100.0", Toast.LENGTH_LONG).show();
                    } else {
                        Intent data = new Intent();
                        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");

                        String date = editTextDate.getText().toString() + " " + hour_x + ":" + min_x;

                        Log.i("Output of formatter", date);
                        Date startDate = formatter.parse(date);

                        assessment.setStartTime(startDate);

                        assessment.setName(editAssessmentName.getText().toString());

                        assessment.setAssessmentType(assessmentStringType);

                        Log.i("Double Value of: ", editAssessmentScore.getText().toString());

                        Double newScore = Double.parseDouble(editAssessmentScore.getText().toString());

                        Log.i("double", "" + newScore);

                        assessment.setScore(newScore);

                        Log.i("New Assessment Score", "" + newScore);

                        database.assessmentDao().updateAssessment(assessment);



                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Intent alertIntent = new Intent(EditAssessmentDetail.this, AlertActivity.class);

                        alertIntent.putExtra(AlertActivity.ALERT_MESSAGE, "The assessment: "+assessment.getName()+" is about to start");

                        PendingIntent pendingIntent = PendingIntent.getActivity(EditAssessmentDetail.this, 0, alertIntent, 0);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, assessment.getStartTime().getTime(), pendingIntent);


                        data.putExtra(AssessmentDetail.CURRENT_ASSESSMENT, assessment);

                        setResult(RESULT_OK, data);

                        finish();
                    }
                } catch (Exception e) {

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
        data.putExtra(AssessmentDetail.CURRENT_ASSESSMENT, assessment);
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        switch (v.getId()) {

            case R.id.editTextDate: {
                if (!datePicker.isAdded())
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                break;
            }
        }
        return true;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance(locale);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        calendar.set(year, month, dayOfMonth);
        editTextDate.setText(dateFormat.format(calendar.getTime()));

    }

    public void showTImePickerDialog() {
        if (timePickerDialog == null)
            timePickerDialog = new TimePickerDialog(EditAssessmentDetail.this, kTimePickerListiner, hour_x, min_x, false);
        timePickerDialog.show();
    }

    protected TimePickerDialog.OnTimeSetListener kTimePickerListiner = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            min_x = minute;
            editTextStartTime.setText(hour_x + ":" + min_x);
        }
    };


    public String getAssessmentStringType() {
        return assessmentStringType;
    }

    public void setAssessmentStringType(String assessmentStringType) {
        this.assessmentStringType = assessmentStringType;
    }


    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

}

