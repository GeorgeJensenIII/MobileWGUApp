/**
 * MentorListing displays the list of mentors associated with the selected course
 * Last Update: 04/04/19
 * By: Goerge W. Jensen III
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;




public class MentorListing extends AppCompatActivity {
    private MenuItem menuItemDelete;
    static final int REQ_MENTOR_ID = 1111;
    static final int REQ_MENTOR_DETAIL = 2222;
    public int mentorPositionn;
    public static final String  SELECTED_MENTOR = "SELECTED_MENTOR";
    public static final String SELECTED_MENTOR_POSITION = "SELECTED_MENTOR_POSITION";
    public static final String MENTOR_LISTING = "MENTOR_LISTING";

    private ArrayList<Mentor> mentors;
    private Mentor mentor;
    private int courseId;
    private AppDatabase database;

    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_listing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        courseId = bundle.getInt("courseId");
        mentors = new ArrayList<>();


        database = MainActivity.getDatabase();
        if (database == null)
        {
            database = AppDatabase.getDatabase(getApplicationContext());
        }


        mentors.addAll(bundle.<Mentor>getParcelableArrayList(CourseDetail.MENTOR_LISTING));

        adapter = new ListAdapter(
                this, R.layout.list_item, mentors);



        final ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MentorListing.this, MentorDetail.class);
                mentor = mentors.get(position);
                mentorPositionn = position;
                intent.putExtra(SELECTED_MENTOR, mentor);
                intent.putExtra(SELECTED_MENTOR_POSITION, mentorPositionn);
                startActivityForResult(intent, REQ_MENTOR_DETAIL);
            }
        });

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






        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MentorListing.this, AddNewMentor.class);
                intent.putExtra(MENTOR_LISTING, mentors);
                intent.putExtra("courseId", courseId);
                startActivityForResult(intent, REQ_MENTOR_ID);
                Intent data = new Intent();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_MENTOR_ID) {
            if (resultCode == RESULT_OK) {
                mentors.clear();
                mentors.addAll(data.<Mentor>getParcelableArrayListExtra(MENTOR_LISTING));
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode == REQ_MENTOR_DETAIL){
            if(resultCode == RESULT_OK){

                mentorPositionn = data.getIntExtra(SELECTED_MENTOR_POSITION,0);

                mentors.remove(mentorPositionn);
                mentors.add(mentorPositionn, data.<Mentor>getParcelableExtra(SELECTED_MENTOR));
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


        MenuItem editMenu = menu.findItem(R.id.action_edit);

        editMenu.setVisible(false);


        menuItemDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                List<Mentor> deleteMentor = adapter.deleteSelectedItems();

                for (Mentor mentor : deleteMentor)
                {
                    database.mentorDao().deleteMentor(mentor);
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
                Toast.makeText(MentorListing.this, "Click on a item to view it's details, long press a item to select and then click the trash can to delete selected items.", Toast.LENGTH_LONG).show();
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
        data.putExtra(CourseDetail.MENTOR_LISTING, mentors);
        setResult(RESULT_OK, data);
        finish();
    }

}

