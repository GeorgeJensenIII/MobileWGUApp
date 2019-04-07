/**
 * AssessmentListing displays the list of assessments associated with the selected course
 * Last Update: 04/04/19
 * By: Goerge W. Jensen III
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;




public class AssessmentListing extends AppCompatActivity {
    private MenuItem menuItemDelete;
    static final int REQ_ADD_ASSESSMENT = 1111;
    static final String SELECTED_ASSESSMENT = "SELECTED_ASSESSMENT";
    static final int REQ_SEL_ASSESSMENT = 2222;
    private int selectedPosition;
    private int courseId;
    private AppDatabase database;

    ListAdapter adapter;

    ArrayList<Assessment> assessments;
    Assessment assessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_listing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Assessment Listing");

        assessments = new ArrayList<>();


        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        Bundle bundle = getIntent().getExtras();
        assessments.addAll(bundle.<Assessment>getParcelableArrayList(CourseDetail.ASSESSMENT_LISTING));
        courseId = bundle.getInt("courseId");
        adapter = new ListAdapter(
                this, R.layout.list_item, assessments);



        final ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        // Listener for click events on list items, then launches view detail activity

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(com.myjensenfamily.mobilewguapp.AssessmentListing.this, AssessmentDetail.class);

                assessment = assessments.get(position);
                intent.putExtra(SELECTED_ASSESSMENT, assessment);
                startActivityForResult(intent, REQ_SEL_ASSESSMENT);
                selectedPosition = position;
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





// floating action button to add new assessment
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(com.myjensenfamily.mobilewguapp.AssessmentListing.this, AddNewAssessment.class);
                intent.putExtra(CourseDetail.ASSESSMENT_LISTING, assessments);
                intent.putExtra("courseId", courseId);
                startActivityForResult(intent, REQ_ADD_ASSESSMENT);
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
        if (requestCode == REQ_ADD_ASSESSMENT) {
            if (resultCode == RESULT_OK) {
                assessments.clear();
                assessments.addAll(data.<Assessment>getParcelableArrayListExtra(CourseDetail.ASSESSMENT_LISTING));
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == REQ_SEL_ASSESSMENT){
            if(resultCode == RESULT_OK){
                assessments.remove(selectedPosition);
                assessments.add(selectedPosition, data.<Assessment>getParcelableExtra(SELECTED_ASSESSMENT));
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showDeleteMenu(boolean show){
        menuItemDelete.setVisible(show);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_main, menu);

        menuItemDelete = menu.findItem(R.id.action_delete);

        menuItemDelete.setVisible(false);


        MenuItem editMenu = menu.findItem(R.id.action_edit);
        editMenu.setVisible(false);

        /**
         * Listener for The delete menu item, when touched it deletes the selected items from the database
         */

        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<Assessment> deleteAssessments = adapter.deleteSelectedItems();
                for (Assessment assessment : deleteAssessments)
                {
                    database.assessmentDao().deleteAssessment(assessment);
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
                Toast.makeText(AssessmentListing.this, "Click on a item to view it's details, long press a item to select and then click the trash can to delete selected items.", Toast.LENGTH_LONG).show();
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
        data.putParcelableArrayListExtra(CourseDetail.ASSESSMENT_LISTING, assessments);
        setResult(RESULT_OK, data);
        finish();
    }

}

