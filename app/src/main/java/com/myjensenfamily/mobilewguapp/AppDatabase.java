/**
 * AppDatabase
 *      By: George W. Jensen III
 *      Last Update: 4/04/19
 *      This is a intilization class that instantiates the SQLite Database Objects
 */

package com.myjensenfamily.mobilewguapp;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;



@Database(entities = {Term.class, Course.class, Assessment.class, Mentor.class }, version = 18, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract AssessmentDao assessmentDao();
    public abstract MentorDao mentorDao();


    public static AppDatabase getDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "userdatabase").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }


    public static void destroyInstance(){
        INSTANCE = null;
    }


    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
