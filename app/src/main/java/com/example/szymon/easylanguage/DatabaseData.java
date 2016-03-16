package com.example.szymon.easylanguage;

import android.provider.BaseColumns;

public class DatabaseData {

    public DatabaseData() {}

    public static abstract class DatabaseInfo implements BaseColumns {
        public static final String DATABASE_NAME = "Dictionaries.db";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_TRANSLATED_WORD = "translated_word";
    }
}
