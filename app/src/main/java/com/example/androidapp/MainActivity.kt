/*
 *
 * See the following reference page - https://developer.android.com/training/data-storage/sqlite
 *
 */

package com.example.androidapptest2

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapptest2.Photo
import com.example.androidapptest2.PhotoAdapter
import kotlinx.android.synthetic.main.activity_main.*

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapptest2.FeedReaderDbHelper

class MainActivity : AppCompatActivity() {

    // The following line is required to access the database
    //      It instantiates our subclass of SQLiteOpenHelper (in the file FeedReaderDbHelper.kt)
    private val dbHelper = FeedReaderDbHelper(this)

    // The following lines of code are identical to our original Android app (without the database)
    private lateinit var photoAdapter: PhotoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoAdapter = PhotoAdapter(mutableListOf())
        rvItems.adapter = photoAdapter
        rvItems.layoutManager = LinearLayoutManager(this)
        idAddButton.setOnClickListener {

            val photoId = tvId.text.toString()

            if (photoId.isNotEmpty()){

                val photo = Photo(photoId)
                photoAdapter.addId(photo)

                tvId.text.clear()

            }

        }

        // I added this method to write the current session to the db
        idWriteDbButton.setOnClickListener {
            // The following lines of code are new and writes data from the existing session into the db
            for (i in 0 until photoAdapter.photos1.size)
                insertIntoDB((photoAdapter.photos1[i]).id.toString())

        }

        idReadDbButton.setOnClickListener {
            readFromDB()
        }

        idDeleteButton.setOnClickListener {

            val photoId = tvId.text.toString()
            if (photoId.isNotEmpty()){

                for (i in 0 until photoAdapter.photos1.size) {
                    if ((photoId).equals((photoAdapter.photos1[i]).id.toString())) {
                        (photoAdapter.photos1[i]).isChecked = true;
                    }
                }
                photoAdapter.deleteId()
                tvId.text.clear()

            }

        }

        idClearDbButton.setOnClickListener {
            clearDb()
        }

        idClearSessionButton.setOnClickListener {
            photoAdapter.clearSession()
        }

        idDisplayDbButton.setOnClickListener {
            val db = dbHelper.readableDatabase
            val projection = arrayOf(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
            //val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} = ?"
            //val selectionArgs = arrayOf("1")
            //val sortOrder = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"
            val cursor = db.query(FeedReaderContract.FeedEntry.TABLE_NAME, projection, null, null, null, null, null)

            var things = mutableListOf<String>()
            with(cursor) {
                while (moveToNext()) {
                    val thing = getString(getColumnIndexOrThrow("thing"))
                    System.out.println("Thing: + " + thing)
                    things.add(thing)
                }
            }
            System.out.println(things.toString());
        }

        idDeleteFromDbButton.setOnClickListener {
            val photoId = tvId.text.toString()
            if (photoId.isNotEmpty()){
                deleteFromDb(photoId)
                tvId.text.clear()
            }
        }

    }

    // This is the insertIntoDb method that is called from the MainActivity class above
    //  It inserts data into the database by passing a ContentValues object to the insert() method
    fun insertIntoDB(photoId: String){

        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            //put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, "field")
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, photoId)
        }

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db?.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
        /*
         * An explanation for the above line -
         *  The first argument for insert() is simply the table name.
         *  The second argument tells the framework what to do in the event that the ContentValues is empty (i.e., you did not put any values).
         *      If you specify the name of a column, the framework inserts a row and sets the value of that column to null.
         *      If you specify null, like in this code sample, the framework does not insert a row when there are no values.
         *  The insert() methods returns the ID for the newly created row, or it will return -1 if there was an error inserting the data.
         *      This can happen if you have a conflict with pre-existing data in the database.
         *
         */
    }

    fun readFromDB() {
        photoAdapter.clearSession()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
        //val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} = ?"
        //val selectionArgs = arrayOf("1")
        //val sortOrder = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"
        val cursor = db.query(FeedReaderContract.FeedEntry.TABLE_NAME, projection, null, null, null, null, null)

        var i = 0;

        with(cursor) {
            while (moveToNext()) {
                val photoId = getString(getColumnIndexOrThrow("thing"))

                if (photoId.isNotEmpty()){

                    val photo = Photo(photoId)
                    photoAdapter.addId(photo)
                    i = 1;

                }
            }
        }

        if (i == 0)
            photoAdapter.clearSession()

    }


    fun clearDb() {
        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        // Deletes all data from the things
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, null, null)
    }

    fun deleteFromDb(photoId: String) {
        val dbW = dbHelper.writableDatabase
        val dbR = dbHelper.readableDatabase
        val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
        val cursor = dbR.query(FeedReaderContract.FeedEntry.TABLE_NAME, projection, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                //val basecolId = getString(getColumnIndexOrThrow(BaseColumns._ID))
                val photoIdR = getString(getColumnIndexOrThrow("thing"))
                val whereArgsStr = arrayOf(photoIdR)
                if (photoId.equals(photoIdR)) {
                    System.out.println("Just before delete")
                    dbW.delete(FeedReaderContract.FeedEntry.TABLE_NAME, "thing=?", whereArgsStr)
                }
            }
        }

    }

}


