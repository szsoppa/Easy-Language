package com.example.szymon.easylanguage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewTableActivity extends AppCompatActivity {

    final String API_KEY = "trnsl.1.1.20160316T233741Z.893cd43240cfcccf.c7aebbae72fd9034a00cb23cb750ffc91a49a12b";
    final String YANDEX_GET_LANG_URL = "https://translate.yandex.net/api/v1.5/tr.json/getLangs?" + "key=" + API_KEY + "&ui=en";
    List<String> langs = new ArrayList<String>();
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_table);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Dictionary");
        new FeedTask().execute();
    }

    public void create(View v) {
        EditText textView = (EditText) findViewById(R.id.editText_dicionaryName);
        String dictionaryName = textView.getText().toString();
        if (dictionaryName.length() == 0) {
            Toast.makeText(this, "Please provide dictionary name.", Toast.LENGTH_LONG).show();
        } else {
            Spinner primaryLanguageSpinner = (Spinner) findViewById(R.id.spinner_primaryLanguage);
            Spinner destinationLanguageSpinner = (Spinner) findViewById(R.id.spinner_destinationLanguage);
            String primaryLanguage = primaryLanguageSpinner.getSelectedItem().toString();
            String destinationLanguage = destinationLanguageSpinner.getSelectedItem().toString();
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            db.createTable(dictionaryName.replaceAll(" ", "_").toLowerCase(),
                            getAbbreviation(primaryLanguage), getAbbreviation(destinationLanguage));
            this.finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public class FeedTask extends AsyncTask<String, Void, String> {
        ProgressDialog myPd_ring = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(YANDEX_GET_LANG_URL).build();
                Response response = client.newCall(request).execute();
                jsonObject = new JSONObject(response.body().string()).getJSONObject("langs");
                return jsonObject.toString();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            myPd_ring = new ProgressDialog(NewTableActivity.this);
            myPd_ring.setMessage("Fetching languages");
            myPd_ring.show();
        }

        protected void onPostExecute(String result) {
            Iterator<?> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                try {
                    langs.add(jsonObject.getString(key));
                    Collections.sort(langs);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item);
                    adapter.addAll(langs);
                    Spinner primaryLanguage = (Spinner) findViewById(R.id.spinner_primaryLanguage);
                    Spinner destinationLanguage = (Spinner) findViewById(R.id.spinner_destinationLanguage);
                    primaryLanguage.setAdapter(adapter);
                    destinationLanguage.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            myPd_ring.dismiss();
        }
    }

    private String getAbbreviation(String language) {
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                String lng =jsonObject.getString(key);
                if (lng.equals(language)) return key;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
