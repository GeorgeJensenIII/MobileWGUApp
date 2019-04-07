/**
 * Course implements the course object and all the associated methods
 * By: George W. Jensen III
 * Last Update: 04/07/19
 */

package com.myjensenfamily.mobilewguapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;


@Entity(indices = {@Index("termId")}, foreignKeys = @ForeignKey(entity = Term.class, parentColumns = "id", childColumns = "termId", onDelete = CASCADE))

public class Course implements ListItem, Parcelable, Contains{

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private Date startDate;
    private Date endDate;
    private String status;
    private int termId;

    @Ignore
    private ArrayList<Mentor> mentors;
    @Ignore
    private ArrayList<Assessment> assessments;


    /**
     * Basic Constructor
     * @param name course name.
     * @param startDate course start date.
     * @param endDate course end date.
     * @param status course status.
     * @param termId term ID of the associated term.
     */
    public Course(String name, Date startDate, Date endDate, String status, int termId)
    {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.termId = termId;
        mentors = new ArrayList<>();
        assessments = new ArrayList<>();
    }


    /**
     * Parcel to Object Deserialization
     * @param in Parcel to deserialize
     */
    protected Course(Parcel in) {
        id = in.readInt();
        name = in.readString();
        status = in.readString();
        startDate = new Date(in.readLong());
        endDate = new Date(in.readLong());
        termId = in.readInt();
        mentors = in.createTypedArrayList(Mentor.CREATOR);
        assessments = in.createTypedArrayList(Assessment.CREATOR);
    }

    /**
     * Creator method to automatically deserialize assciated parcels
     */
    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Mentor> getMentors() {
        return mentors;
    }

    public void setMentors(ArrayList<Mentor> mentors) {
        this.mentors = mentors;
    }

    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }

    public int getTermId() {
        return termId;
    }

    public void setTermId(int termId) {
        this.termId = termId;
    }

    public void setAssessments(ArrayList<Assessment> assessments) {
        this.assessments = assessments;
    }

    /**
     * getInfor formats the data from the Course object as a string to be displayed in a ListView
     * @return the Course info to be displayed
     */
    public String getInfo()
    {
        String dateFormat = "MM-dd-yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        String info ="Course ID: "+getId()+" Start Date: "+simpleDateFormat.format(getStartDate())+" End Date: "+simpleDateFormat.format(getEndDate());
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
        dest.writeString(status);
        dest.writeLong(startDate.getTime());
        dest.writeLong(endDate.getTime());
        dest.writeInt(termId);
        dest.writeTypedList(mentors);
        dest.writeTypedList(assessments);
    }


    @Override
    public boolean isEmpty() {
        if (assessments.isEmpty() && mentors.isEmpty())
            return true;
        else
            return false;
    }
}



