/**
 * MentorDao, interface that facilitates communication with SQLite through the RoomDatabase Library
 * Last Update: 4/04/19
 * By: George W. Jensen III
 */

package com.myjensenfamily.mobilewguapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MentorDao {

    /**
     * addMentor, adds the specified mentor to the database
     * @param mentor to be added to the database
     * @return the mentorID assigned to the new database record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addMentor(Mentor mentor);

    /**
     * getAllMentors, queries the database for all mentors
     * @return Returns a list of mentors that are on the database
     */
    @Query("select * from mentor")
    public List<Mentor> getAllMentors();

    /**
     * getMentor, queries the database for the specified mentor
     * @param mentorId the specified mentor
     * @return if successfull returns the Mentor Object from the database, else returns null
     */
    @Query("select * from mentor where id = :mentorId LIMIT 1")
    public abstract Mentor getMentor(int mentorId);
    /**
     * findMentorsForCourse, queries the database for the mentors associated with the specified course
     * @param courseId the specified course
     * @return if sucessfull returns a list of the mentors associated with the specified course, else returns null
     */
    @Query("select * from mentor where courseId = :courseId")
    List<Mentor> findMentorsForCourse(final int courseId);

    /**
     * updateMentor, executes an update in the database for the specified mentor
     * @param mentor the Mentor object to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMentor(Mentor mentor);


    /**
     * deleteMentor, deletes the specified mentor from the database
     * @param mentor
     */
    @Delete
    void deleteMentor(Mentor mentor);

    /**
     * removeAllMentors, deletes all the mentors from the mentor table
     */
    @Query("delete from mentor")
    void removeAllMentors();

}
