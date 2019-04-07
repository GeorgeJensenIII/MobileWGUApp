/**
 * Mentor implements the mentor object and all the associated methods
 * By: George W. Jensen III
 * Last Update: 04/07/19
 */

package com.myjensenfamily.mobilewguapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(indices = {@Index("courseId")}, foreignKeys = @ForeignKey(entity = Course.class, parentColumns = "id", childColumns = "courseId", onDelete = CASCADE))
public class Mentor implements Parcelable, ListItem, Contains {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String phoneNumber;
    private String emailAddress;
    private int courseId;

    public Mentor(String name, String phoneNumber, String emailAddress, int courseId) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.courseId = courseId;
    }

    /**
     * Parcel to Object Deserialization
     * @param in Parcel to deserialize
     */
    protected Mentor(Parcel in) {
        id = in.readInt();
        name = in.readString();
        phoneNumber = in.readString();
        emailAddress = in.readString();
        courseId = in.readInt();
    }

    /**
     * Creator method to automatically deserialize assciated parcels
     */
    public static final Creator<Mentor> CREATOR = new Creator<Mentor>() {
        @Override
        public Mentor createFromParcel(Parcel in) {
            return new Mentor(in);
        }

        @Override
        public Mentor[] newArray(int size) {
            return new Mentor[size];
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

    /**
     * getInfor formats the data from the Mentor object as a string to be displayed in a ListView
     * @return the Mentor info to be displayed
     */
    @Override
    public String getInfo() {
        String info = "Mentor Name: "+this.getName()+" Mentor Phone Number(s): "+this.getPhoneNumber();
        return info;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(emailAddress);
        dest.writeInt(courseId);
    }


    @Override
    public boolean isEmpty() {
        return true;
    }
}
