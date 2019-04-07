/**
 * AssessmentDao, interface that facilitates communication with SQLite through the RoomDatabase Library
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
public interface AssessmentDao {

    /**
     * addAssessment, adds the specified assessment to the database
     * @param assessment to be added to the database
     * @return the assessmentID assigned to the new database record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addAssessment(Assessment assessment);

    /**
     * getAllAssessments, queries the database for all assessments
     * @return Returns a list of assessments that are on the database
     */
    @Query("select * from assessment")
    public List<Assessment> getAllAssessments();

    /**
     * getAssessment, queries the database for the specified assessment
     * @param assessmentId the specified assessment
     * @return if successfull returns the Assessment Object from the database, else returns null
     */
    @Query("select * from assessment where id = :assessmentId LIMIT 1")
    public abstract Assessment getAssessment(int assessmentId);

    /**
     * findAssessmentsForCourse, queries the database for the assessments associated with the specified courseID
     * @param courseId the specified course
     * @return if sucessfull returns a list of the assments associated with the specified course, else returns null
     */
    @Query("select * from assessment where courseId =:courseId")
    public List<Assessment> findAssessmentsForCourse(final int courseId);

    /**
     * updateAssessment, executes an update in the database for the specified assessment
     * @param assessment the Assessment object to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAssessment(Assessment assessment);

    /**
     * deleteAssessment, deletes the specified assessment from the database
     * @param assessment
     */
    @Delete
    void deleteAssessment(Assessment assessment);

    /**
     * removeAllAssessments, deletes all the assessments from the assessment table
     */
    @Query("delete from assessment")
    void removeAllAssessments();

}
