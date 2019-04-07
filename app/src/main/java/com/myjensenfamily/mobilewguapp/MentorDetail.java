/**
 * MentorDetail displays the mentor detail
 * Last Update: 04/04/19
 * By: Goerge W. Jensen III
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;



public class MentorDetail extends AppCompatActivity {

    private MenuItem menuItemEdit;
    final int REQ_EDIT_MENTOR_DETAIL = 23232;
    private TextView mentorName;
    private TextView mentorDetail;
    private int mentorPosition;
    private Mentor mentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getIntent().getExtras();

        mentor = bundle.getParcelable(MentorListing.SELECTED_MENTOR);
        mentorPosition = bundle.getInt(MentorListing.SELECTED_MENTOR_POSITION);

        mentorName = (TextView) findViewById(R.id.mentorDetailName);
        mentorName.setText(mentor.getName());


        mentorDetail = findViewById(R.id.mentorDetailInfo);
        mentorDetail.setText(
                        "Phone Number(s):\t\t"+mentor.getPhoneNumber()+"\n"+
                        "Email Address(es):\t\t"+mentor.getEmailAddress());

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

        MenuItem deletMenu = menu.findItem(R.id.action_delete);
        deletMenu.setVisible(false);

        menuItemEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MentorDetail.this,EditMentorDetail.class);
                intent.putExtra(MentorListing.SELECTED_MENTOR, mentor);
                intent.putExtra(MentorListing.SELECTED_MENTOR_POSITION, mentorPosition);
                startActivityForResult(intent, REQ_EDIT_MENTOR_DETAIL);
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
                Toast.makeText(MentorDetail.this, "Click on the pencil to edit mentor details.", Toast.LENGTH_LONG).show();
                break;
            case android.R.id.home:
                //finish();
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
        if (requestCode == REQ_EDIT_MENTOR_DETAIL)
            if(resultCode == RESULT_OK){
                mentor = data.<Mentor>getParcelableExtra(MentorListing.SELECTED_MENTOR);
                mentorName.setText(mentor.getName());
                mentorDetail.setText(
                        "Phone Number(s):\t\t"+mentor.getPhoneNumber()+"\n"+
                                "Email Address(es):\t\t"+mentor.getEmailAddress());
            }
    }



    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra(MentorListing.SELECTED_MENTOR, mentor);
        data.putExtra(MentorListing.SELECTED_MENTOR_POSITION, mentorPosition);
        setResult(RESULT_OK, data);
        finish();
    }

}
