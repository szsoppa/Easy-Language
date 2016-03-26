package com.example.szymon.easylanguage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class TestActivity extends AppCompatActivity {

    int round = 0;
    int positives = 0;
    int negatives = 0;
    String tableName;
    DatabaseHelper db;
    ArrayList<Pair<String, String>> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        db = new DatabaseHelper(getApplicationContext());
        words = new ArrayList<Pair<String, String>>();
        Collections.shuffle(words);
        tableName = getIntent().getStringExtra("dictionaryName");
        words = db.getWordsFromDict(tableName);
        TextView textView_primaryWord = (TextView) findViewById(R.id.textView_primaryWord);
        textView_primaryWord.setText("Word: " + words.get(round).first);
    }

    public void check(View v) {
        Button checkButton = (Button) findViewById(R.id.button_check);
        EditText editText = (EditText) findViewById(R.id.editText);
        if (checkButton.getText().equals("Next"))
        {
            TextView textView_primaryWord = (TextView) findViewById(R.id.textView_primaryWord);
            textView_primaryWord.setText("Word: " + words.get(round).first);
            editText.setText("");
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageDrawable(null);
            checkButton.setText("Check");
            checkButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else {
            String translatedWord = editText.getText().toString();

            if (words.get(round).second.equals(translatedWord)) {
                hideKeyboard();
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.ic_done_black_24dp);
                imageView.setColorFilter(Color.parseColor("#66CD00"));
                checkButton.setBackgroundColor(Color.parseColor("#66CD00"));
                checkButton.setText("Next");
                positives++;
            }
            else {
                negatives++;
                hideKeyboard();
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.ic_clear_black_24dp);
                imageView.setColorFilter(getResources().getColor(R.color.colorAccent));
                checkButton.setText("Next");
            }
            round++;
            if (round == words.size()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setMessage("Your score is: " + positives + " good answers and " +
                                                        negatives + " bad answers")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .show();
            }
            else {
                checkButton.setText("Next");
            }
        }

    }

    public void endTest(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Are you sure you want to end test now?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
