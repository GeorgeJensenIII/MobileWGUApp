/**
 * EditMentorDetail creates an activity where users can edit the detail of their mentors
 * By: George W. Jensen III
 * Last Update 04/07/19
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class EditMentorDetail extends AppCompatActivity{

    private EditText mentorName;
    private EditText mentorPhoneNumber;
    private EditText mentorEmailAddress;
    private Mentor mentor;
    private int focus;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();

        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        mentor = bundle.getParcelable(MentorListing.SELECTED_MENTOR);

        mentorName = findViewById(R.id.editMentorName);
        mentorName.setText(mentor.getName());

        mentorPhoneNumber = findViewById(R.id.editMentorPhoneNumber);
        mentorPhoneNumber.setText(mentor.getPhoneNumber().toString());

        mentorEmailAddress = findViewById(R.id.editMentorEmailAddress);
        mentorEmailAddress.setText(mentor.getEmailAddress());


        Button saveMentor = findViewById(R.id.saveMentor);
        saveMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mentor.setName(mentorName.getText().toString());
                    mentor.setPhoneNumber(mentorPhoneNumber.getText().toString());
                    mentor.setEmailAddress(mentorEmailAddress.getText().toString());

                    database.mentorDao().updateMentor(mentor);

                    Intent data = new Intent();
                    data.putExtra(MentorListing.SELECTED_MENTOR, mentor);
                    setResult(RESULT_OK, data);
                    finish();

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
        data.putExtra(MentorListing.SELECTED_MENTOR, mentor);
        setResult(RESULT_OK, data);
        finish();
    }

}

