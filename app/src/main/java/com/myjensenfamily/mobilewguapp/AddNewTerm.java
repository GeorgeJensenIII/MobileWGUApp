/**
 * AddNewTerm
 *      By: George W. Jensen III
 *      Last Update: 4/04/19
 *      This screen facilitates the UI elements and controls in order to add a new term.
 */


package com.myjensenfamily.mobilewguapp;


import android.app.Dialog;
import android.app.TimePickerDialog;
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



public class AddNewTerm extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.OnDateSetListener {


    private ArrayList<Term> terms;
    private TextView termName;
    private EditText editTextStartDate;
    private Locale locale;
    private DialogFragment datePicker;
    private TimePicker timePicker1;
    private EditText editTextEndDate;
    private Calendar calendar;
    private String format = "";
    private int focus;
    private AppDatabase database;
    private Term term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_term);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        database = MainActivity.getDatabase();
        if (database == null) {
            database = AppDatabase.getDatabase(getApplicationContext());
        }

        terms = bundle.getParcelableArrayList(TermListing.TERM_LISTING);

        Term lastTerm = terms.get(terms.size() -1);

        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        startDate.setTime(lastTerm.getEndDate());
        startDate.add(Calendar.DATE, 1);
        endDate.setTime(new Date(startDate.getTimeInMillis()));
        endDate.add(Calendar.MONTH, 6);
        term = new Term("",new Date(startDate.getTimeInMillis()), new Date(endDate.getTimeInMillis()));

        termName = findViewById(R.id.termName);
        StringBuilder termNameBuilder = new StringBuilder();
        if (terms.size() > 0) {
            termNameBuilder.append(terms.get(terms.size() - 1).getName());
            Integer termNumber = Integer.parseInt(termNameBuilder.substring(termNameBuilder.length() - 1)) + 1;
            termNameBuilder.setCharAt(termNameBuilder.length() - 1, termNumber.toString().charAt(0));
            termName.setText(termNameBuilder.toString());
        } else {
            termName.setText("Term 1");
        }



        locale = getResources().getConfiguration().locale;
        datePicker = new DatePickerFragment();

        editTextStartDate = (EditText) findViewById(R.id.editTextStartDate);
        editTextStartDate.setOnClickListener(this);

        editTextEndDate = (EditText) findViewById(R.id.editTextEndDate);
        editTextEndDate.setOnClickListener(this);

        editTextStartDate.setText(formatter.format(new Date(startDate.getTimeInMillis())));
        editTextEndDate.setText(formatter.format(new Date(endDate.getTimeInMillis())));





        Button addTerm = findViewById(R.id.addTerm);
        /**
         * Listener that adds the new term to the list of terms.
         * This method automaticlly takes the last day of the pervious term and populates the start and end dates of the new term for the next 6 month period,
         * although the user can change the dates by clicking on the TextFields.
         */

        addTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
                    Date startDate = formatter.parse(editTextStartDate.getText().toString());
                    Date endDate = formatter.parse(editTextEndDate.getText().toString());

                    int termPosition = terms.size();

                    boolean validated = true;

                    if (terms.size() > 1) {
                        if (startDate.before(terms.get(termPosition - 1).getEndDate()))
                            validated = false;
                    }
                    if (terms.size() > (termPosition + 1)) {
                        if (endDate.after(terms.get(termPosition + 1).getStartDate()))
                            validated = false;
                    }
                    if (validated) {
                        Term newTerm = new Term(termName.getText().toString(), startDate, endDate);

                        newTerm.setId((int) database.termDao().addTerm(newTerm));

                        terms.add(newTerm);

                        Intent data = new Intent();
                        data.putExtra(TermListing.TERM_LISTING, getTerms());
                        setResult(RESULT_OK, data);
                        finish();
                    } else
                    {
                        if(startDate.after(endDate))
                        {
                            Toast.makeText(AddNewTerm.this, "Terms start date can not be after end date, please check start/end dates",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(AddNewTerm.this, "Terms can not overlap, please check start/end dates", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    Log.i("*****Error ", e.toString());
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
        data.putExtra(TermListing.TERM_LISTING, getTerms());
        setResult(RESULT_OK, data);
        finish();
    }


    /**
     * Listener that opens a DatePickerDialog and passess the viewID that is associated with the TextField the user selected.
     * @param v the viewID of the selected TextField
     */

    @Override
    public void onClick(View v) {
        focus = v.getId();
        switch (v.getId()) {

            case R.id.editTextStartDate: {
                if (!datePicker.isAdded())
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                break;
            }
            case R.id.editTextEndDate: {
                if (!datePicker.isAdded())
                    datePicker.show(getSupportFragmentManager(), "datePicker");
                break;
            }

        }
    }

    /**
     * The Listener that is called when the user selects a date from the DatePickerDialog
     * @param view the viewID of the editTextField to be updated with the selected date
     * @param year the year value of the selected date
     * @param month the month value of the selected date
     * @param dayOfMonth the day value of the selected date
     */
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

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public void setTerms(ArrayList<Term> terms) {
        this.terms = terms;
    }
}
