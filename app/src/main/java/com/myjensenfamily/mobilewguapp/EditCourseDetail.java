/**
 * EditCourseDetail creates an activity where users can edit the detail of their courses
 * By: George W. Jensen III
 * Last Update 04/07/19
 */

package com.myjensenfamily.mobilewguapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class EditCourseDetail extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.OnDateSetListener {

    private EditText courseName;
    private EditText editTextStartDate;
    private Locale locale;
    private DialogFragment datePicker;
    private Course course;

    private EditText editTextEndDate;
    private Calendar calendar;
    private String format = "";
    private int focus;
    private AppDatabase database;
    private String courseStatus;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        Bundle bundle = getIntent().getExtras();

        term = bundle.getParcelable("Term");

        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        setTitle("Edit Course Detail");


        course = bundle.getParcelable(CourseListing.SELECTED_COURSE);

        courseName = findViewById(R.id.courseName);
        courseName.setText(course.getName());

        locale = getResources().getConfiguration().locale;
        datePicker = new DatePickerFragment();

        editTextStartDate = (EditText) findViewById(R.id.editTextStartDate);
        editTextStartDate.setOnClickListener(this);
        editTextStartDate.setText(formatter.format(course.getStartDate()).toString());



        editTextEndDate = (EditText) findViewById(R.id.editTextEndDate);
        editTextEndDate.setOnClickListener(this);
        editTextEndDate.setText(formatter.format(course.getEndDate()));


        Spinner dropdown = findViewById(R.id.courseStatus);

        final String[] items = new String[]{"PLAN_TO_TAKE", "IN_PROGRESS", "DROPPED", "COMPLETED"};


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditCourseDetail.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        if (course.getStatus() != null) {
            int spinnerPosition = adapter.getPosition(course.getStatus());
            dropdown.setSelection(spinnerPosition);
        }

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                setCourseStatus(items[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        Button addCourse = findViewById(R.id.addCourse);
        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
                    Date startDate = formatter.parse(editTextStartDate.getText().toString());
                    Date endDate = formatter.parse(editTextEndDate.getText().toString());


                    if (startDate.after(endDate)) {
                        Toast.makeText(EditCourseDetail.this, "Start date can not be after end date, please check start/end dates", Toast.LENGTH_LONG).show();
                    }else if (startDate.after(term.getEndDate()) || endDate.after(term.getEndDate()) || startDate.before(term.getStartDate())) {
                        Toast.makeText(EditCourseDetail.this, "Courses have to be within Term Start/End Dates, please check start/end dates", Toast.LENGTH_LONG).show();
                    }else {

                        course.setName(courseName.getText().toString());
                        course.setStartDate(startDate);
                        course.setEndDate(endDate);

                        database.courseDao().updateCourse(course);


                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Intent alertIntent = new Intent(EditCourseDetail.this, AlertActivity.class);

                        alertIntent.putExtra(AlertActivity.ALERT_MESSAGE, "The course: " + course.getName() + " is about to start");

                        PendingIntent pendingIntent = PendingIntent.getActivity(EditCourseDetail.this, 0, alertIntent, 0);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, course.getStartDate().getTime(), pendingIntent);


                        alertIntent = new Intent(EditCourseDetail.this, AlertActivity.class);

                        alertIntent.putExtra(AlertActivity.ALERT_MESSAGE, "The course: " + course.getName() + " is about to end");

                        pendingIntent = PendingIntent.getActivity(EditCourseDetail.this, 0, alertIntent, 0);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, course.getEndDate().getTime(), pendingIntent);

                        course.setStatus(getCourseStatus());


                        Intent data = new Intent();
                        data.putExtra(CourseListing.SELECTED_COURSE, course);
                        setResult(RESULT_OK, data);
                        finish();
                    }

                } catch (Exception e) {

                }
            }
        });

        calendar = Calendar.getInstance();


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
        data.putExtra(CourseListing.SELECTED_COURSE, course);
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public void onClick(View v) {
        focus = v.getId();
        switch (v.getId()) {

            case R.id.editTextStartDate: {
                if (!datePicker.isAdded())
                {
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                    focus = R.id.editTextStartDate;
                }
                break;
            }
            case R.id.editTextEndDate: {
                if (!datePicker.isAdded())
                {
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                    focus = R.id.editTextEndDate;
                }
                break;
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance(locale);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);


            switch (focus) {

                case R.id.editTextStartDate: {
                    calendar.set(year, month, dayOfMonth);
                    editTextStartDate.setText(dateFormat.format(calendar.getTime()));
                    break;
                }

                case R.id.editTextEndDate: {
                    calendar.set(year, month, dayOfMonth);
                    editTextEndDate.setText(dateFormat.format(calendar.getTime()));
                    break;
                }
            }

    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }
}

