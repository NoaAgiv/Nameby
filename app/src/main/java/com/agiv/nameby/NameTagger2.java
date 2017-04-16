package com.agiv.nameby;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.agiv.nameby.Firebase.Firebase;
import com.agiv.nameby.entities.Name;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.agiv.nameby.entities.Name.NameTag;
import static com.agiv.nameby.entities.Name.NameTag.*;

/**
 * Created by Noa Agiv on 3/11/2017.
 */

public class NameTagger2 {
    // Context
//    public static TabLayout.Tab matchTab;
    public static Context context;
    public static Activity activity;

    // Lists
    public static NameList femaleNames = new NameList();
    public static NameList maleNames = new NameList();
    public static NameList allNames = new NameList();
    public static NameList untaggedNames = new NameList();
    public static NameList lovedNames = new NameList();
    public static NameList unlovedNames = new NameList();
    public static NameList maybeNames = new NameList();
    public static NameList matchedNames = new NameList();
    public static NameList untaggedPartnerPositiveNames = new NameList();

    // List Views
    private static ListView lovedNamesListView;
    private static ListView matchedNamesListView;
    private static ListView unlovedNamesListView;
    private static BaseAdapter lovedAdapter;
    private static BaseAdapter untaggedAdapter;
    private static BaseAdapter unlovedAdapter;
    private static BaseAdapter maybeAdapter;
    private static BaseAdapter matchedAdapter;

    // Other Views
    private static NameTaggerViewContainer untaggedNamesView;

    // Helpers
    private static NameGenerator ngen;

    // Mappers
    public static Map<Name.NameTag, NameList> tagListMap = new HashMap<Name.NameTag, NameList>(){{
        put(Name.NameTag.loved, lovedNames);
        put(Name.NameTag.untagged, untaggedNames);
        put(Name.NameTag.unloved, unlovedNames);
        put(Name.NameTag.maybe, maybeNames);
    }};
    public static Map<NameList, ListAdapter> listToAdapterMap = new HashMap<NameList, ListAdapter>(){{
        put(lovedNames, lovedAdapter);
        put(untaggedNames, untaggedAdapter);
        put(unlovedNames, unlovedAdapter);
        put(maybeNames, maybeAdapter);
        put(matchedNames, maybeAdapter);
    }};

    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void initData(Context context, Activity activity, TabLayout.Tab matchTab){
        Log.w("view", "a");
        NameTagger2.context = context;
        NameTagger2.activity = activity;
//        matchTab = matchTab;
//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        Firebase.initFamilyListener(Settings.getFamilyId());
        initLists2();
        initViewAdapters();
        initUntaggedArea();


//        setListAdapters();
        int unseenMatchesCount = Settings.getCurrentUserUnseenMatches();
//        setMatchTabCount(unseenMatchesCount);
//        loveSound = MediaPlayer.create(context, R.raw.c_tone);
//        unlikeSound = MediaPlayer.create(context, R.raw.a_tone);
//        matchSound = MediaPlayer.create(context, R.raw.pin_drop_match);

    }

    private static void initUntaggedArea(){
        ngen = new NameGenerator(untaggedNames, untaggedPartnerPositiveNames, new NamePreferences());
        untaggedNamesView = new NameTaggerViewContainer(context, (TextView) activity.findViewById(R.id.untagged_names_view), activity);
        untaggedNamesView.setName(ngen.getNextUntaggedName());


    }

    private static void initViewAdapters() {

        lovedNamesListView = (ListView) activity.findViewById(R.id.loved_names);

        final SwitchListsCallBack lovedToUnlovedSwitch = new SwitchListsCallBack() {
            @Override
            public void switchLists(Name name) {
                markNameUnloved(name);
            }
        };

        lovedAdapter = new EditableListViewAdapter(lovedToUnlovedSwitch, lovedNames, activity,
                activity.getString(R.string.mark_unloved_dialog_title), activity.getString(R.string.mark_unloved_dialog_body), R.drawable.edit_unlove);

        lovedNamesListView.setAdapter(lovedAdapter);


        unlovedNamesListView = (ListView) activity.findViewById(R.id.unloved_names);

        SwitchListsCallBack unlovedToLovedSwitch = new SwitchListsCallBack() {
            @Override
            public void switchLists(Name name) {
                markNameLoved(name);
            }
        };

        unlovedAdapter = new EditableListViewAdapter(unlovedToLovedSwitch, unlovedNames, activity,
                activity.getString(R.string.mark_loved_dialog_title), activity.getString(R.string.mark_loved_dialog_body), R.drawable.edit_love);
        unlovedNamesListView.setAdapter(unlovedAdapter);

        matchedNamesListView = (ListView) activity.findViewById(R.id.matched_names);

        matchedAdapter = new SimpleListViewAdapter(matchedNames, context);
        matchedNamesListView.setAdapter(matchedAdapter);

//        updateMatchedNames();



    }

