/*
 * From https://developer.android.com/training/data-storage/sqlite
 *
 * Create a database using an SQL helper
 *  Once you have defined how your database looks, you should implement methods that create and maintain the database and tables.
 *
 * Just like files that you save on the device's internal storage, Android stores your database in your app's private folder.
 *  Your data is secure, because by default this area is not accessible to other apps or the user.
 *
 * The SQLiteOpenHelper class contains a useful set of APIs for managing your database.
 *  When you use this class to obtain references to your database, the system performs the potentially long-running operations of
 *      creating and updating the database only when needed and not during app startup.
 *  All you need to do is call getWritableDatabase() or getReadableDatabase().
 *
 * Note: Because they can be long-running, be sure that you call getWritableDatabase() or getReadableDatabase() in a background thread. See Threading on Android for more information.
 *
 * To use SQLiteOpenHelper, create a subclass that overrides the onCreate() and onUpgrade() callback methods
 *      You may also want to implement the onDowngrade() or onOpen() methods, but they are not required.
 *
 * SQLiteOpenHelper - https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper
 *
 */

package com.example.androidapptest2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

// The following are some typical statements that create a table
private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE ${FeedReaderContract.FeedEntry.TABLE_NAME} (" + "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} TEXT)"
    //"${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} TEXT,"

// The following is a typical statement that deletes a table
private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedReaderContract.FeedEntry.TABLE_NAME}"

// The following class (a subclass of SQLiteOpenHelper) demonstrates an implementation of SQLiteOpenHelper
//      To use SQLiteOpenHelper, this subclass overrides the onCreate() and onUpgrade() callback methods (required)
//          As well as the optional onDownGrade() method
class FeedReaderDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MyDBx.db"
    }
}