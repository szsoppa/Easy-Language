package com.example.szymon.easylanguage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
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
        initializeButton();
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
            case R.id.action_deleteDictionary:
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

    @Override
    protected void onResume() {
        super.onResume();
        initializeMenu();
    }

    private void initializeMenu() {
        final ArrayList<Pair<String, String>> words = db.getWordsFromDict(tableName);
        TextView textView = (TextView) findViewById(R.id.textView_dictionaryInfo);
        final ListView listView = (ListView) findViewById(R.id.listView_words);
        listView.setAdapter(null);
        if (words.size() == 0) {
            textView.setText("Your dictionary is empty at this moment.");
        }
        else {
            textView.setText("My words");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item);
            for (int i=0; i<words.size(); i++) {
                String item = i+1 + ". " + words.get(i).first + "\nTranslation: " + words.get(i).second;
                arrayAdapter.add(item);
            }
            listView.setAdapter(arrayAdapter);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, final long id) {
                    PopupMenu menu = new PopupMenu (getApplicationContext(), view);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getTitle().toString().equals(getResources().getString(R.string.remove_word))) {
                                db.deleteWord(tableName, words.get((int) id).first);
                                initializeMenu();
                            }
                            else if (item.getTitle().toString().equals(getResources().getString(R.string.edit_word))){
                                Intent addItemActivity = new Intent(getApplicationContext(), AddItemActivity.class);
                                tableName = getIntent().getStringExtra("dictionaryName");
                                languageDirection = getIntent().getStringExtra("languageDirection");
                                addItemActivity.putExtra("dictionaryName", tableName);
                                addItemActivity.putExtra("languageDirection", languageDirection);
                                addItemActivity.putExtra("primaryWord", words.get((int) id).first);
                                addItemActivity.putExtra("translatedWord", words.get((int) id).second);
                                startActivity(addItemActivity);
                            }
                            return false;
                        }
                    });
                    menu.inflate(R.menu.word_menu);
                    menu.show();
                    return true;
                }
            });
        }
    }

    private void initializeButton() {
        Button buttonTest = new Button(this);
        buttonTest.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        buttonTest.setTextColor(Color.WHITE);
        buttonTest.setText("Take test");
        LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.addView(buttonTest,0,lp);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testActivity = new Intent(getApplicationContext(), TestActivity.class);
                testActivity.putExtra("dictionaryName", tableName);
                startActivity(testActivity);
            }
        });
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(R.string.delete_dictionary_confirmation)
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
