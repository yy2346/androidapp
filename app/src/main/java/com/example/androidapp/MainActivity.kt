package com.example.androidapp

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

    private val dbHelper = FeedReaderDbHelper(this)

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

        idWriteDbButton.setOnClickListener {
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

    fun insertIntoDB(photoId: String){

        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, photoId)
        }

        val newRowId = db?.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
    }

    fun readFromDB() {
        photoAdapter.clearSession()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
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
        val db = dbHelper.writableDatabase

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
