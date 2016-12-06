package com.agiv.names2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public ArrayList<String> names= new ArrayList(){{
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

    private String getNextUntaggedName(){
        if (untaggedNames.isEmpty())
            return END_OF_LIST;
        else
            return untaggedNames.get(rgenerator.nextInt(untaggedNames.size()));
    }

    private void markNameLoved(String name){
        if (!name.equals(END_OF_LIST)) {
            lovedNames.add(name);
            untaggedNames.remove(name);
        }
    }

    private void markNameUnloved(String name){
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
        lovedNamesListView.setAdapter(new EditableListViewAdapter(lovedNames, unlovedNames, this,
                getString(R.string.mark_unloved_dialog_title), getString(R.string.mark_unloved_dialog_body)));
        unlovedNamesListView = (ListView) findViewById(R.id.unloved_names);
        unlovedNamesListView.setAdapter(new EditableListViewAdapter(unlovedNames, lovedNames, this,
                getString(R.string.mark_loved_dialog_title), getString(R.string.mark_loved_dialog_body)));
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
        allTabs.addTab(allTabs.newTab().setText(R.string.triage_tab),true);
        allTabs.addTab(allTabs.newTab().setText(R.string.loved_tab));
        allTabs.addTab(allTabs.newTab().setText(R.string.unloved_tab));

        allTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabName = tab.getText().toString();
                View selectedView = null;
                if (tabName.equals(getString(R.string.loved_tab))){
                    Collections.sort(lovedNames, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                    selectedView = lovedNamesListView;
                }
                else if (tabName.equals(getString(R.string.unloved_tab))){
                    Collections.sort(unlovedNames, new Comparator<String>() {
                        @Override
                        public int compare(String s, String t1) {
                            return s.compareTo(t1);
                        }
                    });
                    selectedView = unlovedNamesListView;
                }
                else if (tabName.equals(getString(R.string.triage_tab))){
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
//
//        addNameButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        FloatingActionButton lovedListButton = (FloatingActionButton) findViewById(R.id.loved_list_button);
//        untaggedNamesView.setVisibility(View.GONE);
//
        switchToView(untaggedNamesView);
        lovedListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                untaggedNamesView.setVisibility(View.GONE);
                int x=1;
                lovedNamesListView.setVisibility((View.VISIBLE));
            }
        });
    }


    private void switchToView(View selectedView){
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
}

