/**
 *  MainActivity is the starting class and entry point for the MobileWGUApp
 *  By: George W. Jensen III
 *  Last Update: 04/07/2019
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Term> terms;
    private MenuItem menuItem;
    public static final String TERM_ID = "TERM_ID";
    private Term newTerm;
    static final String TERM_LISTING = "TERM_LISTING";
    static final int REQ_TERM_LISTING = 11111;
    private TextView textView;
    public static AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        terms = new ArrayList<>();

        getSupportActionBar().setLogo(getDrawable(R.drawable.ic_action_name));

        database = AppDatabase.getDatabase(getApplicationContext());



        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.MONTH, +1);



        terms.addAll(database.termDao().getAllTerms());

        Log.i("****** Terms in DB", "" + terms.size());

        for (Term currentTerm : terms) {
            Log.i("Term " + currentTerm.getName() + " With ID " + currentTerm.getId(), "is in database");
            Log.i("***********", "Adding Courses to Term: " + currentTerm.getName());
            currentTerm.getCourses().clear();
            currentTerm.getCourses().addAll(database.courseDao().findCoursesForTerm((int) currentTerm.getId()));

            for (Course currentCourse : currentTerm.getCourses()) {
                Log.i("***********", "Adding Assessments to Course:  " + currentCourse.getName());
                currentCourse.getAssessments().clear();
                currentCourse.getAssessments().addAll(database.assessmentDao().findAssessmentsForCourse(currentCourse.getId()));

                Log.i("***********", "Adding Mentors to Course " + currentCourse.getName());
                currentCourse.getMentors().clear();
                currentCourse.getMentors().addAll(database.mentorDao().findMentorsForCourse(currentCourse.getId()));
            }
        }



        textView = findViewById(R.id.degreePlanDetail);
        textView.setText("There are " + terms.size() + " Terms Registered for this degree plan.");


        final Button viewTerms = findViewById(R.id.viewTerms);

        viewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TermListing.class);
                intent.putExtra(TERM_LISTING, terms);
                startActivityForResult(intent, REQ_TERM_LISTING);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        invalidateOptionsMenu();
        menu.findItem(R.id.action_delete).setVisible(false);
        MenuItem editButton = menu.findItem(R.id.action_edit);
        editButton.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            Toast.makeText(MainActivity.this, "Click View Terms to view the terms associated with this Degree Plan", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_TERM_LISTING) {
            if (resultCode == RESULT_OK) {
                terms.clear();
                terms.addAll(data.<Term>getParcelableArrayListExtra(TERM_LISTING));
                textView.setText("There are " + terms.size() + " Terms Registered for this degree plan.");
            }

        }
    }

    public static AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }
}
