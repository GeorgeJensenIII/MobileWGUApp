/**
 * AddNewCourse
 *      By: George W. Jensen III
 *      Last Update: 4/04/19
 *      This screen facilitates the UI elements and controls in order to add a new course.
 */

package com.myjensenfamily.mobilewguapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;




public class AddNewCourse extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.OnDateSetListener {


    private ArrayList<Course> courses;
    private EditText courseName;
    private EditText editTextStartDate;
    private Locale locale;
    private DialogFragment datePicker;

    private EditText editTextEndDate;
    private Calendar calendar;
    private String format = "";
    private int focus;
    private int termId;
    private AppDatabase database;
    private String courseStatus;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        setTitle("Add New Course");

        Bundle bundle = getIntent().getExtras();
        termId = bundle.getInt("termId");
        term = bundle.getParcelable("Term");

        courses = bundle.getParcelableArrayList(CourseListing.COURSE_LISTING);

        courseName = findViewById(R.id.courseName);

        locale = getResources().getConfiguration().locale;
        datePicker = new DatePickerFragment();

        editTextStartDate = (EditText) findViewById(R.id.editTextStartDate);
        editTextStartDate.setOnClickListener(this);

        editTextEndDate = (EditText) findViewById(R.id.editTextEndDate);
        editTextEndDate.setOnClickListener(this);

        Spinner dropdown = findViewById(R.id.courseStatus);

        final String[] items = new String[]{"PLAN_TO_TAKE", "IN_PROGRESS", "DROPPED", "COMPLETED"};


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddNewCourse.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setSelection(0);


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

                    if (startDate.after(endDate))
                    {
                        Toast.makeText(AddNewCourse.this, "Start date can not be after end date, please check start/end dates",
                                Toast.LENGTH_LONG).show();
                    } else if (startDate.after(term.getEndDate()) || endDate.after(term.getEndDate()) || startDate.before(term.getStartDate())) {
                        Toast.makeText(AddNewCourse.this, "Courses have to be within Term Start/End Dates, please check start/end dates",
                                Toast.LENGTH_LONG).show();
                    }

                    else {


                        Course newCourse = new Course(courseName.getText().toString(), startDate, endDate, getCourseStatus(), termId);
                        newCourse.setId((int) database.courseDao().addCourse(newCourse));
                        courses.add(newCourse);


                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        Intent alertIntent = new Intent(AddNewCourse.this, AlertActivity.class);

                        alertIntent.putExtra(AlertActivity.ALERT_MESSAGE, "The course: " + newCourse.getName() + " is about to start");

                        PendingIntent pendingIntent = PendingIntent.getActivity(AddNewCourse.this, 0, alertIntent, 0);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, newCourse.getStartDate().getTime(), pendingIntent);


                        alertIntent = new Intent(AddNewCourse.this, AlertActivity.class);

                        alertIntent.putExtra(AlertActivity.ALERT_MESSAGE, "The course: " + newCourse.getName() + " is about to end");

                        pendingIntent = PendingIntent.getActivity(AddNewCourse.this, 0, alertIntent, 0);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, newCourse.getEndDate().getTime(), pendingIntent);


                        Intent data = new Intent();
                        data.putExtra(CourseListing.COURSE_LISTING, getCourses());
                        setResult(RESULT_OK, data);
                        finish();
                    }

                } catch (Exception e) {
                    Log.i("*****Error ", e.toString());
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
        data.putExtra(CourseListing.COURSE_LISTING, getCourses());
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * On Click listener for Interactive UI elements
     * @param v, the view ID to be passed to date picker.
     */
    @Override
    public void onClick(View v) {
        focus = v.getId();
        switch (v.getId()) {

            case R.id.editTextStartDate: {
                if (!datePicker.isAdded())
                {
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                }
                break;
            }
            case R.id.editTextEndDate: {
                if (!datePicker.isAdded())
                {
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                }
                break;
            }
        }
    }

    /**
     * The Listener activated when the user picks a date
     * @param view the associated date TextField to which to apply the values from the DatePickerDialog
     * @param year the year value of the selected date
     * @param month the month value of the selected date
     * @param dayOfMonth the day value of the selected date
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance(locale);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        if (getCurrentFocus() != null) {
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
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }
    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }
}

