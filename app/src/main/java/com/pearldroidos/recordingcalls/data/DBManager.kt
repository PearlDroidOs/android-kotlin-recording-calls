package com.pearldroidos.recordingcalls.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder

class DBManager(context: Context) {
    private val dbName = "RecordingCall"
    private val dbTable = "Tracking"
    private val colId = "ID"
    private val colPhoneNumber = "PhoneNumber"
    private val colCallingType = "CallingType"
    private val colStartDate = "StartDate"
    private val colEndDate = "EndDate"
    private val dbVersion = 1

    //Pattern: CREATE TABLE IF NOT EXISTS Notes (ID, Title, Description)
    private val sqlCreateTable =
        "CREATE TABLE IF NOT EXISTS $dbTable ($colId INTEGER PRIMARY KEY, $colPhoneNumber TEXT, $colCallingType TEXT, $colStartDate TEXT, $colEndDate TEXT);"
    private var sqlDB: SQLiteDatabase? = null


    init {
        val db = DBHelper(context, dbName, dbVersion, dbTable, sqlCreateTable)
        this.sqlDB = db.writableDatabase //DB is used from writable DB
    }

    /**
     * Pattern: INSERT INTO table_name VALUES(values of columns);
     * Example: INSERT INTO dbTable VALUES(values);
     * ContentValues: Key value pass from db
     */
    fun insert(values: ContentValues): Long {
        val id = sqlDB!!.insert(dbTable, "", values)
        return id
    }


    /**
     * Pattern: SELECT * FROM table_name WHERE condition (AND, OR) ORDER BY column_name(s) ASC;
     * Example: SELECT projectionIn[] FROM dbTable WHERE selection = selectionArgs ORDER BY sortOrder
     */
    fun query(projection:Array<String>, selection:String, selectionArgs:Array<String>, sortOrder:String) : Cursor {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbTable
        return qb.query(sqlDB, projection, selection, selectionArgs,null, null, sortOrder)
    }
}