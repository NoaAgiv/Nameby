package com.agiv.nameby;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.agiv.nameby.Settings.isHelpScreenSeen;
import static com.agiv.nameby.Settings.setIsHelpScreenSeen;

public class WelcomeScreen extends AppCompatActivity {

    private GoogleApiClient client;
    private Queue<String> texts;
    private Intent chooseSexIntent;
    private TextView welcomeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        chooseSexIntent = new Intent(getBaseContext(), ChooseSexScreen.class);
        Settings.init(getSharedPreferences("group_settings", 0));
        if (isHelpScreenSeen()){
            startActivity(chooseSexIntent);
            return;
        }
        else{
            setIsHelpScreenSeen(true);
        }
        setContentView(R.layout.welcome_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.icon);
        welcomeText = (TextView) findViewById(R.id.welcome_text);
        Button skipButton = (Button) findViewById(R.id.skip);
        ImageButton nextButton = (ImageButton) findViewById(R.id.next);
        texts = new ArrayDeque<String>() {{
            add(getText(R.string.help_text).toString());
            add(getText(R.string.initiation_help_text).toString());
            add(getText(R.string.nevigation_help_text).toString());
            add(getText(R.string.nevigation_help_text2).toString());
            add(getText(R.string.nevigation_help_text3).toString());
        }};

        welcomeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNextHelpText();
            }});

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToNextHelpText();
            }});

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(chooseSexIntent);
            }});
    }

    private void moveToNextHelpText(){
        String text = texts.poll();
        if (text != null) {
            welcomeText.setText(text);
        }
        else {
            startActivity(chooseSexIntent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

