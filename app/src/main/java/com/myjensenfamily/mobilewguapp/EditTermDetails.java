/**
 * EditTermDetail creates an activity where users can edit the detail of their terms
 * By: George W. Jensen III
 * Last Update 04/07/19
 */

package com.myjensenfamily.mobilewguapp;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class EditTermDetails extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.OnDateSetListener {



    private Term term;
    private TextView termName;
    private EditText editTextStartDate;
    private Locale locale;
    private DialogFragment datePicker;
    private TimePicker timePicker1;
    private EditText editTextEndDate;
    private Calendar calendar;
    private SimpleDateFormat formatter;
    private String format = "";
    private int focus;
    private AppDatabase database;
    private ArrayList<Term> terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_term_detials);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        formatter = new SimpleDateFormat("MMM dd, yyyy");

        Bundle bundle = getIntent().getExtras();

        database = MainActivity.getDatabase();
        if (database == null) {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        term = bundle.getParcelable(TermDetail.CURRENT_TERM);
        terms = new ArrayList<>();
        terms.addAll(bundle.<Term>getParcelableArrayList("Terms"));


        termName = findViewById(R.id.termName);
        termName.setText(term.getName());

        locale = getResources().getConfiguration().locale;
        datePicker = new DatePickerFragment();

        editTextStartDate = (EditText) findViewById(R.id.editTextStartDate);
        editTextStartDate.setText(formatter.format(term.getStartDate()).toString());
        editTextStartDate.setOnClickListener(this);


        editTextEndDate = (EditText) findViewById(R.id.editTextEndDate);
        editTextEndDate.setText(formatter.format(term.getEndDate()).toString());
        editTextEndDate.setOnClickListener(this);

        Button saveTerm = findViewById(R.id.saveTerm);
        saveTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Date startDate = formatter.parse(editTextStartDate.getText().toString());
                    Log.i("******Start Date", startDate.toString());
                    Date endDate = formatter.parse(editTextEndDate.getText().toString());
                    Log.i("******sEnd Date", endDate.toString());

                    int termPosition = terms.indexOf(term);

                    boolean validated = true;

                    if (termPosition > 0) {
                        if (startDate.before(terms.get(termPosition - 1).getEndDate()))
                            validated = false;
                    }
                    if (terms.size() > (termPosition + 1)) {
                        if (endDate.after(terms.get(termPosition + 1).getStartDate()))
                            validated = false;
                    }
                    if (validated) {
                        term.setStartDate(startDate);
                        term.setEndDate(endDate);

                        database.termDao().updateTerm(term);

                        Intent data = new Intent();
                        data.putExtra(TermDetail.CURRENT_TERM, term);
                        setResult(RESULT_OK, data);
                        finish();
                    } else
                    {
                        Toast.makeText(EditTermDetails.this, "Terms can not overlap, please check start/end dates", Toast.LENGTH_LONG).show();
                    }


                } catch (Exception e) {

                }
            }
        });

        calendar = Calendar.getInstance();


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
        data.putExtra(TermDetail.CURRENT_TERM, term);
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public void onClick(View v) {
        focus = v.getId();
        switch (v.getId()) {
            case R.id.editTextStartDate: {
                if (!datePicker.isAdded()) {

                    datePicker.show(getSupportFragmentManager(), "datePicker");
                }
                break;
            }
            case R.id.editTextEndDate: {
                if (!datePicker.isAdded()) {
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                }
                break;
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance(locale);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        switch (focus) {

            case R.id.editTextStartDate: {
                calendar.set(year, month, dayOfMonth);
                editTextStartDate.setText(dateFormat.format(calendar.getTime()));
                break;
            }

            case R.id.editTextEndDate: {
                calendar.set(year, month, dayOfMonth);
                editTextEndDate.setText(dateFormat.format(calendar.getTime()));
                break;
            }
        }

    }

}
