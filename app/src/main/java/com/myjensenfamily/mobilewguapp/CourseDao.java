/**
 * CoursetDao, interface that facilitates communication with SQLite through the RoomDatabase Library
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
public interface CourseDao {

    /**
     * addCourse, adds the specified course to the database
     * @param course to be added to the database
     * @return the courseId assigned to the new database record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addCourse(Course course);

    /**
     * getAllCourses, queries the database for all courses
     * @return Returns a list of courses that are on the database
     */
    @Query("select * from course")
    public List<Course> getAllCourses();

    /**
     * getCourse, queries the database for the specified course
     * @param courseId the specified course
     * @return if successfull returns the Course Object from the database, else returns null
     */
    @Query("select * from course where id = :courseId LIMIT 1")
    public abstract Course getCourse(int courseId);

    /**
     * findCoursesForTerm, queries the database for the courses associated with the specified term
     * @param termId the specified term
     * @return if sucessfull returns a list of the courses associated with the specified term, else returns null
     */
    @Query("select * from course where termId = :termId")
    public List<Course> findCoursesForTerm(final int termId);

    /**
     * updateCourse, executes an update in the database for the specified course
     * @param course the Course object to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCourse(Course course);

    /**
     * deleteCourse, deletes the specified course from the database
     * @param course
     */
    @Delete
    void deleteCourse(Course course);
    /**
     * removeAllCourses, deletes all the courses from the course table
     */
    @Query("delete from course")
    void removeAllCourses();

}
