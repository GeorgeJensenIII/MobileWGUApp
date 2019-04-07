/**
 * TermDetail displays the term detail
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;




public class TermDetail extends AppCompatActivity {

    static final String TERM_CODE = "TERM_CODE";
    private final int REQ_ID_TERM = 11111;
    static final String COURSE_LISTING = "MENTOR_LISTING";
    static final String CURRENT_TERM = "CURRENT_TERM";
    private final int REQ_EDIT_TERM_DETAILS = 22222;
    private MenuItem menuItemEdit;
    int termPositon;
    private TextView termDetail;

    ArrayList<Term> terms;

    Term term;

    public int getTermPositon() {
        return termPositon;
    }

    public void setTermPositon(int termPositon) {
        this.termPositon = termPositon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        setTitle("Term Detail");

        Bundle bundle = getIntent().getExtras();

        this.setTerm((Term) bundle.getParcelable(TermListing.CURRENT_TERM));
        this.setTermPositon(bundle.getInt("selectedTermPosition"));
        terms = new ArrayList<>();
        terms.addAll(bundle.<Term>getParcelableArrayList("Terms"));

        TextView termName = findViewById(R.id.termName);
        termName.setText(term.getName());

        termDetail = findViewById(R.id.termDetail);
        termDetail.setText("Term Start Date:\t"+term.getStartDate()+"\n\nTerm End Date:\t"+term.getEndDate());

        Button viewCourses = findViewById(R.id.viewCourses);

        viewCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TermDetail.this, CourseListing.class);
                intent.putExtra(COURSE_LISTING, term);
                intent.putExtra("termId", term.getId());
                startActivityForResult(intent, REQ_ID_TERM);
            }
        });
    }


    /**
     * onActivityResult returns when the launched activity returns to this activity
     * @param requestCode the requestCode of the activity that is returning
     * @param resultCode the resultCode of the activity that is returning
     * @param data the intent bundle that is returning
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Result *** Code",""+requestCode);

        if (requestCode == REQ_ID_TERM) {
            if (resultCode == RESULT_OK) {
                term = data.getParcelableExtra(TERM_CODE);
            }
        }
        if (requestCode == REQ_EDIT_TERM_DETAILS){
            if (resultCode == RESULT_OK){
                term = data.getParcelableExtra(CURRENT_TERM);
                termDetail.setText("Term Start Date:\t"+term.getStartDate()+"\n\nTerm End Date:\t"+term.getEndDate());

            }
        }
    }

    /**
     * onCreateOptionsMenu, inflates the menuItemEdit item and launches new activity
     * and waits for result
     * @param menu the main menu
     * @return when successful always returns true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // menuItemSearch = menu.findItem(R.id.action_search);


        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu.findItem(R.id.action_delete).setVisible(false);

        menuItemEdit = menu.findItem(R.id.action_edit);

        menuItemEdit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(TermDetail.this,EditTermDetails.class);
                intent.putExtra(CURRENT_TERM, term);
                intent.putExtra("Terms", terms);
                startActivityForResult(intent, REQ_EDIT_TERM_DETAILS);
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
                Toast.makeText(TermDetail.this, "Click on view courses to see the courses associated with this term, click on the pencil to edit term details.", Toast.LENGTH_LONG).show();
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
        data.putExtra("selectedTerm", this.getTerm());
        data.putExtra("selectedTermPosition", this.termPositon);
        setResult(RESULT_OK, data);
        finish();
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }
}
