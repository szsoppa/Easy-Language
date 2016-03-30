package com.example.szymon.easylanguage;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddItemActivity extends AppCompatActivity {

    final String API_KEY = "trnsl.1.1.20160316T233741Z.893cd43240cfcccf.c7aebbae72fd9034a00cb23cb750ffc91a49a12b";
    final String YANDEX_GET_TR_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?" + "key=" + API_KEY;
    String primaryWord;
    String translatedWord;
    String tableName;
    String languageDirection;
    Boolean editable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add new item");
        tableName = getIntent().getStringExtra("dictionaryName");
        languageDirection = getIntent().getStringExtra("languageDirection");
        primaryWord = getIntent().getStringExtra("primaryWord");
        translatedWord = getIntent().getStringExtra("translatedWord");
        if (primaryWord != null && translatedWord != null) {
            EditText editText_primaryWord = (EditText) findViewById(R.id.editText_primaryWord);
            EditText editText_translatedWord = (EditText) findViewById(R.id.editText_translatedWord);
            editText_primaryWord.setText(primaryWord);
            editText_translatedWord.setText(translatedWord);
            editable = true;
            getSupportActionBar().setTitle("Edit word");
            Button button_edit = (Button) findViewById(R.id.button_addWord);
            button_edit.setText("Confirm");
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public boolean translate(View v) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null) {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_LONG).show();
            return false;
        }
        EditText editText = (EditText) findViewById(R.id.editText_primaryWord);
        primaryWord = editText.getText().toString();
        if (primaryWord.length() == 0) {
            Toast.makeText(this, "To translate firstly you have to provide a word", Toast.LENGTH_LONG).show();
        }
        else {
            new FeedTask().execute();
        }
        return true;
    }

    public boolean addWord(View v) {
        EditText editText_primaryWord = (EditText) findViewById(R.id.editText_primaryWord);
        EditText editText_translatedWord = (EditText) findViewById(R.id.editText_translatedWord);
        primaryWord = editText_primaryWord.getText().toString().trim();
        translatedWord = editText_translatedWord.getText().toString().trim();
        if (primaryWord.equals("") || translatedWord.equals("")) {
            Toast.makeText(getApplicationContext(), "Please provide word and translation", Toast.LENGTH_LONG).show();
            return false;
        }
        DatabaseHelper db = new DatabaseHelper(this);
        if (editable) {
            String originalPrimaryWord = getIntent().getStringExtra("primaryWord");
            db.editWord(tableName, originalPrimaryWord, primaryWord, translatedWord);
        }
        else
            db.insertWord(tableName, primaryWord, translatedWord);
        this.finish();
        return true;
    }

    public class FeedTask extends AsyncTask<String, Void, String> {
        ProgressDialog myPd_ring = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                String translationUrl = YANDEX_GET_TR_URL + "&text=" + primaryWord + "&lang=" + languageDirection;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(translationUrl).build();
                Response response = client.newCall(request).execute();
                JSONArray translations = new JSONObject(response.body().string()).getJSONArray("text");
                String translatedWords = "";
                for (int i=0; i<translations.length(); i++) {
                    translatedWords += translations.getString(i);
                    if ((i+1) != translations.length())
                        translatedWords += ",";
                }
                return translatedWords;
            } catch (Exception e)
            {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            myPd_ring  = new ProgressDialog(AddItemActivity.this);
            myPd_ring.setMessage("Translating");
            myPd_ring.show();
        }

        protected void onPostExecute(String result) {
            EditText editText = (EditText) findViewById(R.id.editText_translatedWord);
            editText.setText(result);
            myPd_ring.dismiss();
        }
    }
}
