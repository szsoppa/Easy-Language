package com.example.szymon.easylanguage;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onResume() {
        super.onResume();
        initializeMenu();
    }

    private void initializeMenu() {
        final ArrayList<String> tableNames = db.getTableNames();
        TextView textView = (TextView) findViewById(R.id.textView_dictionaries);
        if (tableNames.size() == 0) {
            textView.setText("You have no dictionaries at this moment.\nPlease create one.");
        }
        else {
            textView.setText("My dictionaries");
            ListView listView = (ListView) findViewById(R.id.listView_dictionaries);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    R.layout.list_item);
            for (int i=0; i<tableNames.size(); i++) {
                arrayAdapter.add(i+1 + ". " + capitalizeString(tableNames.get(i)));
            }
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent dictionaryActivity = new Intent(getApplicationContext(), DictionaryActivity.class);
                    String tableName = tableNames.get(position);
                    dictionaryActivity.putExtra("dictionaryName", tableName.replaceAll(" ", "_").toLowerCase());
                    dictionaryActivity.putExtra("languageDirection", db.getLanguageDirections(tableName));
                    startActivity(dictionaryActivity);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
                if(cm.getActiveNetworkInfo() != null) {
                    Intent newTableActivity = new Intent(this, NewTableActivity.class);
                    startActivity(newTableActivity);
                    return true;
                }
                else {
                    Toast.makeText(this, "Please connect to internet", Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String capitalizeString(String text) {
        return text.substring(0,1).toUpperCase()+text.substring(1, text.length());
    }
}
