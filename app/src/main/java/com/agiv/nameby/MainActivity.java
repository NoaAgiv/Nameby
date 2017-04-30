package com.agiv.nameby;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.StateSet;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;

import com.agiv.nameby.entities.Family;
import com.agiv.nameby.entities.Member;
import com.agiv.nameby.entities.Name;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.agiv.nameby.Settings.changeUser;
import static com.agiv.nameby.Settings.getCurrentUser;
import static com.agiv.nameby.Settings.getGreenUser;
import static com.agiv.nameby.Settings.setIsHelpScreenSeen;
//import static com.agiv.nameby.NameTagger.*;
import static com.agiv.nameby.NameTagger2.*;
public class MainActivity extends AppCompatActivity {

    private GoogleApiClient client;
    private FloatingActionButton addNameButton;
    private SharedPreferences.Editor editor;
    Intent sexChooseIntent;
    Intent familyMembersIntent;
    Intent helpIntent;
    private TextView userName;
    private TabLayout.Tab matchTab;

    private enum ViewName{
        lovedNames,
        unlovedNames,
        untaggedNames,
        matchedNames
    }

    private Map<ViewName, View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sexChooseIntent = new Intent(getBaseContext(), ChooseSexScreen.class);
        familyMembersIntent = new Intent(getBaseContext(), FamilyMembersScreen.class);
        helpIntent = new Intent(getBaseContext(), WelcomeScreen.class);
        Log.d("view", "initiating data");
//        Member noa = new Member("1", "נעה");
//        Settings.setMember(noa);
        Settings.setMemberId("1");
        NameTagger2.initData(MainActivity.this, this, matchTab);

        Log.d("view", "setting UI");
        views = new HashMap<ViewName, View>() {{
            put(ViewName.lovedNames, getLovedNamesListView());
            put(ViewName.unlovedNames, getUnlovedNamesListView());
            put(ViewName.untaggedNames, getUntaggedNamesView());
            put(ViewName.matchedNames, getMatchedNamesListView());
        }};
        setTabs();
        setAddButton();
        getLovedNamesListView().setOnScrollListener(listScrollMoveButtonListener);
        getUnlovedNamesListView().setOnScrollListener(listScrollMoveButtonListener);
        getMatchedNamesListView().setOnScrollListener(listScrollMoveButtonListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        userName = (TextView) findViewById(R.id.user_name);
        userName.setText(getString(R.string.user_name) + " : " + Settings.getCurrentUser());
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchUser();
            }
            });
        toolbar.setLogo(R.drawable.icon);
        setSupportActionBar(toolbar);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        switchToView(ViewName.untaggedNames);
    }

    private void switchUser() {
        changeUser();
        int userImage = getCurrentUser().equals(getGreenUser())? R.drawable.user_green : R.drawable.user_yellow;
        userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, userImage, 0);
        changeUserInit();
    }


    AbsListView.OnScrollListener listScrollMoveButtonListener = new AbsListView.OnScrollListener(){
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
    };

    private void setAddButton(){
        addNameButton = (FloatingActionButton) findViewById(R.id.add_name_button);
        addNameButton.getBackground().setColorFilter(Color.parseColor("#a64dff"), PorterDuff.Mode.MULTIPLY);

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
                                String[] untaggedNames = input.getText().toString().split("\n");
                                for (String name : untaggedNames) {
//                                    addName(name);
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
    }

    private void setTabs(){
        TabLayout allTabs = (TabLayout) findViewById(R.id.tabs);
        allTabs.addTab(allTabs.newTab().setText(R.string.triage_tab), true);
        allTabs.addTab(allTabs.newTab().setText(R.string.loved_tab));
        allTabs.addTab(allTabs.newTab().setText(R.string.unloved_tab));
        allTabs.addTab(allTabs.newTab().setText(R.string.name_matches));
        matchTab = allTabs.getTabAt(3);
        allTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabName = tab.getText().toString();
                ViewName selectedView = null;
                if (tabName.equals(getString(R.string.loved_tab))) {
                    Collections.sort(lovedNames, new Comparator<Name>() {
                        @Override
                        public int compare(Name s, Name t1) {
                            return s.name.compareTo(t1.name);
                        }
                    });
                    getLovedAdapter().notifyDataSetChanged();
                    selectedView = ViewName.lovedNames;
                } else if (tabName.equals(getString(R.string.unloved_tab))) {
                    Collections.sort(unlovedNames, new Comparator<Name>() {
                        @Override
                        public int compare(Name s, Name t1) {
                            return s.name.compareTo(t1.name);
                        }
                    });
                    getUnlovedAdapter().notifyDataSetChanged();
                    selectedView = ViewName.unlovedNames;
                } else if (tabName.equals(getString(R.string.triage_tab))) {
                    selectedView = ViewName.untaggedNames;
                }
                else if (tab.getPosition() == 3 ){ //matches
//                    setMatchTabCount(-1);
                    Settings.setCurrentUserUnseenMatches(0);
//                    updateMatchedNames();
                    Collections.sort(matchedNames, new Comparator<Name>() {
                        @Override
                        public int compare(Name s, Name t1) {
                            return s.name.compareTo(t1.name);
                        }
                    });
                    selectedView = ViewName.matchedNames;
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
    }

    private void switchToView(ViewName viewName) {
        View requestedView = views.get(viewName);
        for (View view : views.values()){
            Log.w("view", "switch");
            if (!view.equals(requestedView))
                view.setVisibility(View.GONE);
            else
                view.setVisibility(View.VISIBLE);
        }
//        getLovedNamesListView().setVisibility(View.GONE);
//        getUnlovedNamesListView().setVisibility(View.GONE);
//        getMatchedNamesListView().setVisibility(View.GONE);
//        getUntaggedNamesView().setVisibility(View.GONE);
//        selectedView.setVisibility(View.VISIBLE);
//        if (selectedView.equals(getUntaggedNamesView())){
//            getLoveImage().setVisibility(View.VISIBLE);
//            getDisloveButton().setVisibility(View.VISIBLE);
//        }
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
            Settings.unsetSex();
            startActivity(sexChooseIntent);
            return true;
        }
        if (id == R.id.family_settings) {
            Settings.setFamilyMembersEdited(false);
            startActivity(familyMembersIntent);
            return true;
        }
        if (id == R.id.change_user){
            switchUser();
        }
        if (id == R.id.help){
            setIsHelpScreenSeen(false);
            startActivity(helpIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeUserInit(){
        try {
            NameTagger2.initData(MainActivity.this, this, matchTab);
        }
        catch (Exception e){
            System.out.println(e);
        }
        userName.setText(getString(R.string.user_name) + " : " + Settings.getCurrentUser());
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

