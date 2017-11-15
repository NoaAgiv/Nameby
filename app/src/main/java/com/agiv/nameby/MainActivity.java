package com.agiv.nameby;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.agiv.nameby.Firebase.FirebaseDb;
import com.agiv.nameby.entities.Family;
import com.agiv.nameby.entities.Member;
import com.agiv.nameby.fragments.FamilyFragment;
import com.agiv.nameby.fragments.ListsFragment;
import com.agiv.nameby.fragments.NameAdditionFragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.Map;

import static com.agiv.nameby.Firebase.NotificationService.MATCH_NOTIFICATION;
import static com.agiv.nameby.Firebase.NotificationService.MATCH_WATCHED;
import static com.agiv.nameby.NameTagger.FAMILY_MEMBER_TAG;
import static com.agiv.nameby.Settings.changeUser;
import static com.agiv.nameby.Settings.getCurrentUser;
import static com.agiv.nameby.Settings.getGreenUser;
//import static com.agiv.nameby.NameTagger.*;
import static com.agiv.nameby.Settings.member;

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
    private NameAdditionFragment nameAdditionFragment;
    private BottomNavigationView quickMenu;
    private BottomNavigationItemView quickMenuTriage;
    private BottomNavigationItemView quickMenuLists;

    private static int RC_SIGN_IN = 100;
    FragmentManager fragmentManager = getSupportFragmentManager();
    int currentScreen = -1;

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
            String message = intent.getStringExtra("key");
            final BottomNavigationItemView menuMatchesItem = (BottomNavigationItemView) findViewById(R.id.menu_matches);

            switch (intent.getAction()) {
                case MATCH_NOTIFICATION:
                    Log.d("Receiver", "main thread: got match notification");
                    menuMatchesItem.setSelected(true); // set to selected state to change icon
                    break;
                case MATCH_WATCHED:
                    Log.d("Receiver", "main thread: got match watched notification");
                    menuMatchesItem.setSelected(false); // set to not selected state to change icon
                    break;
                case FAMILY_MEMBER_TAG:
                    Log.d("Receiver", "main thread: got family member tag intent");
                    // update matches filtering if it is currently the selected filter
                    if (currentScreen == R.id.menu_matches || currentScreen == R.id.matches_menu_item) {
                        listFrag.filterByTag(getResources().getString(R.string.all_names)); // for calling spinner's onItemSelection. Spinner does not tirgger it if it is the same selection
                        listFrag.filterByTag(getResources().getString(R.string.matches));
                        listFrag.notifyChange();
                    }
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
        nameAdditionFragment = new NameAdditionFragment();

        setContentView(R.layout.drawer_layout);
        createDrawer();
        createQuickMenu();
        signIn();

        quickMenu = (BottomNavigationView) findViewById(R.id.quick_menu);
        quickMenuTriage = (BottomNavigationItemView) quickMenu.findViewById(R.id.menu_triage);
        quickMenuLists = (BottomNavigationItemView) quickMenu.findViewById(R.id.menu_lists);
        quickMenuTriage.performClick();

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, randomTagger).commit();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void deselectAllQuickMenuItems(){
        int size = quickMenu.getMenu().size();
        for (int i = 0; i < size; i++) {
            quickMenu.getMenu().getItem(i).setChecked(false);
        }
    }

    private void createDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.nameby_logo);
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

//        final BottomNavigationItemView triageMenuItem = (BottomNavigationItemView) bottomMenu.findViewById(R.id.menu_triage);
        bottomMenu.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        currentScreen = item.getItemId();
                        switch (item.getItemId()) {
                            case R.id.menu_matches:
                                listFrag.filterByTag(getResources().getString(R.string.matches));
                                fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
                                break;
                            case R.id.menu_triage:
                                fragmentManager.beginTransaction().replace(R.id.content_frame, randomTagger).commit();
                                break;
                            case R.id.menu_lists:
                                listFrag.filterByTag(getResources().getString(R.string.all));
                                fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();

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
        return false;
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
        currentScreen = id;

        if (id == R.id.triage_menu_item) {
            quickMenuTriage.performClick();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, randomTagger).commit();

        }else if (id == R.id.loved_menu_item) {
            quickMenuLists.performClick();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
            listFrag.filterByTag(getResources().getString(R.string.loved));

        } else if (id == R.id.matches_menu_item) {
            quickMenuLists.performClick();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
            listFrag.filterByTag(getResources().getString(R.string.matches));

        } else if (id == R.id.all_names_menu_item) {
            quickMenuLists.performClick();
//            fragmentManager.beginTransaction().replace(R.id.content_frame, listFrag).commit();
            listFrag.filterByTag(getResources().getString(R.string.all));

        } else if (id == R.id.add_names_menu_item) {
            deselectAllQuickMenuItems();
            fragmentManager.beginTransaction().replace(R.id.content_frame, nameAdditionFragment).commit();

        } else if (id == R.id.family_menu_item) {
            deselectAllQuickMenuItems();
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


    private void changeUserInit(){
        try {
            NameTagger.initData(MainActivity.this, this, listFrag, randomTaggerLayout, randomTagger, familyFragment, nameAdditionFragment);
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

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(MATCH_WATCHED));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(FAMILY_MEMBER_TAG));

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
        if (Settings.getMember()!=null)
            NameTagger.initData(MainActivity.this, this, listFrag, randomTaggerLayout, randomTagger, familyFragment, nameAdditionFragment);
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
            final GoogleSignInAccount acct = result.getSignInAccount();
            TextView nameView = (TextView) findViewById(R.id.googleAccountDisplayName);
            TextView emailView = (TextView) findViewById(R.id.googleAccountEmail);
            ImageView imageView = (ImageView) findViewById(R.id.googleAccountImage);
            nameView.setText(acct.getDisplayName());
            emailView.setText(acct.getEmail());
            Picasso.with(this).load(acct.getPhotoUrl()).into(imageView);
            imageView.setMaxHeight(1);
            Task<FirebaseDb.MemberInitiationState> tryGetMember = FirebaseDb.setMemberAndFamily(emailView.getText().toString());

            tryGetMember.addOnCompleteListener(new OnCompleteListener<FirebaseDb.MemberInitiationState>() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.getResult() == FirebaseDb.MemberInitiationState.SubscribedToFamily)
                        return;

                    Family family = Family.addSaveFamily(getResources().getString(R.string.default_family_name));
                    Settings.setFamily(family);

                    if (task.getResult() == FirebaseDb.MemberInitiationState.Unknown) {
                        Log.i("sign in handler", "unknown member, creating a new one");
                        Member member = new Member(getResources().getString(R.string.default_member_name), acct.getEmail());
                        member.save();
                        Settings.setMember(member);

                        deselectAllQuickMenuItems();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, familyFragment).commit();
                    }
                    Settings.getMember().setFamily(family.id);
                    member.save();
                    family.addSaveMember(member);

                    familyFragment.resetFamily();

                    deselectAllQuickMenuItems();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, familyFragment).commit();

                }
            });


//            setSwitchUserButton();
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

}

