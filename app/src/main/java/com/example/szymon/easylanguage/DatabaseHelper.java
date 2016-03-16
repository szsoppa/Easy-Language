package com.example.szymon.easylanguage;

import com.example.szymon.easylanguage.DatabaseData.DatabaseInfo;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseInfo.DATABASE_NAME + " (" +
                    DatabaseInfo.COLUMN_WORD + "TEXT," +
                    DatabaseInfo.COLUMN_TRANSLATED_WORD + " )";


    public DatabaseHelper(Context context) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void createTable(String TableName) {
        String SQL_CREATE_ENTRIES =  "CREATE TABLE " + TableName + " (" +
                                        DatabaseInfo.COLUMN_WORD + "TEXT," +
                                        DatabaseInfo.COLUMN_TRANSLATED_WORD + " )";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        db.execSQL(SQL_CREATE_ENTRIES);
        db.close();
    }

    public ArrayList<String> getTableNames() {
        ArrayList<String> arrTblNames = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name not like 'android_metadata'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                arrTblNames.add( c.getString( c.getColumnIndex("name")) );
                c.moveToNext();
            }
        }
        return arrTblNames;
    }

    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(SQL_CREATE_ENTRIES);
//        Log.d("Database operations", "Database created");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL("DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAM;);
//        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
