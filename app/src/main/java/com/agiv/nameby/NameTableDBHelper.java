package com.agiv.nameby;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
* Created by Noa Agiv on 12/11/2016.
*/

public class NameTableDBHelper extends SQLiteAssetHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "names.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE_NAMES " + NameContract.NameEntry.TABLE_NAMES + " (" +
                    NameContract.NameEntry._ID + " INTEGER PRIMARY KEY," +
                    NameContract.NameEntry.TABLE_NAMES_NAME + TEXT_TYPE + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE_NAMES IF EXISTS " + NameContract.NameEntry.TABLE_NAMES;

    private Context context;

    public NameTableDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(SQL_CREATE_ENTRIES);
//    }
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
////        // This database is only a cache for online data, so its upgrade policy is
////        // to simply to discard the data and start over
////        db.execSQL(SQL_DELETE_ENTRIES);
////        onCreate(db);
//    }
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
////        onUpgrade(db, oldVersion, newVersion);
//    }
}
