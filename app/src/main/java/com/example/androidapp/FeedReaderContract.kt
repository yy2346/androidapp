/*
 *
 * From https://developer.android.com/training/data-storage/sqlite
 *
 * Save data using SQLite
 *  Saving data to a database is ideal for repeating or structured data, such as contact information.
 *      This page assumes that you are familiar with SQL databases in general and helps you get started with SQLite databases on Android.
 *      The APIs you'll need to use a database on Android are available in the android.database.sqlite package.
 *
 * Caution: Although these APIs are powerful, they are fairly low-level and require a great deal of time and effort to use:
 *      There is no compile-time verification of raw SQL queries. As your data graph changes, you need to update the affected SQL queries manually. This process can be time consuming and error prone.
 *      You need to use lots of boilerplate code to convert between SQL queries and data objects.
 *      For these reasons, we highly recommended using the Room Persistence Library as an abstraction layer for accessing information in your app's SQLite databases.
 *
 * Define a schema and contract
 * One of the main principles of SQL databases is the schema: a formal declaration of how the database is organized.
 *  The schema is reflected in the SQL statements that you use to create your database.
 *  You may find it helpful to create a companion class, known as a contract class, which explicitly specifies the layout of your schema in a systematic and self-documenting way.
 *
 * A contract class is a container for constants that define names for URIs, tables, and columns.
 *  The contract class allows you to use the same constants across all the other classes in the same package.
 *  This lets you change a column name in one place and have it propagate throughout your code.
 *
 * A good way to organize a contract class is to put definitions that are global to your whole database in the root level of the class.
 *  Then create an inner class for each table.
 *  Each inner class enumerates the corresponding table's columns.
 *
 * Note: By implementing the BaseColumns interface, your inner class can inherit a primary key field called _ID that some Android classes such as CursorAdapter expect it to have.
 *  It's not required, but this can help your database work harmoniously with the Android framework.
 * For example, the following contract defines the table name and column names for a single table representing an RSS feed:
 *
 */

import android.provider.BaseColumns

object FeedReaderContract {

    // Table contents are grouped together in an anonymous object.
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "favorite_things2"
       // const val COLUMN_NAME_TITLE = "id"
        const val COLUMN_NAME_SUBTITLE = "thing"
    }

}