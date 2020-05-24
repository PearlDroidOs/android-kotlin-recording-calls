package com.pearldroidos.recordingcalls.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

/**
 * SQLiteOpenHelper is a class that create database and allow user to connect DB
 * If a database is not available -> super(..) will call function to create a db
 * If the database is exist -> it will upgrade something or do another things
 *
 */
class DBHelper (
    private val context: Context,
    private val dbName: String,
    private val dbVersion: Int,
    private val dbTable: String,
    private val sqlCreateTable: String
) :
    SQLiteOpenHelper(context, dbName, null, dbVersion) {


    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(sqlCreateTable)
        Toast.makeText(context, "Database is created", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $dbTable")
    }

}