package com.agiv.nameby;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.agiv.nameby.Firebase.FirebaseDb;
import com.agiv.nameby.entities.Name;
import com.agiv.nameby.fragments.FamilyFragment;
import com.agiv.nameby.fragments.ListsFragment;
import com.agiv.nameby.fragments.RandomTagger;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import static com.agiv.nameby.Settings.changeUser;
import static com.agiv.nameby.Settings.getCurrentUser;
import static com.agiv.nameby.Settings.getGreenUser;
//import static com.agiv.nameby.NameTagger.*;
import static com.agiv.nameby.NameTagger.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleApiClient client;
    private FloatingActionButton addNameButton;
    private SharedPreferences.Editor editor;
    Intent sexChooseIntent;
    Intent familyMembersIntent;
    Intent helpIntent;
    private TextView userName;
    private TabLayout.Tab matchTab;
    public static Context context;
    private static View listsLayout;
    private static View randomTaggerLayout;
    private RandomTagger randomTagger;
    private ListsFragment listFrag;
    private FamilyFragment familyFragment;
    private static int RC_SIGN_IN = 100;

    private enum ViewName{
        lovedNames,
        unlovedNames,
        untaggedNames,
        matchedNames
    }

    private Map<ViewName, View> views;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout mDrawerLinear;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();


//        setContentView(R.layout.activity_main);
//        listsLayout = View.inflate(this, R.layout.content_main, null);
//        randomTaggerLayout = View.inflate(this, R.layout.random_tagger, null);
        listFrag = new ListsFragment();
        randomTagger  = new RandomTagger();
        familyFragment = new FamilyFragment();

        setContentView(R.layout.drawer_layout);
        createDrawer();
        signIn();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, randomTagger).commit();

//        sexChooseIntent = new Intent(getBaseContext(), ChooseSexScreen.class);
//        familyMembersIntent = new Intent(getBaseContext(), FamilyMembersScreen.class);
//        helpIntent = new Intent(getBaseContext(), WelcomeScreen.class);
//        Log.d("view", "initiating data");

//        Settings.setMemberId("0");

//        Log.d("view", "setting UI");
//        views = new HashMap<ViewName, View>() {{
//            put(ViewName.lovedNames, getLovedNamesListView());
//            put(ViewName.unlovedNames, getUnlovedNamesListView());
//            put(ViewName.names, getUntaggedNamesView());
//            put(ViewName.matchedNames, getMatchedNamesListView());
//        }};
//        setTabs();
//        setAddButton();
//        getLovedNamesListView().setOnScrollListener(listScrollMoveButtonListener);
//        getUnlovedNamesListView().setOnScrollListener(listScrollMoveButtonListener);
//        getMatchedNamesListView().setOnScrollListener(listScrollMoveButtonListener);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        userName = (TextView) findViewById(R.id.user_name);
//        userName.setText(getString(R.string.user_name) + " : " + Settings.getCurrentUser());
//        userName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switchUser();
//            }
//            });
//        toolbar.setLogo(R.drawable.icon);
//        setSupportActionBar(toolbar);
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

//        switchToView(ViewName.names);
    }


//    public enum MenuItem {
//        triage(R.string.triage_tab, new RandomTagger()),
//        loved(R.string.loved_tab, new RandomTagger()),
//        unloved(R.string.unloved_tab, new RandomTagger()),
//        matches(R.string.name_matches, new RandomTagger());
//
//        MenuItem(int name, Fragment fragment){
//            this.name = name;
//            this.fragment = fragment;
//        }
//        private Context context = MainActivity.context;
//        private int name;
//        private Fragment fragment;
//
//        @Override
//        public String toString() {
//            return context.getResources().getString(name);
//        }
//    }

    private void createDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

