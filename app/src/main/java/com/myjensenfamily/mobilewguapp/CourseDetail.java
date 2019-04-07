/**
 * CourseDetail displays the course detail
 * Last Update: 04/04/19
 * By: Goerge W. Jensen III
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;




public class CourseDetail extends AppCompatActivity {

    private Course course;

    final static int REQ_ID_COURSE = 3333;
    final static String ASSESSMENT_LISTING = "ASSESSMENT_LISTING";
    private MenuItem menuItemEdit;
    final int REQ_EDIT_COURSE_DETAIL = 23232;
    final int REQ_MENTORS = 33443;
    final static String MENTOR_LISTING = "MENTOR_LISTING";
    private TextView courseName;
    private TextView courseDetail;
    private int coursePosition;
    private ArrayList<Mentor> mentors;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Bundle bundle = getIntent().getExtras();
        term = bundle.getParcelable("Term");

        course = bundle.getParcelable(CourseListing.SELECTED_COURSE);

        mentors = new ArrayList<>();
        mentors.addAll(course.getMentors());

        coursePosition = bundle.getInt(CourseListing.SELECTED_COURSE_POSITION);

        courseName = (TextView) findViewById(R.id.courseDetailName);
        courseName.setText(course.getName());


        courseDetail = findViewById(R.id.courseDetailInfo);
        courseDetail.setText("Start Date:\t"+course.getStartDate()+"\n\nEnd Date:\t"+course.getEndDate());

        Button viewAssessments = findViewById(R.id.viewAssessments);

        viewAssessments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetail.this, AssessmentListing.class);
                intent.putParcelableArrayListExtra(ASSESSMENT_LISTING, course.getAssessments());
                intent.putExtra("courseId", course.getId());
                startActivityForResult(intent, REQ_ID_COURSE);
            }
        });

        Button viewMentors = findViewById(R.id.viewMentors);

        viewMentors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetail.this, MentorListing.class);
                intent.putExtra(MENTOR_LISTING, mentors);
                intent.putExtra("courseId", course.getId());
                startActivityForResult(intent, REQ_MENTORS);
            }
        });

        Button notes = findViewById(R.id.viewNotes);

        notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(CourseDetail.this, CourseNotes.class);
                intent.putExtra("courseId", course.getId());
                startActivity(intent);
            }
        });

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

        menuItemEdit = menu.findItem(R.id.action_edit);

        menuItemEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(CourseDetail.this,EditCourseDetail.class);
                intent.putExtra(CourseListing.SELECTED_COURSE, course);
                intent.putExtra(CourseListing.SELECTED_COURSE_POSITION, coursePosition);
                intent.putExtra("Term", term);
                startActivityForResult(intent, REQ_EDIT_COURSE_DETAIL);
                return true;
            }
        });
        return true;

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
                Toast.makeText(CourseDetail.this, "Click on view mentors/assessments/notes to see the mentors/assessments/notes associated with this course, click on the pencil to edit course details.", Toast.LENGTH_LONG).show();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
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
        if (requestCode == REQ_ID_COURSE) {
            if (resultCode == RESULT_OK) {
                course.getAssessments().clear();
                course.getAssessments().addAll(data.<Assessment>getParcelableArrayListExtra(ASSESSMENT_LISTING));
            }
        }
        if (requestCode == REQ_EDIT_COURSE_DETAIL)
                if(resultCode == RESULT_OK){
                    course = data.<Course>getParcelableExtra(CourseListing.SELECTED_COURSE);

                    courseName.setText(course.getName());
                    courseDetail.setText(course.getInfo());

                }
        if(requestCode == REQ_MENTORS){
            if(resultCode == RESULT_OK){
                Log.i("RETURN FROM: ", "MENTOR LISTING");
                mentors.clear();
                mentors.addAll(data.<Mentor>getParcelableArrayListExtra(CourseDetail.MENTOR_LISTING));
                course.setMentors(mentors);
            }
        }
    }



    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra(CourseListing.SELECTED_COURSE, course);
        data.putExtra(CourseListing.SELECTED_COURSE_POSITION, coursePosition);
        setResult(RESULT_OK, data);
        finish();
    }

}
