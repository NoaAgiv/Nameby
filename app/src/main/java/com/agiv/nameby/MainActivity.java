package com.agiv.nameby;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
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

import static com.agiv.nameby.Firebase.NotificationService.MATCH_NOTIFICATION;
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
    FragmentManager fragmentManager = getSupportFragmentManager();

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


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            System.out.println("any recieve at all?");
            String message = intent.getStringExtra("key");
            final BottomNavigationItemView menuMatchesItem = (BottomNavigationItemView) findViewById(R.id.menu_matches);

            switch (intent.getAction()) {
                case MATCH_NOTIFICATION:
                    Log.d("Receiver", "main thread: got match notification");
                    menuMatchesItem.setSelected(true); // set to selected state to change icon
                    break;

            }
        }
    };
    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }




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
        createQuickMenu();
        signIn();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, randomTagger).commit();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


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

    private void createQuickMenu() {
        final BottomNavigationView bottomMenu = (BottomNavigationView) findViewById(R.id.quick_menu);
        bottomMenu.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        System.out.println("on nav item selected");

                        switch (item.getItemId()) {
                            case R.id.menu_matches:
                                fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
                                listFrag.filterByTag(getResources().getString(R.string.matches));
                                break;
                            case R.id.menu_triage:
                                fragmentManager.beginTransaction().replace(R.id.content_frame, randomTagger).commit();
                                break;
                            case R.id.menu_lists:
                                fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
                                listFrag.filterByTag(getResources().getString(R.string.all));
                                break;
                        }
                        return true;
                    }
                });
    }


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
        getMenuInflater().inflate(R.menu.quick_menu, menu);
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
        System.out.println("hi an option was chosen " + item.getItemId());
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

    private void switchToView(ViewName viewName) {
        View requestedView = views.get(viewName);
        for (View view : views.values()){
            Log.w("view", "switch");
            if (!view.equals(requestedView))
                view.setVisibility(View.GONE);
            else
                view.setVisibility(View.VISIBLE);
        }

    }

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

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MATCH_NOTIFICATION));
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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

