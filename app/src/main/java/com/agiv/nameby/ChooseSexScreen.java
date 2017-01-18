package com.agiv.nameby;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class ChooseSexScreen extends AppCompatActivity {

    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        final Intent FamilyIntent = new Intent(getBaseContext(), FamilyMembersScreen.class);
        GroupSettings.init(getSharedPreferences("group_settings", 0));
        GroupSettings.Sex sex = GroupSettings.getSex();
        if (sex!=null){
            GroupSettings.setSex(sex);
            startActivity(FamilyIntent);
            return;
        }
        setContentView(R.layout.choose_sex_screen);
        ImageButton chooseFemale = (ImageButton) findViewById(R.id.choose_sex_female);

        chooseFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupSettings.setSex(GroupSettings.Sex.FEMALE);
                startActivity(FamilyIntent);
            }});
        ImageButton chooseMale = (ImageButton) findViewById(R.id.choose_sex_male);
        chooseMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupSettings.setSex(GroupSettings.Sex.MALE);
                startActivity(FamilyIntent);
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
        if (id == R.id.sex_settings) {
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

