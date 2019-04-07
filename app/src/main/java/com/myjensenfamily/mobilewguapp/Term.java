/**
 * Term implements the term object and all the associated methods
 * By: George W. Jensen III
 * Last Update: 04/07/19
 */

package com.myjensenfamily.mobilewguapp;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class Term implements ListItem, Parcelable, Contains{

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private Date startDate;
    private Date endDate;
    @Ignore
    private ArrayList<Course> courses;

    public Term(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.courses = new ArrayList<>();
    }

    /**
     * Parcel to Object Deserialization
     * @param in Parcel to deserialize
     */
    protected Term(Parcel in) {
        this("", new Date(), new Date ());
        id = in.readInt();
        name = in.readString();
        startDate = new Date(in.readLong());
        endDate = new Date(in.readLong());
        courses = in.createTypedArrayList(Course.CREATOR);
    }

    /**
     * Creator method to automatically deserialize assciated parcels
     */
    public static final Creator<Term> CREATOR = new Creator<Term>() {
        @Override
        public Term createFromParcel(Parcel in) {
            return new Term(in);
        }

        @Override
        public Term[] newArray(int size) {
            return new Term[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }


    /**
     * getInfor formats the data from the Term object as a string to be displayed in a ListView
     * @return the Term info to be displayed
     */
    public String getInfo()
    {
        /**
         * implements the method getInfo from the ListItem interface
         *
         * @info returns the details of the current term
         */
        String dateFormat = "MM-dd-yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        String info ="Start Date: "+simpleDateFormat.format(startDate)+"\nEnd Date: "+simpleDateFormat.format(endDate);
        return info;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeLong(startDate.getTime());
        dest.writeLong(endDate.getTime());
        dest.writeTypedList(courses);
    }

    @Override
    public boolean isEmpty() {
        if (courses.isEmpty())
            return true;
        else
            return false;
    }
}
