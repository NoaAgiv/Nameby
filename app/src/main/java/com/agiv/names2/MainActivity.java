package com.agiv.names2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import static com.agiv.names2.NameTagger.*;
public class MainActivity extends AppCompatActivity {

    private GoogleApiClient client;
    private FloatingActionButton addNameButton;

//    private void populateDb() throws IOException{
//    // Check if the database exists before copying
//        boolean initialiseDatabase = this.getDatabasePath("names.db").exists();
//        if (initialiseDatabase == false) {
//
//            // Open the .db file in your assets directory
//            InputStream is = MainActivity.this.getAssets().open("names.db");
//
//            // Copy the database into the destination
//            OutputStream os = new FileOutputStream(this.getDatabasePath("names.db"));
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = is.read(buffer)) > 0){
//                os.write(buffer, 0, length);
//            }
//            os.flush();
//
//            os.close();
//            is.close();
//        }
//    }

    private void getNamesFromDb(){
        DbAccess databaseAccess = DbAccess.getInstance(this);
        databaseAccess.open();
        untaggedNames = databaseAccess.getUntaggedNames("Noa");
        lovedNames = databaseAccess.getLovedNames("Noa");
        unlovedNames = databaseAccess.getUnlovedNames("Noa");
        databaseAccess.close();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            init(MainActivity.this, this, 1);
//            populateDb();
        }
        catch (Exception e){

        }
//        getNamesFromDb();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout allTabs = (TabLayout) findViewById(R.id.tabs);
        allTabs.addTab(allTabs.newTab().setText(R.string.triage_tab), true);
        allTabs.addTab(allTabs.newTab().setText(R.string.loved_tab));
        allTabs.addTab(allTabs.newTab().setText(R.string.unloved_tab));

        allTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabName = tab.getText().toString();
                View selectedView = null;
                if (tabName.equals(getString(R.string.loved_tab))) {
                    Collections.sort(lovedNames, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                    getLovedAdapter().notifyDataSetChanged();
                    selectedView = getLovedNamesListView();
                } else if (tabName.equals(getString(R.string.unloved_tab))) {
                    Collections.sort(unlovedNames, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                    getUnlovedAdapter().notifyDataSetChanged();
                    selectedView = getUnlovedNamesListView();
                } else if (tabName.equals(getString(R.string.triage_tab))) {
                    selectedView = getUntaggedNamesView();
                }
                switchToView(selectedView);

            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        getLovedNamesListView().setOnScrollListener(new AbsListView.OnScrollListener(){
            // hide floating button when scrolling so it does not hide the list items
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int btn_initPosY=addNameButton.getScrollY();
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    addNameButton.animate().cancel();
                    addNameButton.animate().translationYBy(250);
                } else {
                    addNameButton.animate().cancel();
                    addNameButton.animate().translationY(btn_initPosY);
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        addNameButton = (FloatingActionButton) findViewById(R.id.add_name_button);
        switchToView(getUntaggedNamesView());
        addNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(MainActivity.this);


                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.add_dialog_title)
                        .setMessage(R.string.add_dialog_body)
                        .setCancelable(false)
                        .setView(input)
                        .setPositiveButton(R.string.add_approve_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] names = input.getText().toString().split("\n");
                                for (String name : names) {
                                    addName(name);
                                }
                                getLovedAdapter().notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void switchToView(View selectedView) {
        getLovedNamesListView().setVisibility(View.GONE);
        getUnlovedNamesListView().setVisibility(View.GONE);
        getUntaggedNamesView().setVisibility(View.GONE);
        selectedView.setVisibility(View.VISIBLE);
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

