package com.example.szymon.easylanguage;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
         OnConnectionFailedListener {

    int action = -1;
    private DatabaseHelper db;
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private static final int REQUEST_CODE_OPENER = 1;
    private static final int RESTORE_BACKUP = 1;
    private static final int UPLOAD_BACKUP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(getApplicationContext());
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
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() == null && item.getItemId() != R.id.list_settings) {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_LONG).show();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.action_add:
                Intent newTableActivity = new Intent(this, NewTableActivity.class);
                startActivity(newTableActivity);
                return true;
            case R.id.list_backup:
                action = UPLOAD_BACKUP;
                initializeDrive();
                return true;
            case R.id.list_restore:
                action = RESTORE_BACKUP;
                initializeDrive();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void upload(final String titl, final File file, final String mime) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && titl != null  && file != null) try {
            Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveContentsResult contRslt) {
                    if (contRslt.getStatus().isSuccess()){
                        DriveContents cont = contRslt.getDriveContents();
                        if (cont != null && file2Os(cont.getOutputStream(), file)) {
                            MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(titl).setMimeType(mime).build();
                            Drive.DriveApi.getRootFolder(mGoogleApiClient).createFile(mGoogleApiClient, meta, cont).setResultCallback(
                                    new ResultCallback<DriveFolder.DriveFileResult>() {
                                        @Override
                                        public void onResult(@NonNull DriveFolder.DriveFileResult fileRslt) {
                                            if (fileRslt.getStatus().isSuccess()) {
//                                                fileRslt.getDriveFile();
                                            }
                                        }
                                    }
                            );
                        }
                    }
                }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    static boolean file2Os(OutputStream os, File file) {
        boolean bOK = false;
        InputStream is = null;
        if (file != null && os != null) try {
            byte[] buf = new byte[4096];
            is = new FileInputStream(file);
            int c;
            while ((c = is.read(buf, 0, buf.length)) > 0)
                os.write(buf, 0, c);
            bOK = true;
        } catch (Exception e) {e.printStackTrace();}
        finally {
            try {
                os.flush(); os.close();
                if (is != null )is.close();
            } catch (Exception e) {e.printStackTrace();}
        }
        return  bOK;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMenu();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (action == RESTORE_BACKUP) {
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[] { "application/x-sqlite3" })
                    .build(mGoogleApiClient);
            try {
                startIntentSenderForResult(
                        intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "Unable to send intent");
            }
        }
        else if (action == UPLOAD_BACKUP) {
            Context ctx = getApplicationContext();
            final String inFileName = "/data/data/" +ctx.getPackageName() +
                    "/databases/"+DatabaseData.DatabaseInfo.DATABASE_NAME;
            File file = ctx.getDatabasePath(inFileName);
            upload("easy_language_backup.db", file, "application/x-sqlite3");
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    private void initializeDrive() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient,driveId);
                    file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                            .setResultCallback(contentsOpenedCallback);
                    Toast.makeText(this, "Dictionaries have been restored", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    final String outFileName = "/data/data/" +getApplicationContext().getPackageName() +
                            "/databases/"+DatabaseData.DatabaseInfo.DATABASE_NAME;
                    db.close();
                    getApplicationContext().deleteDatabase(DatabaseData.DatabaseInfo.DATABASE_NAME);
                    DriveContents contents = result.getDriveContents();
                    InputStream inputStream = contents.getInputStream();

                    try {
                        OutputStream mOutput = new FileOutputStream(outFileName);
                        byte[] mBuffer = new byte[1024];
                        int mLength;
                        while ((mLength = inputStream.read(mBuffer))>0)
                        {
                            mOutput.write(mBuffer, 0, mLength);
                        }
                        mOutput.flush();
                        mOutput.close();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    initializeMenu();
                }
            };

    private String capitalizeString(String text) {
        return text.substring(0,1).toUpperCase()+text.substring(1, text.length());
    }
}