    public static void saveNameTag(Name name){
        String user = Settings.getCurrentUser();
        DatabaseReference tagsRef = database.getReference("users/" + user + "/tags");
        DatabaseReference nameTagRef = tagsRef.child(String.valueOf(name.id));
        Log.w("save", name.getTag().toString());
        Log.w("save", String.valueOf(name.id));
        Log.w("save", name.name);

        nameTagRef.setValue(name.getTag());
    }

    public static boolean markNameLoved(Name name){
        name.tagName(Name.NameTag.loved);
        saveNameTag(name);
        return updateListsWithName(name);
    }

    public static boolean markNameUnloved(Name name){
        name.tagName(NameTag.unloved);
        saveNameTag(name);
        return updateListsWithName(name);
    }

    public static abstract class SwitchListsCallBack {
        void switchLists(Name name){}
    }

    private static void initLists2(){
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference namesRef = database.getReference("names");

        final ValueEventListener TagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUserNameTag(Settings.getCurrentUser(), dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        final ChildEventListener namesListener = new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                Name name = dataSnapshot.getValue(Name.class);
                name.setId(id);
                allNames.addIf(name, NameList.validNameFilter);
                femaleNames.addIf(name, NameList.femaleFilter);
                maleNames.addIf(name, NameList.maleFilter);
                DatabaseReference userTagsRef = database.getReference("users/"+ Settings.getCurrentUser() + "/tags");
                userTagsRef.child(id).addListenerForSingleValueEvent(TagsListener);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        namesRef.addChildEventListener(namesListener);


    }

    private static void setUserNameTag(String user, DataSnapshot usereTagData){
        // since all names in lists are references,
        // all lists should be updated by updating any Name reference
        String id = usereTagData.getKey();
        Name name = allNames.getById(id);
        if (usereTagData.getValue()==null) {
            allNames.getById(id).tagName(user, Name.NameTag.untagged);
        }
        else {
            allNames.getById(id).tagName(user, (String) usereTagData.getValue());
        }
        updateListsWithName(name);
        Log.w("tag", id);
//        Log.w("tag", lovedNames.get(id).userTags.get(Settings.getCurrentUser()).toString());
    }

    private static boolean updateListsWithName(Name name){
        NameTag tag = name.getTag();
        NameList relevantList = tagListMap.get(tag);
        Log.w("update", tag.toString());
        Log.w("update", relevantList.toString());
        Log.w("update", Boolean.toString(tagListMap.get(NameTag.untagged).isEmpty()));
        for (Map.Entry<NameTag, NameList> list : tagListMap.entrySet()){
            Boolean removed = list.getValue().remove(name);
            if (removed)
                Log.d("NameTagger", String.format("removed %s from %s list", name.name, list.getKey()));
        }

        relevantList.add(name);
        Log.i("NameTagger", String.format("moved %s to %s list", name.name, tag));
        untaggedNamesView.setName(ngen.getNextUntaggedName());

        // update partner related lists
        if (name.isUnanimouslyPositive()){
            matchedNames.add(name);
            return true;
        }

        if (tag.equals(untagged) && name.isPositiveForAnyPartner()){
            untaggedPartnerPositiveNames.add(name);
        }
        return false;

    }

    public static ListView getLovedNamesListView() {
        return lovedNamesListView;
    }

    public static ListView getMatchedNamesListView() {
        return matchedNamesListView;
    }

    public static ListView getUnlovedNamesListView() {
        return unlovedNamesListView;
    }
    public static BaseAdapter getLovedAdapter() {
        return lovedAdapter;
    }

    public static BaseAdapter getUnlovedAdapter() {
        return unlovedAdapter;
    }
    public static BaseAdapter getMatchedAdapter() {
        return matchedAdapter;
    }

    public static NameTaggerViewContainer getUntaggedNamesView() {
        return untaggedNamesView;
    }

}
