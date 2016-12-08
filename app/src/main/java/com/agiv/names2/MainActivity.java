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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> names = new ArrayList() {{
        add("noa");
        add("נעה");
        add("ניר");
        add("איזמרלדה");
        add("שמוליקית");
        add("אביה");
        add("אילנה");
        add("מיה");
        add("טל");
        add("איתי");
        add("שלמה");
        add("יהודית");
        add("יוסי");
        add("שלומי");
        add("גיל");
        add("שני");
        add("אלון");
    }};
    public ArrayList<String> untaggedNames;
    public ArrayList<String> lovedNames = new ArrayList<>();
    public ArrayList<String> unlovedNames = new ArrayList<>();
    private static String END_OF_LIST = "Congrats! You tagged all the names!";
    private final Random rgenerator = new Random();
    private ListView lovedNamesListView;
    private ListView unlovedNamesListView;
    private TextView untaggedNamesView;
    private BaseAdapter lovedAdapter;
    private BaseAdapter unlovedAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private FloatingActionButton addNameButton;

    private String getNextUntaggedName() {
        if (untaggedNames.isEmpty())
            return END_OF_LIST;
        else
            return untaggedNames.get(rgenerator.nextInt(untaggedNames.size()));
    }

    private void addName(String name) {
        // replace non-letters and trip edge white spaces
        name = name.replaceAll("[^a-zA-Z ]", "").trim();
        if (name.isEmpty() || lovedNames.contains(name) || unlovedNames.contains(name))
            return;

        lovedNames.add(name);
        untaggedNames.remove(name);
        if (untaggedNamesView.getText().equals(name)) {
            untaggedNamesView.setText(getNextUntaggedName());
        }
        if (!names.contains(name)) {
            names.add(name);
            // TODO: and add to db
        }


    }

    private void markNameLoved(String name) {
        if (!name.equals(END_OF_LIST)) {
            lovedNames.add(name);
            untaggedNames.remove(name);
        }
    }

    private void markNameUnloved(String name) {
        if (!name.equals(END_OF_LIST)) {
            unlovedNames.add(name);
            untaggedNames.remove(name);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        untaggedNames = (ArrayList<String>) names.clone();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        lovedNamesListView = (ListView) findViewById(R.id.loved_names);
        lovedAdapter = new EditableListViewAdapter(lovedNames, unlovedNames, this,
                getString(R.string.mark_unloved_dialog_title), getString(R.string.mark_unloved_dialog_body), R.drawable.dislove);

        lovedNamesListView.setAdapter(lovedAdapter);

        unlovedNamesListView = (ListView) findViewById(R.id.unloved_names);

        unlovedAdapter = new EditableListViewAdapter(unlovedNames, lovedNames, this,
                getString(R.string.mark_loved_dialog_title), getString(R.string.mark_loved_dialog_body), R.drawable.love);
        unlovedNamesListView.setAdapter(unlovedAdapter);
        untaggedNamesView = (TextView) findViewById(R.id.untagged_names_view);
        untaggedNamesView.setText(getNextUntaggedName());

        untaggedNamesView.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            //            public void onSwipeTop() {
//            }
            public void onSwipeRight() {

                Toast.makeText(MainActivity.this, "unloved", Toast.LENGTH_SHORT).show();
                markNameUnloved(untaggedNamesView.getText().toString());
                untaggedNamesView.setText(getNextUntaggedName());

            }

            public void onSwipeLeft() {
                Toast.makeText(MainActivity.this, "loved", Toast.LENGTH_SHORT).show();
                markNameLoved(untaggedNamesView.getText().toString());
                untaggedNamesView.setText(getNextUntaggedName());
            }
//            public void onSwipeBottom() {
//            }

        });
        setSupportActionBar(toolbar);

//        FloatingActionButton addNameButton = (FloatingActionButton) findViewById(R.id.add_name_button);
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
                    lovedAdapter.notifyDataSetChanged();
                    selectedView = lovedNamesListView;
                } else if (tabName.equals(getString(R.string.unloved_tab))) {
                    Collections.sort(unlovedNames, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                    unlovedAdapter.notifyDataSetChanged();
                    selectedView = unlovedNamesListView;
                } else if (tabName.equals(getString(R.string.triage_tab))) {
                    selectedView = untaggedNamesView;
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

        lovedNamesListView.setOnScrollListener(new AbsListView.OnScrollListener(){
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
        switchToView(untaggedNamesView);
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
                                lovedAdapter.notifyDataSetChanged();
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
        lovedNamesListView.setVisibility(View.GONE);
        unlovedNamesListView.setVisibility(View.GONE);
        untaggedNamesView.setVisibility(View.GONE);
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

