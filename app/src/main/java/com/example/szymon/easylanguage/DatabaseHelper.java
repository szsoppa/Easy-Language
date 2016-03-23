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

    public DatabaseHelper(Context context) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void createTable(String TableName) {
        String SQL_CREATE_ENTRIES =  "CREATE TABLE " + TableName + " (" +
                                        DatabaseInfo.COLUMN_WORD + "TEXT," +
                                        DatabaseInfo.COLUMN_TRANSLATED_WORD + "TEXT )";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        db.execSQL(SQL_CREATE_ENTRIES);

        String SQL_CREATE_TABLE_TR =  "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TRANSLATIONS_TABLE + " (" +
                DatabaseInfo.COLUMN_TR_FROM + "TEXT," +
                DatabaseInfo.COLUMN_TR_TO + "TEXT," +
                DatabaseInfo.COLUMN_TABLE_NAME + "TEXT )";
        db.execSQL(SQL_CREATE_TABLE_TR);
        db.close();
    }

    public void deleteTable(String TableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        db.close();
    }

    public ArrayList<String> getTableNames() {
        ArrayList<String> arrTblNames = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name not in ('android_metadata', 'Dictionary_Tr')", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                arrTblNames.add( c.getString( c.getColumnIndex("name")).replaceAll("_", " ") );
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
