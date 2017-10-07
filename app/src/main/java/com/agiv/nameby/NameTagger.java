package com.agiv.nameby;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.agiv.nameby.Firebase.FirebaseDb;
import com.agiv.nameby.entities.Member;
import static com.agiv.nameby.entities.Member.NameTag.*;
import static com.agiv.nameby.entities.Member.NameTag;
import com.agiv.nameby.entities.Name;
import com.agiv.nameby.fragments.FamilyFragment;
import com.agiv.nameby.fragments.ListsFragment;
import com.agiv.nameby.fragments.NameAdditionFragment;
import com.agiv.nameby.fragments.RandomTagger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by Noa Agiv on 3/11/2017.
 */

public class NameTagger {
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
    private static RandomTagger randomTagger;
    private static NameList nameList = new NameList();
    private static ListsFragment listFrag;

    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void initData(Context context, Activity activity, ListsFragment listsFragment, View randomTaggerLayout, RandomTagger randomTagger, FamilyFragment familyFragment, NameAdditionFragment nameAdditionFragment){
        NameTagger.randomTagger = randomTagger;
        NameTagger.listFrag = listsFragment;
        listsFragment.setNames(nameList, context);
        nameAdditionFragment.setNames(allNames);
        NameTagger.context = context;
        NameTagger.activity = activity;
        FirebaseDb.initFamilyListener(Settings.getFamilyId());
        FirebaseDb.initNameTagListeners();
        initUntaggedArea();
    }

    public static void updateListsWithName(Name name){
        allNames.addIf(name, NameList.validNameFilter);

//        nameList.addIf(name, NameList.validNameFilter);
        maleNames.addIf(name, NameList.maleFilter);
        femaleNames.addIf(name, NameList.femaleFilter);
        updateListsByGender();

        listFrag.notifyChange();
    }

    public static void updateListsByGender(){
        nameList = Settings.getGender().equals(Settings.Gender.FEMALE)?
                femaleNames :
                maleNames;
        if (listFrag!=null)
            listFrag.setNames(nameList, context);
        if (ngen!=null)
            initUntaggedArea();
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
        if (tag.equals(NameTag.untagged) && ngen.allNamesTagged()) // update triage view with the single new untagged name
            randomTagger.setName(ngen.getNextUntaggedName());
    }

    private static void initUntaggedArea(){
        ngen = new NameGenerator(nameList, new NamePreferences());
        randomTagger.setName(ngen.getNextUntaggedName());
    }


    public static void saveNameTag(Name name, Member member){
        DatabaseReference tagsRef = database.getReference("users/" + member.id + "/tags");
        DatabaseReference nameTagRef = tagsRef.child(String.valueOf(name.id));

        nameTagRef.setValue(member.getTag(name));
    }

    public static boolean markNameLoved(Name name){
        return markNameTag(name, loved);
    }

    public static boolean markNameUnloved(Name name){
       return markNameTag(name, unloved);
    }

    public static boolean markNameMaybe(Name name){
        return markNameTag(name, maybe);
    }

    public static boolean markNameTag(Name name, NameTag tag){

        Member member = Settings.getMember();
        member.tagName(name, tag);
        saveNameTag(name, member);
        return updateListsWithTags(name, member);
    }

    public static abstract class SwitchListsCallBack {
        void switchLists(Name name){}
    }



    private static void setMemberNameTag(Member member, Name name, NameTag tag){
        System.identityHashCode(member);

        System.identityHashCode(Settings.getMember());
        if (tag==null) {
            member.tagName(name, untagged);
        }
        else {
            member.tagName(name, tag);
        }

    }

    private static boolean updateListsWithTags(Name name, Member m) {
        NameTag tag = m.getTag(name);
        updateListsWithName(name);
        randomTagger.setName(ngen.getNextUntaggedName());
        return Settings.getFamily().isUnanimouslyPositive(name);
    }

    private static boolean updateListsWithTags2(Name name, Member m){
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
