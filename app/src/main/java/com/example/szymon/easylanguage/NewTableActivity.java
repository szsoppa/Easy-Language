package com.example.szymon.easylanguage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_table);
    }

    public void create(View v) {
        EditText textView = (EditText) findViewById(R.id.editText_dicionaryName);
        String dictionaryName = textView.getText().toString();
        if (dictionaryName.length() == 0) {
            Toast.makeText(this, "Please provide dictionary name.", Toast.LENGTH_LONG).show();
        }
        else {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            db.createTable(dictionaryName);
            this.finish();
        }
    }

    public void cancel(View v) {
        this.finish();
    }
}
