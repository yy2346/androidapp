package com.example.androidapp

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.Photo
import com.example.androidapp.PhotoAdapter
import kotlinx.android.synthetic.main.activity_main.*

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.BaseColumns
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.FeedReaderDbHelper

import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.time.*

class MainActivity : AppCompatActivity() {

    private val dbHelper = FeedReaderDbHelper(this)

    // Log file variables
    private val fileName = "transact.log"
    private var fileData = ""
    private val file:String = fileName
    private val dateTime = LocalDateTime.now()

    private lateinit var photoAdapter: PhotoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Opening log file in APPEND mode
        var fileOutputStream:FileOutputStream
        fileOutputStream = openFileOutput(file, Context.MODE_APPEND)
        fileOutputStream.write("Session started ".toByteArray())
        fileOutputStream.write(dateTime.toString().toByteArray())
        fileOutputStream.write("\n".toByteArray())

        photoAdapter = PhotoAdapter(mutableListOf())
        rvItems.adapter = photoAdapter
        rvItems.layoutManager = LinearLayoutManager(this)
        idAddButton.setOnClickListener {

            val photoId = tvId.text.toString()

            if (photoId.isNotEmpty()){
                fileData = "Add photo: " + photoId + "\n"
                fileOutputStream.write(fileData.toByteArray())
                val photo = Photo(photoId)
                photoAdapter.addId(photo)

                tvId.text.clear()

            }

        }

        idWriteDbButton.setOnClickListener {
            for (i in 0 until photoAdapter.photos1.size) {
                fileData = "Write db: " + (photoAdapter.photos1[i]).id.toString() + "\n"
                fileOutputStream.write(fileData.toByteArray())
                insertIntoDB((photoAdapter.photos1[i]).id.toString())
            }

        }

        idReadDbButton.setOnClickListener {
            fileData = "Read from db\n"
            fileOutputStream.write(fileData.toByteArray())
            readFromDB()
        }

        idDeleteButton.setOnClickListener {

            val photoId = tvId.text.toString()
            if (photoId.isNotEmpty()){
                var count = 0

                for (i in 0 until photoAdapter.photos1.size) {
                    if ((photoId).equals((photoAdapter.photos1[i]).id.toString())) {
                        count++
                        (photoAdapter.photos1[i]).isChecked = true;
                    }
                }

                fileData = "Delete " + count + " instances from session of: " + photoId  + "\n"
                fileOutputStream.write(fileData.toByteArray())

                photoAdapter.deleteId()
                tvId.text.clear()

            }

        }

        idClearDbButton.setOnClickListener {
            fileData = "Clear db\n"
            fileOutputStream.write(fileData.toByteArray())

            clearDb()
        }

        idClearSessionButton.setOnClickListener {
            fileData = "Clear session\n"
            fileOutputStream.write(fileData.toByteArray())

            photoAdapter.clearSession()
        }

        idDisplayDbButton.setOnClickListener {
            fileData = "Display db\n"
            fileOutputStream.write(fileData.toByteArray())

            val db = dbHelper.readableDatabase
            val projection = arrayOf(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE)
            val cursor = db.query(FeedReaderContract.FeedEntry.TABLE_NAME, projection, null, null, null, null, null)

            var things = mutableListOf<String>()
            with(cursor) {
                while (moveToNext()) {
                    val thing = getString(getColumnIndexOrThrow("thing"))
                    things.add(thing)
                }
            }
            System.out.println(things.toString());
        }

        idDeleteFromDbButton.setOnClickListener {
            val photoId = tvId.text.toString()
            if (photoId.isNotEmpty()){
                fileData = "Delete all instances from db of: " + photoId  + "\n"
                fileOutputStream.write(fileData.toByteArray())

                deleteFromDb(photoId)
                tvId.text.clear()
            }
        }

        idClearLogButton.setOnClickListener {
            val dir = getFilesDir()
            val fileD = File(dir, "transact.log")
            fileD.delete()
            fileOutputStream = openFileOutput(file, Context.MODE_APPEND)

            fileData = "Log cleared and new log started at " + dateTime + "\n"
            fileOutputStream.write(fileData.toByteArray())

        }

        idReadLogButton.setOnClickListener {
            fileData = "Log read at " + dateTime + "\n"
            fileOutputStream.write(fileData.toByteArray())

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
                val photoIdR = getString(getColumnIndexOrThrow("thing"))
                val whereArgsStr = arrayOf(photoIdR)
                if (photoId.equals(photoIdR)) {
                    dbW.delete(FeedReaderContract.FeedEntry.TABLE_NAME, "thing=?", whereArgsStr)
                }
            }
        }

    }

}


// Example of file output and input code
/*
val fileOutputStream:FileOutputStream
try {
    fileOutputStream = openFileOutput(file, Context.MODE_APPEND)
    fileOutputStream.write(fileData.toByteArray())
    fileOutputStream.write(10)
} catch (e: Exception) {
    e.printStackTrace()
}
var fileInputStream: FileInputStream? = null
fileInputStream = openFileInput(fileName)
var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
//val stringBuilder: StringBuilder = StringBuilder()
var text: String? = null
while ({ text = bufferedReader.readLine(); text }() != null) {
    System.out.println(text)
    //stringBuilder.append(text)
}
//System.out.println(stringBuilder.toString())
 */
