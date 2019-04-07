/**
 * DatePickerFragment
 * By: George W. Jensen III
 * Last Update: 04/07/19
 */

package com.myjensenfamily.mobilewguapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;



public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDateSetListener callbackListener;
    public String dateString;

    /**
     * An interface containing onDateSet() method signature.
     * Container Activity must implement this interface.
     */
    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            callbackListener = (OnDateSetListener) activity;

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDateSetListener.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance(getResources().getConfiguration().locale);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Locale locale = getResources().getConfiguration().locale;
        final Calendar calendar = Calendar.getInstance(locale);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        calendar.set(year, month, dayOfMonth);
        dateString = calendar.toString();

        callbackListener.onDateSet(view, year, month, dayOfMonth);
    }



}