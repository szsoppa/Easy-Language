package com.example.szymon.easylanguage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DictionaryActivity extends AppCompatActivity {

    DatabaseHelper db;
    String tableName;
    String languageDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tableName = getIntent().getStringExtra("dictionaryName").replaceAll("_", " ");
        setContentView(R.layout.activity_dictionary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(capitalizeString(tableName));
        db = new DatabaseHelper(getApplicationContext());
        initializeMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dictionary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addItem:
                Intent addItemActivity = new Intent(this, AddItemActivity.class);
                tableName = getIntent().getStringExtra("dictionaryName");
                languageDirection = getIntent().getStringExtra("languageDirection");
                addItemActivity.putExtra("dictionaryName", tableName);
                addItemActivity.putExtra("languageDirection", languageDirection);
                startActivity(addItemActivity);
                return true;
            case R.id.action_list:
                return true;
            case R.id.list_settings:
                return true;
            case R.id.list_remove:
                confirmDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void initializeMenu() {
        final ArrayList<Pair<String, String>> words = db.getWordsFromDict(tableName);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        TextView textView = (TextView) findViewById(R.id.textView_dictionaryInfo);
        if (words.size() == 0) {
            textView.setText("Your dictionary is empty at this moment.");
        }
        else {
            textView.setText("My words");
            ListView listView = (ListView) findViewById(R.id.listView_words);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this, R.layout.list_item);
            for (int i=0; i<words.size(); i++) {
                String item = i+1 + ". " + words.get(i).first + "\nTranslation: " + words.get(i).second;
                arrayAdapter.add(item);
            }
            listView.setAdapter(arrayAdapter);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    return false;
                }
            });
        }
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Are you sure?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        db.deleteTable(tableName);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private String capitalizeString(String text) {
        return text.substring(0,1).toUpperCase()+text.substring(1, text.length());
    }
}
