package com.agiv.names2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.List;

import static com.agiv.names2.GroupSettings.getCurrentUser;
import static com.agiv.names2.GroupSettings.isFamilyMembersEdited;
import static com.agiv.names2.GroupSettings.setCurrentUser;
import static com.agiv.names2.GroupSettings.sex;

/**
 * Created by Noa Agiv on 1/13/2017.
 */

public class FamilyMembersScreen extends AppCompatActivity {

    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        final Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
        GroupSettings.init(getSharedPreferences("group_settings", 0));
        final DbAccess databaseAccess = DbAccess.getInstance(this);
        databaseAccess.open();
        final List<String> users = databaseAccess.getUsers();
        databaseAccess.close();
        if (isFamilyMembersEdited()){
            GroupSettings.setGreenUser(users.get(0));
            GroupSettings.setYellowUser(users.get(1));
            GroupSettings.setCurrentUser(users.get(0));
            startActivity(mainIntent);
            return;
        }
        setContentView(R.layout.family_members_screen);
        ImageButton chooseFemale = (ImageButton) findViewById(R.id.next);

        final EditText greenUser = (EditText) findViewById(R.id.green_user_name);
        greenUser.setImeOptions(EditorInfo.IME_ACTION_DONE);
        greenUser.setText(users.get(0));
        final EditText yellowUser = (EditText) findViewById(R.id.yellow_user_name);
        yellowUser.setImeOptions(EditorInfo.IME_ACTION_DONE);
        yellowUser.setText(users.get(1));


        chooseFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String greenName = greenUser.getText().toString();
                String yellowName = yellowUser.getText().toString();
                GroupSettings.setFamilyMembersEdited(true);
                if (!greenName.equals(users.get(0))){
                    databaseAccess.open();
                    databaseAccess.editUserName(users.get(0), greenName);
                    databaseAccess.close();
                }
                if (!yellowName.equals(users.get(1))){
                    databaseAccess.open();
                    databaseAccess.editUserName(users.get(1), yellowName);
                    databaseAccess.close();
                }
                GroupSettings.setGreenUser(greenName);
                GroupSettings.setYellowUser(yellowName);
                GroupSettings.setCurrentUser(greenName);
                startActivity(mainIntent);
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

