package com.example.szymon.easylanguage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TestActivity extends AppCompatActivity {

    int round = 0;
    int progress = 0;
    int positives = 0;
    int negatives = 0;
    String tableName;
    DatabaseHelper db;
    Map badAnswers;
    ArrayList<Pair<String, String>> words;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        db = new DatabaseHelper(getApplicationContext());
        words = new ArrayList<Pair<String, String>>();
        tableName = getIntent().getStringExtra("dictionaryName");
        words = db.getWordsFromDict(tableName);
        badAnswers = new HashMap<>();
        Collections.shuffle(words);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(words.size());
        progressBar.setProgress(round);
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

            if (words.get(round).second.toLowerCase().equals(translatedWord.toLowerCase().trim())) {
                positives++;
                hideKeyboard();
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.ic_done_black_24dp);
                imageView.setColorFilter(Color.parseColor("#66CD00"));
                checkButton.setBackgroundColor(Color.parseColor("#66CD00"));
                checkButton.setText("Next");
                words.remove(round);
                Collections.shuffle(words);
                progress++;
                progressBar.setProgress(progress);
                round--;
            }
            else {
                negatives++;
                hideKeyboard();
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.ic_clear_black_24dp);
                imageView.setColorFilter(getResources().getColor(R.color.colorAccent));
                checkButton.setText("Next");
                words.add(words.get(round));
                if(badAnswers.containsKey(words.get(round).first)) {
                    Integer n = (Integer)badAnswers.get(words.get(round).first);
                    n++;
                    badAnswers.put(words.get(round).first, n);
                }
                else {
                    badAnswers.put(words.get(round).first, 1);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setMessage("Correct answer is: \n'" + words.get(round).second.toLowerCase()+"'")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .show();
            }
            round++;
            if (round == words.size()) {
                String info = "";
                Set entrySet = badAnswers.entrySet();
                Iterator it = entrySet.iterator();
                while(it.hasNext()){
                    Map.Entry set = (Map.Entry)it.next();
                    if (set.getValue() == 1) {
                        info += "Please revise word: '" + set.getKey() + "'\n";
                    }
                    else if (set.getValue() == 2) {
                        info += "You should carefully repeat word: '" + set.getKey() + "'\n";
                    }
                    else {
                        info += "You had a real truble with word: '" + set.getKey() + "'\n";
                    }
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final String finalInfo = info;
                builder
                .setMessage("Your score is: \n" + positives + " good answers and " +
                        negatives + " bad answers\n" + info)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("Send raport to email", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                final String raport = "Your score is: \n" + positives + " good answers and " +
                                        negatives + " bad answers\n" + finalInfo;
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_SUBJECT, "My Easy Language raport");
                            email.putExtra(Intent.EXTRA_TEXT, raport);
                            email.setType("message/rfc822");
                            startActivity(Intent.createChooser(email, "Choose an Email client :"));
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
