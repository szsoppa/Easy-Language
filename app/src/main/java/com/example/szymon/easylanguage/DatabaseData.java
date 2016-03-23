package com.example.szymon.easylanguage;

import android.provider.BaseColumns;

public class DatabaseData {

    public DatabaseData() {}

    public static abstract class DatabaseInfo implements BaseColumns {
        public static final String DATABASE_NAME = "Dictionaries.db";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_TRANSLATED_WORD = "translated_word";
        public static final String COLUMN_TR_FROM = "translate_from";
        public static final String COLUMN_TR_TO = "translate_to";
        public static final String COLUMN_TABLE_NAME = "table_name";
        public static final String TRANSLATIONS_TABLE = "Dictionary_Tr";
    }
}
