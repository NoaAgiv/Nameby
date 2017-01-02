package com.agiv.names2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Collections;
import java.util.Comparator;

import static com.agiv.names2.NameTagger.addName;
import static com.agiv.names2.NameTagger.context;
import static com.agiv.names2.NameTagger.getDisloveImage;
import static com.agiv.names2.NameTagger.getLoveImage;
import static com.agiv.names2.NameTagger.getLovedAdapter;
import static com.agiv.names2.NameTagger.getLovedNamesListView;
import static com.agiv.names2.NameTagger.getUnlovedAdapter;
import static com.agiv.names2.NameTagger.getUnlovedNamesListView;
import static com.agiv.names2.NameTagger.getUntaggedNamesView;
import static com.agiv.names2.NameTagger.initData;
import static com.agiv.names2.NameTagger.lovedNames;
import static com.agiv.names2.NameTagger.unlovedNames;

public class InitiationScreen extends AppCompatActivity {

    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        setContentView(R.layout.initiation_screen);
        ImageButton chooseFemale = (ImageButton) findViewById(R.id.choose_sex_female);
        final SharedPreferences sharedPref= getSharedPreferences("group_settings", 0);
        final SharedPreferences.Editor editor= sharedPref.edit();
        chooseFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("sex", GroupSettings.Sex.FEMALE.ordinal());
                editor.commit();
                //GroupSettings.setSex(GroupSettings.Sex.FEMALE);
                Intent menuIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(menuIntent);
            }});
        ImageButton chooseMale = (ImageButton) findViewById(R.id.choose_sex_male);
        chooseMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt("sex", GroupSettings.Sex.MALE.ordinal());
                editor.commit();
                //GroupSettings.setSex(GroupSettings.Sex.MALE);
                Intent menuIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(menuIntent);
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

