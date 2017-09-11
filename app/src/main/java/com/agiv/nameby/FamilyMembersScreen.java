package com.agiv.nameby;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

import static com.agiv.nameby.Settings.isFamilyMembersEdited;

/**
 * Created by Noa Agiv on 1/13/2017.
 */

public class FamilyMembersScreen extends AppCompatActivity {

    private GoogleApiClient client;
    private List<String> users;
    private Intent mainIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mainIntent = new Intent(getBaseContext(), MainActivity.class);
        Settings.init(getSharedPreferences("group_settings", 0));
//        databaseAccess = DbAccess.getInstance(this);
//        databaseAccess.open();
//        users = databaseAccess.getUsers();
        users = new ArrayList<String>(){{
           add("נעה");
            add("ניר");
        }};
//        databaseAccess.close();
        if (isFamilyMembersEdited()){
            Settings.setGreenUser(users.get(0));
            Settings.setYellowUser(users.get(1));
            Settings.setCurrentUser(users.get(0));
            startActivity(mainIntent);
            return;
        }
        setContentView(R.layout.family_members_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.icon);
        ImageButton nextButton = (ImageButton) findViewById(R.id.next);

        final EditText greenUser = (EditText) findViewById(R.id.green_user_name);
        greenUser.setImeOptions(EditorInfo.IME_ACTION_DONE);
        greenUser.setText(users.get(0));
        final EditText yellowUser = (EditText) findViewById(R.id.yellow_user_name);
        yellowUser.setImeOptions(EditorInfo.IME_ACTION_DONE);
        yellowUser.setText(users.get(1));


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String greenName = greenUser.getText().toString();
                String yellowName = yellowUser.getText().toString();
                if (greenName.equals(yellowName) || greenName.isEmpty() || yellowName.isEmpty()) {
                    showErrorAlert();
                }
                else{
                    setUsersAndContinue(greenName, yellowName);
                }

            }});
    }

    private void setUsersAndContinue(String greenName, String yellowName){
        Settings.setFamilyMembersEdited(true);
//        if (!greenName.equals(users.get(0))){
//            databaseAccess.open();
//            databaseAccess.editUserName(users.get(0), greenName);
//            databaseAccess.close();
//        }
//        if (!yellowName.equals(users.get(1))){
//            databaseAccess.open();
//            databaseAccess.editUserName(users.get(1), yellowName);
//            databaseAccess.close();
//        }
        Settings.setGreenUser(greenName);
        Settings.setYellowUser(yellowName);
        Settings.setCurrentUser(greenName);
        startActivity(mainIntent);
    }

    private void showErrorAlert(){
        new AlertDialog.Builder(FamilyMembersScreen.this)
                .setTitle(R.string.error_dialog_title)
                .setMessage(R.string.error_set_usernames_dialog_body)
                .setCancelable(false)
                .setPositiveButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .show();
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

