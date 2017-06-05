package com.agiv.nameby;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.agiv.nameby.Firebase.Firebase;
import com.agiv.nameby.entities.Member;
import static com.agiv.nameby.entities.Member.NameTag.*;
import static com.agiv.nameby.entities.Member.NameTag;
import com.agiv.nameby.entities.Name;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

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
    public static ListView lovedNamesListView;
    public static ListView matchedNamesListView;
    public static ListView unlovedNamesListView;
    public static BaseAdapter lovedAdapter;
    public static BaseAdapter untaggedAdapter;
    public static BaseAdapter unlovedAdapter;
    public static BaseAdapter maybeAdapter;
    public static BaseAdapter matchedAdapter;

    // Other Views
    private static NameTaggerViewContainer untaggedNamesView;

    // Helpers
    private static NameGenerator ngen;


    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void initData(Context context, Activity activity, TabLayout.Tab matchTab){
        Log.w("view", "a");
        NameTagger2.context = context;
        NameTagger2.activity = activity;
//        matchTab = matchTab;
//        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        Firebase.initFamilyListener(Settings.getFamilyId());
//        System.out.println("init family " + Settings.getFamily().familyMembers);
        Firebase.initNameTagListeners();
        initViewAdapters();
        initUntaggedArea();



//        setListAdapters();
        int unseenMatchesCount = Settings.getCurrentUserUnseenMatches();
//        setMatchTabCount(unseenMatchesCount);
//        loveSound = MediaPlayer.create(context, R.raw.c_tone);
//        unlikeSound = MediaPlayer.create(context, R.raw.a_tone);
//        matchSound = MediaPlayer.create(context, R.raw.pin_drop_match);

    }

    public static void initName(Name name){
        allNames.addIf(name, NameList.validNameFilter);
        femaleNames.addIf(name, NameList.femaleFilter);
        maleNames.addIf(name, NameList.maleFilter);
    }

    public static List<String> getNameIds(){
        return allNames.getIds();
    }

    public static void initTag(Member member, String nameId, String tagStr){
        Member.NameTag tag = untagged;
        if (tagStr!=null) {
            tag = Member.NameTag.valueOf(tagStr);
        }
        Name name = allNames.getById(nameId);

        setMemberNameTag(member, name, tag);
        if (member.equals(Settings.getMember())) {
            updateListsWithTags(name, member);
        }
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

    public static void saveNameTag(Name name, Member member){
        DatabaseReference tagsRef = database.getReference("users/" + member.id + "/tags");
        DatabaseReference nameTagRef = tagsRef.child(String.valueOf(name.id));

        nameTagRef.setValue(member.getTag(name));
    }

    public static boolean markNameLoved(Name name){
        Member member = Settings.getMember();
        member.tagName(name, loved);
        saveNameTag(name, member);
        return updateListsWithTags(name, member);
    }

    public static boolean markNameUnloved(Name name){

        Member member = Settings.getMember();
        member.tagName(name, unloved);
        saveNameTag(name, member);
        return updateListsWithTags(name, member);
    }

    public static abstract class SwitchListsCallBack {
        void switchLists(Name name){}
    }



    private static void setMemberNameTag(Member member, Name name, NameTag tag){
        if (tag==null) {
            member.tagName(name, untagged);
        }
        else {
            member.tagName(name, tag);
        }

    }

    private static boolean updateListsWithTags(Name name, Member m){
        // since all names in lists are references,
        // all lists should be updated by updating any Name reference
        NameTag tag = m.getTag(name);
        NameList relevantList = tag.nameList;

        for (NameTag t : NameTag.values()){
            Boolean removed = t.nameList.remove(name);
            if (removed)
                Log.d("NameTagger", String.format("removed %s from %s list", name.name, t));
        }

        relevantList.add(name);
        Log.i("NameTagger", String.format("moved %s to %s list", name.name, tag));
        untaggedNamesView.setName(ngen.getNextUntaggedName());

        // update partner related lists
        if (Settings.getFamily().isUnanimouslyPositive(name)){
            matchedNames.add(name);
            return true;
        }

        if (tag.equals(untagged) && Settings.getFamily().isPositiveForSomeone(name)){
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