/**
 * CourseNotes, creates a activity and populates it with UI elements entirely progamatically
 * By: George W. Jensen III
 * Last Update 04/07/19
 */

package com.myjensenfamily.mobilewguapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;




public class CourseNotes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create params for views
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // Create a layout
        RelativeLayout relativeLayout = new RelativeLayout(this);

        Toolbar toolbar = new Toolbar(this);


        setSupportActionBar(toolbar);




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 168);
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitle("Course Notes");
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setId(View.generateViewId());
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        relativeLayout.addView(toolbar, layoutParams);
        toolbar.requestLayout();





        // Create A EditText
        final EditText editText = new EditText(this);
        RelativeLayout.LayoutParams editTextLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editTextLp.addRule(RelativeLayout.BELOW, toolbar.getId());
        editText.setId(View.generateViewId());
        relativeLayout.addView(editText, editTextLp);
        editText.requestLayout();


        // Create Button

        Button saveButton = new Button(this);
        saveButton.setText("Save Notes");
        saveButton.setId(View.generateViewId());
        RelativeLayout.LayoutParams saveButtonLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        saveButtonLp.addRule(RelativeLayout.BELOW, editText.getId());
        saveButtonLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relativeLayout.addView(saveButton, saveButtonLp);
        saveButton.requestLayout();


        FloatingActionButton fab = new FloatingActionButton(this);
        RelativeLayout.LayoutParams fabLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fabLp.setMargins(32, 32, 32, 32);
        fabLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fabLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        fab.setImageResource(android.R.drawable.ic_dialog_email);
        relativeLayout.addView(fab, fabLp);



        // Add all elements to the layout





        // Create a layout param for the layout
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        this.addContentView(relativeLayout, relativeLayoutParams);

        Bundle bundle = getIntent().getExtras();

        final Integer courseId = new Integer(bundle.getInt("courseId"));

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        editText.setText(sharedPref.getString(courseId.toString(), "Type your notes here"));


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(courseId.toString() , editText.getText().toString());
                editor.commit();
                finish();
            }
        });




        final String[] addresses = {"abc@gmail.com","abcd@gmail.com"};

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("sms:"));
                smsIntent.putExtra("sms_body", editText.getText().toString());
                startActivity(smsIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }



}
