package com.example.szymon.easylanguage;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(getApplicationContext());
        initializeMenu();
    }

    private void initializeMenu() {
        ArrayList<String> tableNames = db.getTableNames();
        TextView textView = (TextView) findViewById(R.id.textView_dictionaries);
        if (tableNames.size() == 1) {
            textView.setText("You have no dictionaries at this moment.\nPlease create one.");
        }
        else {
            textView.setText("My dictionaries");
            ListView listView = (ListView) findViewById(R.id.listView_dictionaries);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    tableNames);
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent dictionaryActivity = new Intent(getApplicationContext(), DictionaryActivity.class);
                    startActivity(dictionaryActivity);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent newTableActivity = new Intent(this, NewTableActivity.class);
                startActivity(newTableActivity);
                initializeMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