//    private void createDrawer_old(Bundle savedInstanceState){
//        MenuItem[] mPlanetTitles = MenuItem.values();
//        mTitle = mDrawerTitle = getTitle();
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer_menu);
//        mDrawerLinear = (LinearLayout) findViewById(R.id.left_drawer);
//
//        // set a custom shadow that overlays the main content when the drawer opens
////        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
//        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<MenuItem>(this,
//                R.layout.drawer_list_item, mPlanetTitles));
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
//
//        // enable ActionBar app icon to behave as action to toggle nav drawer
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);
//
//        // ActionBarDrawerToggle ties together the the proper interactions
//        // between the sliding drawer and the action bar app icon
//        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
//                this,                  /* host Activity */
//                mDrawerLayout,         /* DrawerLayout object */
//                R.string.drawer_open,  /* "open drawer" description for accessibility */
//                R.string.drawer_close  /* "close drawer" description for accessibility */
//        ) {
//            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(mTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//        };
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }
//    }
//    private class DrawerItemClickListener implements ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            selectItem(position);
//        }
//    }
//
//    private void selectItem(int position) {
////        // update the main content by replacing fragments
////        Fragment fragment = new PlanetFragment();
//////        Bundle args = new Bundle();
//////        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//////        fragment.setArguments(args);
////
////        FragmentManager fragmentManager = getFragmentManager();
////        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
////
////        // update selected item and title, then close the drawer
////        mDrawerList.setItemChecked(position, true);
//////        setTitle(mPlanetTitles[position]);
////        mDrawerLayout.closeDrawer(mDrawerList);
//
//        MenuItem selected = MenuItem.values()[position];
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.content_frame, selected.fragment)
//                .commit();
//        mDrawerList.setItemChecked(position, true);
//        setTitle(selected.toString());
//        mDrawerLayout.closeDrawer(mDrawerLinear);
//
//    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.triage_menu_item) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, randomTagger).commit();

        }else if (id == R.id.loved_menu_item) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
            listFrag.filterByTag(getResources().getString(R.string.loved));

        } else if (id == R.id.matches_menu_item) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
            listFrag.filterByTag(getResources().getString(R.string.matches));

        } else if (id == R.id.all_names_menu_item) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
            listFrag.filterByTag(getResources().getString(R.string.all));

        } else if (id == R.id.family_menu_item) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, familyFragment).commit();
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }








//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }
//

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
        allTabs.addTab(allTabs.newTab().setText(R.string.loved));
        allTabs.addTab(allTabs.newTab().setText(R.string.unloved));
        allTabs.addTab(allTabs.newTab().setText(R.string.matches));
        matchTab = allTabs.getTabAt(3);
        allTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabName = tab.getText().toString();
                ViewName selectedView = null;
                if (tabName.equals(getString(R.string.loved))) {
                    Collections.sort(lovedNames, new Comparator<Name>() {
                        @Override
                        public int compare(Name s, Name t1) {
                            return s.name.compareTo(t1.name);
                        }
                    });
                    getLovedAdapter().notifyDataSetChanged();
                    selectedView = ViewName.lovedNames;
                } else if (tabName.equals(getString(R.string.unloved))) {
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(android.view.MenuItem item) {
////        // Handle action bar item clicks here. The action bar will
////        // automatically handle clicks on the Home/Up button, so long
////        // as you specify a parent activity in AndroidManifest.xml.
////        int id = item.getItemId();
////
////        // Pass the event to ActionBarDrawerToggle, if it returns
////        // true, then it has handled the app icon touch event
////        if (mDrawerToggle.onOptionsItemSelected(item)) {
////            return true;
////            return true;
////        }
////        // Handle your other action bar items...
////
////        return super.onOptionsItemSelected(item);
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }


        //noinspection SimplifiableIfStatement
//        if (id == R.id.sex_settings) {
//            Settings.unsetGender();
//            startActivity(sexChooseIntent);
//            return true;
//        }
//        if (id == R.id.family_settings) {
//            Settings.setFamilyMembersEdited(false);
//            startActivity(familyMembersIntent);
//            return true;
//        }
//        if (id == R.id.change_user){
//            switchUser();
//        }
//        if (id == R.id.help){
//            setIsHelpScreenSeen(false);
//            startActivity(helpIntent);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void changeUserInit(){
        try {
            NameTagger.initData(MainActivity.this, this, matchTab, listFrag, randomTaggerLayout, randomTagger, familyFragment);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        NameTagger.initData(MainActivity.this, this, matchTab, listFrag, randomTaggerLayout, randomTagger, familyFragment);
    }

    private void signIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleApiClient.OnConnectionFailedListener  a = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        };
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, a)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void setSwitchUserButton(){
        ImageButton switchAccount = (ImageButton) findViewById(R.id.switchAccount);
        switchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                signOut();
//                mGoogleApiClient.clearDefaultAccountAndReconnect();

                signIn();
            }
        });



    }

    private void signOut(){
        mGoogleApiClient.clearDefaultAccountAndReconnect();
//        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
        mGoogleApiClient.disconnect();
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Authentication", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            TextView nameView = (TextView) findViewById(R.id.googleAccountDisplayName);
            TextView emailView = (TextView) findViewById(R.id.googleAccountEmail);
            ImageView imageView = (ImageView) findViewById(R.id.googleAccountImage);
            nameView.setText(acct.getDisplayName());
            emailView.setText(acct.getEmail());
            Picasso.with(this).load(acct.getPhotoUrl()).into(imageView);
            imageView.setMaxHeight(1);
            FirebaseDb.setMemberAndFamily(emailView.getText().toString());
            setSwitchUserButton();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

}

