package com.example.szymon.easylanguage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import com.example.szymon.easylanguage.DatabaseData.DatabaseInfo;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DatabaseInfo.DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void createTable(String TableName, String primaryLanguage, String destinationLanguage) {
        String SQL_CREATE_ENTRIES =  "CREATE TABLE " + TableName + " (" +
                                        DatabaseInfo.COLUMN_WORD + " TEXT," +
                                        DatabaseInfo.COLUMN_TRANSLATED_WORD + " TEXT )";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        db.execSQL(SQL_CREATE_ENTRIES);

        String SQL_CREATE_TABLE_TR =  "CREATE TABLE IF NOT EXISTS " + DatabaseInfo.TRANSLATIONS_TABLE + "( " +
                DatabaseInfo.COLUMN_TR_FROM + " TEXT," +
                DatabaseInfo.COLUMN_TR_TO + " TEXT," +
                DatabaseInfo.COLUMN_TABLE_NAME + " TEXT )";
        db.execSQL(SQL_CREATE_TABLE_TR);
        System.out.println(SQL_CREATE_TABLE_TR);
        db.execSQL("INSERT INTO " + DatabaseInfo.TRANSLATIONS_TABLE + " (" +
                DatabaseInfo.COLUMN_TR_FROM + "," +
                DatabaseInfo.COLUMN_TR_TO + "," +
                DatabaseInfo.COLUMN_TABLE_NAME + ")" +
                " VALUES('" + primaryLanguage + "','" + destinationLanguage + "','" + TableName +
                "')");
        db.close();
    }

    public void deleteTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
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

    public void insertWord(String tableName, String primaryWord, String translatedWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + tableName + " (" +
                DatabaseInfo.COLUMN_WORD + "," +
                DatabaseInfo.COLUMN_TRANSLATED_WORD + ")" +
                " VALUES('" + primaryWord + "','" + translatedWord +
                "')");
        db.close();
    }

    public void deleteWord(String tableName, String primaryWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName + " WHERE " + DatabaseInfo.COLUMN_WORD +
                " LIKE '" + primaryWord + "'");
        db.close();
    }

    public String getLanguageDirections(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + DatabaseInfo.COLUMN_TR_FROM + "," +
                DatabaseInfo.COLUMN_TR_TO + " FROM " + DatabaseInfo.TRANSLATIONS_TABLE + " where " +
                DatabaseInfo.COLUMN_TABLE_NAME + " like '" + tableName + "'", null);
        c.moveToFirst();
        String primaryLanguage = c.getString(c.getColumnIndex(DatabaseInfo.COLUMN_TR_FROM));
        String destinationLanguage = c.getString(c.getColumnIndex(DatabaseInfo.COLUMN_TR_TO));
        db.close();
        return primaryLanguage + "-" + destinationLanguage;
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

    public ArrayList<Pair<String,String>> getWordsFromDict(String tableName) {
        ArrayList<Pair<String,String>> wordPairs = new ArrayList<Pair<String,String>>();
        ArrayList<String> arrTblNames = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + DatabaseInfo.COLUMN_WORD + "," + DatabaseInfo.COLUMN_TRANSLATED_WORD +
                                " FROM " + tableName, null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                String primaryWord = c.getString(c.getColumnIndex(DatabaseInfo.COLUMN_WORD));
                String translatedWord = c.getString(c.getColumnIndex(DatabaseInfo.COLUMN_TRANSLATED_WORD));
                wordPairs.add(new Pair<String, String>(primaryWord, translatedWord));
                c.moveToNext();
            }
        }
        return wordPairs;
    }

    public void editWord(String tableName, String originalPrimaryWord, String changedPrimaryWord, String translatedWord) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + tableName + " SET " +
                DatabaseInfo.COLUMN_WORD + "='" + changedPrimaryWord + "'," +
                DatabaseInfo.COLUMN_TRANSLATED_WORD + "='" + translatedWord + "'" +
                " WHERE " + DatabaseInfo.COLUMN_WORD + "='" + originalPrimaryWord + "'");
        db.close();
    }
}
