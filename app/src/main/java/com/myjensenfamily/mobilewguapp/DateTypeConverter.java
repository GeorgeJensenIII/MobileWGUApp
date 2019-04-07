/**
 * DateTypeConverter, converts between Date and Long so dates can be saved in SQLite
 */

package com.myjensenfamily.mobilewguapp;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;



public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }
}