/**
 * Assessment
 *      By: George W. Jensen III
 *      Last Update: 4/04/19
 *      This class implements the Assessment Object and the associated methods
 */


package com.myjensenfamily.mobilewguapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;



// RoomDatabase Annotation to set up Assessment Table
@Entity(indices = {@Index("courseId")}, foreignKeys = @ForeignKey(entity = Course.class, parentColumns = "id", childColumns = "courseId", onDelete = CASCADE))
public class Assessment implements ListItem, Parcelable, Contains {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String assessmentType;
    private Date startTime;
    private Double score;
    private int courseId;

    /**
     * Empty Constructor to comply with RoomDatabase Standards
     */
    public Assessment()
    {

    }

    public Assessment(String name, String assessmentType, Double score, Date date, final int courseId) {
        this.name = name;
        this.assessmentType = assessmentType;
        this.score = score;
        this.startTime = date;
        this.courseId = courseId;
    }

    /**
     * Parcel to Object Deserialization
     * @param in Parcel to deserialize
     */
    protected Assessment(Parcel in) {
        id = in.readInt();
        name = in.readString();
        assessmentType = in.readString();
        this.startTime = new Date(in.readLong());
        courseId = in.readInt();
        if (in.readByte() == 0) {
            score = null;
        } else {
            score = in.readDouble();
        }
    }

    /**
     * Creator method to automatically deserialize assciated parcels
     */
    public static final Creator<Assessment> CREATOR = new Creator<Assessment>() {
        @Override
        public Assessment createFromParcel(Parcel in) {
            return new Assessment(in);
        }

        @Override
        public Assessment[] newArray(int size) {
            return new Assessment[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * getInfor formats the data from the Assesment object as a string to be displayed in a ListView
     * @return the Assessment info to be displayed
     */
    public String getInfo()
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");

        return (assessmentType+"\n Start Time: "+dateFormatter.format(startTime));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in)
    {
        this.id = in.readInt();
        this.name = in.readString();
        this.assessmentType = in.readString();
        this.score = in.readDouble();
        this.startTime = new Date(in.readLong());
        this.courseId = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(assessmentType);
        dest.writeLong(startTime.getTime());
        dest.writeInt(courseId);
        if (score == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(score);
        }
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
