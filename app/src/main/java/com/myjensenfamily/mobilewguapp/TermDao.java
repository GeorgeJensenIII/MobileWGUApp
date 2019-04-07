/**
 * TermDao, interface that facilitates communication with SQLite through the RoomDatabase Library
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
public interface TermDao {

    /**
     * addTerm, adds the specified term to the database
     *
     * @param term to be added to the database
     * @return the termID assigned to the new database record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addTerm(Term term);

    /**
     * getAllTerms, queries the database for all terms
     *
     * @return Returns a list of terms that are on the database
     */
    @Query("select * from term")
    public List<Term> getAllTerms();

    /**
     * getTerm, quires the database for the specified term
     *
     * @param termId the specified termId
     * @return the Term object for the specified term
     */
    @Query("select * from term where id = :termId LIMIT 1")
    public abstract Term getTerm(int termId);

    /**
     * updateTerm, executes an update in the database for the specified term
     *
     * @param term the Term object to be updated
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTerm(Term term);

    /**
     * deleteTerm, deletes the specified term from the database
     *
     * @param term
     */
    @Delete
    void deleteTerm(Term term);

    /**
     * removeAllTerms, deletes all the terms from the term table
     */

    @Query("delete from term")
    void removeAllTerms();

}
