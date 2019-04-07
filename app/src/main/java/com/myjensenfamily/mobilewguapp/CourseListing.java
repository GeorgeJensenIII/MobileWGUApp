/**
 * CourseListing displays the list of courses associated with the selected Term
 * Last Update: 04/04/19
 * By: Goerge W. Jensen III
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;




public class CourseListing extends AppCompatActivity {
    private MenuItem menuItemDelete;
    static final int REQUEST_ADD_COURSE_ID = 1111;
    static final int REQUEST_COURSE_DETAIL = 2222;
    public int coursePosition;
    ArrayList<Course> courses;
    public static final String  SELECTED_COURSE = "SELECTED_COURSE";
    public static final String  SELECTED_COURSE_POSITION = "SELECTED_MENTOR_POSITION";
    public static final String COURSE_LISTING = "MENTOR_LISTING";
    private AppDatabase database;

    private Course course;

    ListAdapter adapter;
    Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_listing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        this.setTerm((Term)bundle.getParcelable(TermDetail.COURSE_LISTING));


        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        adapter = new ListAdapter(
                this, R.layout.list_item, term.getCourses());

        setTitle("Course Listing");

        final ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CourseListing.this, CourseDetail.class);
                course = term.getCourses().get(position);
                coursePosition = position;
                intent.putExtra(SELECTED_COURSE, course);
                intent.putExtra("courseId", course.getId());
                intent.putExtra(SELECTED_COURSE_POSITION, coursePosition);
                intent.putExtra("Term", term);
                startActivityForResult(intent, REQUEST_COURSE_DETAIL);
            }
        });

        /* listener for long click events, then shows the delete menu item so the user
         can delete the selected items. */

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.handleLongPress(position, view);
                if(adapter.getListSelectedItems().size() > 0){
                    showDeleteMenu(true);
                } else {
                    showDeleteMenu(false);
                }
                return true;
            }
        });





// floating action button to add new course

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseListing.this, AddNewCourse.class);
                intent.putExtra(COURSE_LISTING, term.getCourses());
                intent.putExtra("termId",term.getId());
                intent.putExtra("Term", term);
                startActivityForResult(intent, REQUEST_ADD_COURSE_ID);
                Intent data = new Intent();

            }
        });

    }

    /**
     * onActivityResult waits for child activity to return and then executes associated result block
     * @param requestCode the requestCode associated with the returning activity
     * @param resultCode the resultCode associated with the returning activity
     * @param data the intent bundle asscoiated witht the returning activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_COURSE_ID) {
            if (resultCode == RESULT_OK) {
                term.getCourses().clear();
                term.getCourses().addAll(data.<Course>getParcelableArrayListExtra(COURSE_LISTING));
                adapter.notifyDataSetChanged();
            }
        }
            if (requestCode == REQUEST_COURSE_DETAIL){
                if(resultCode == RESULT_OK){
                    Log.i("RETURN FROM: ","REQUEST_COURSE_DETAIL");
                    coursePosition = data.getIntExtra(SELECTED_COURSE_POSITION,0);

                    Course course = data.<Course>getParcelableExtra(SELECTED_COURSE);
                    Log.i("Course Returned: ", "Size of mentors = "+course.getMentors().size());

                    term.getCourses().remove(coursePosition);
                    term.getCourses().add(coursePosition, data.<Course>getParcelableExtra(SELECTED_COURSE));
                }
            }
        }


    private void showDeleteMenu(boolean show){
        menuItemDelete.setVisible(show);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuItemSearch = menu.findItem(R.id.action_search);


        getMenuInflater().inflate(R.menu.menu_main, menu);

        menuItemDelete = menu.findItem(R.id.action_delete);

        menuItemDelete.setVisible(false);


        MenuItem editMenuItem = menu.findItem(R.id.action_edit);

        editMenuItem.setVisible(false);


        /**
         * Listener for The delete menu item, when touched it deletes the selected items from the database
         */
        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<Course> deleteCourses = adapter.deleteSelectedItems();
                for (Course course : deleteCourses)
                {
                    database.courseDao().deleteCourse(course);
                }

                adapter.notifyDataSetChanged();
                showDeleteMenu(false);
                return true;
            }
        });



        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Toast.makeText(CourseListing.this, "Click on a item to view it's details, long press a item to select and then click the trash can to delete selected items.", Toast.LENGTH_LONG).show();
                break;
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra(TermDetail.TERM_CODE, this.getTerm());
        setResult(RESULT_OK, data);
        finish();
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

}
