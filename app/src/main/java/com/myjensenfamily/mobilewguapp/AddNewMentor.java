/**
 * AddNewMentor
 *      By: George W. Jensen III
 *      Last Update: 4/04/19
 *      This screen facilitates the UI elements and controls in order to add a new course mentor.
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class AddNewMentor extends AppCompatActivity{

    private EditText mentorName;
    private EditText mentorPhoneNumber;
    private EditText mentorEmailAddress;
    private Mentor mentor;
    private ArrayList<Mentor> mentors;
    private int courseId;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_mentor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        Bundle bundle = getIntent().getExtras();

        courseId = bundle.getInt("courseId");
        mentors = new ArrayList<>();

        mentors.addAll(bundle.<Mentor>getParcelableArrayList(CourseDetail.MENTOR_LISTING));

        mentor = new Mentor("", "", "", courseId);

        mentorName = findViewById(R.id.mentorName);
        mentorName.setText(mentor.getName());

        mentorPhoneNumber = findViewById(R.id.mentorPhoneNumber);
        mentorPhoneNumber.setText(mentor.getPhoneNumber().toString());

        mentorEmailAddress = findViewById(R.id.mentorEmailAddress);
        mentorEmailAddress.setText(mentor.getEmailAddress());


        Button addMentor = findViewById(R.id.addMentor);

        /**
         * Listener that creates the new mentor based on the user input, and then adds the new mentor to the list of mentors for this course.
         */

        addMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mentor.setName(mentorName.getText().toString());
                    mentor.setPhoneNumber(mentorPhoneNumber.getText().toString());
                    mentor.setEmailAddress(mentorEmailAddress.getText().toString());
                    mentor.setCourseId(courseId);
                    mentor.setId((int)database.mentorDao().addMentor(mentor));
                    mentors.add(mentor);

                    Intent data = new Intent();
                    data.putExtra(CourseDetail.MENTOR_LISTING, mentors);
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
        data.putExtra(CourseDetail.MENTOR_LISTING, mentors);
        setResult(RESULT_OK, data);
        finish();
    }

}