/*
 * Read information from a database
 *  To read from a database, use the query() method, passing it your selection criteria and desired columns.
 *      The method combines elements of insert() and update(), except the column list defines the data you want to fetch (the "projection"), rather than the data to insert.
 *      The results of the query are returned to you in a Cursor object.
 *
 */

/* Note - I am commenting out this section to use my own readFromDB() method
fun readFromDB() {

    // Gets the data repository in read mode
    val db = dbHelper.readableDatabase
    // The following two lines (of which only one is active) define a projection that specifies which columns from the database you will actually use after this query
        //val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
    val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)

    // The following two lines (of which only one is active) filter results WHERE "title" = 'My Title'
        //val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE} = ?"
    val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} = ?"

    // I am not sure what the following line does
    //  From my research, selectionArgs seems to be used to replace any question marks in the selection string
    //      Maybe it is supposed to replace the ? in the line above
    val selectionArgs = arrayOf("1")

    // The next line indicates how you want the results sorted in the resulting Cursor
    //      Cursors in Android are what contain the result set of a query made against a database
    //      See - https://www.informit.com/articles/article.aspx?p=2731932&seqNum=4
    val sortOrder = "${FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE} DESC"

    /* This line is not needed as it is the wrong base type
    // The next line is creating a new Mutable List of Long values with the reference variable itemIds3
    val itemIds3 = mutableListOf<Long>()
     */

    /*
    //stackoverflow.com/questions/55787574/how-can-i-view-these-values-in-the-database-after-i-insert-them-so-that-i-can-ac

    //one row:
     */

    /*
     * This section is a little difficult to understand -
     *  First: https://stackoverflow.com/questions/9938471/what-is-use-of-cursor-in-android-development
     *
     * Cursor is the Interface which represents a 2 dimensional table of any database.
     *  When you try to retrieve some data using SELECT statement, then the database will first create a CURSOR object and return its reference to you.
     * The pointer of this returned reference is pointing to the 0th location which is otherwise called as before first location of the Cursor,
     *  so when you want to retrieve data from the cursor, you have to first move to the first record so we have to use moveToFirst
     * When you invoke moveToFirst() method on the Cursor, it takes the cursor pointer to the first location
     *  Now you can access the data present in the first record
     *
     * In simple words, Cursor is an Interface which returns collection of your query data.
     *  moveToFirst() is used to point the cursor position from where you want to get data from your cursor.
     *  There are methods moveToLast(), moveToNext(), moveToPrevious(), moveToPosition(position) by which you can iterate through your cursor by desired way
     *
     * See - https://stackoverflow.com/questions/55787574/how-can-i-view-these-values-in-the-database-after-i-insert-them-so-that-i-can-ac
     *  for examples of using Cursors
     *
     */
    // The following Cursor starts with a query call to dbHelper.readDatabase (db)
    val cursor2: Cursor = db.query(

        // See - https://www.informit.com/articles/article.aspx?p=2731932&seqNum=4
        // This next line is the name of our table from FeedReaderContract.kt which is favorite_things2
        FeedReaderContract.FeedEntry.TABLE_NAME,

        // This next line indicates that we want to return an array containing the specified elements
        // See - https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/array-of.html
        //  and - https://www.geeksforgeeks.org/kotlin-array/
        arrayOf(

            //FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
            FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE,
            BaseColumns._ID
        ),
        // So the above code would return an array of ('thing', BaseColumns._ID)

        // I am not sure what the following line of code does
        //  From what I can tell, it returns a string with the _id of the 'thing' concatenated with =?
        //      E.g., so a 'thing' with id 1 and the value apple would be returned as 1=?
        BaseColumns._ID + "=?",

        // The following line of code returns an array containing one String value of 10
        arrayOf("10"),

        // The following three nulls indicate that our db.query has no values for groupBy, having, and orderBy
        null, null, null
    )

    // The following line of code moves the cursor to the next row relative to the current position
    while (cursor2.moveToNext()) {
        /*println(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " is " + cursor2.getString(0)
                + " " + FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " is " + cursor2.getString(1)
        );*/
        println(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " is " + cursor2.getString(0));
    }
    cursor2.close()

    // all rows from the table
    // The following line of code returns all rows from the table
    val cursor3: Cursor = db.query(FeedReaderContract.FeedEntry.TABLE_NAME, null, null, null, null, null, null)

    while (cursor3.moveToNext()) {
        /*println(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " is " + cursor3.getString(1)
                + " " + FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " is " + cursor3.getString(2)
        );*/
        println(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + " is " + cursor3.getString(1));
    }
    cursor3.close()
}
 */