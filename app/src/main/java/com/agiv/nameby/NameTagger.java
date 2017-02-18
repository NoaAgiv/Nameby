package com.agiv.nameby;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Noa Agiv on 12/12/2016.
 */

public class NameTagger {
    public static ArrayList<Name2> untaggedNames;
    public static ArrayList<Name2> partnerlovedNames = new ArrayList<>();
    public static ArrayList<Name2> untaggedPartnerlovedNames = new ArrayList<>();
    public static ArrayList<Name2> lovedNames = new ArrayList<>();
    public static ArrayList<Name2> unlovedNames = new ArrayList<>();
    public static ArrayList<Name2> matchedNames = new ArrayList<>();
    private static Name2 END_OF_LIST = new Name2("Congrats! You tagged all the names!","f", 0);
    private static final Random rgenerator = new Random();
    private static ListView lovedNamesListView;
    private static ListView matchedNamesListView;
    private static ListView unlovedNamesListView;
    private static NameTextView untaggedNamesView;
    private static BaseAdapter lovedAdapter;
    private static BaseAdapter unlovedAdapter;
    private static ImageView loveImage;
    private static ImageView disloveImage;
    private static BaseAdapter matchedAdapter;
    public static TabLayout.Tab matchTab;
    public static Context context;
    public static Activity activity;

    public static MediaPlayer loveSound;
    public static MediaPlayer unlikeSound;
    public static MediaPlayer matchSound;

//    private static DbAccess databaseAccess = DbAccess.getInstance(activity);

//    public void setLists(Name2 name){
//        addNameToListIfNotExists(untaggedNames, name);
//        addNameToListIfNotExists(lovedNames, name);
//            untaggedNames.add(name)
//        untaggedNames.add = databaseAccess.getUntaggedNames(user);
//        lovedNames = databaseAccess.getLovedNames(user);
//        unlovedNames = databaseAccess.getUnlovedNames(user);
//        databaseAccess.close();
//        partnerlovedNames = getPartnerLovedNames();
//        updateUntaggedNames();
//        untaggedPartnerlovedNames = getUntaggedPartnerLovedNames();
//    }

    public void addNameToListIfNotExists(ArrayList<Name2> list, Name2 element){
        if (!list.contains(element))
            list.add(element);
    }
    public static void initData(Context context, Activity activity, TabLayout.Tab matchTab) throws IOException{
        NameTagger.context = context;
        NameTagger.activity = activity;
        NameTagger.matchTab = matchTab;
        getNamesFromDb();
        setListAdapters();
        int unseenMatchesCount = GroupSettings.getCurrentUserUnseenMatches();
        setMatchTabCount(unseenMatchesCount);
        loveSound = MediaPlayer.create(context, R.raw.c_tone);
        unlikeSound = MediaPlayer.create(context, R.raw.a_tone);
        matchSound = MediaPlayer.create(context, R.raw.pin_drop_match);

    }

    public static void setMatchTabCount(int unseenMatchesCount){
        if (unseenMatchesCount > 0) {
            matchTab.setText("(" + unseenMatchesCount + ")");
            matchTab.setIcon(R.drawable.love);
        }
        else {
            matchTab.setText(R.string.name_matches);
            matchTab.setIcon(null);
        }
    }
    public static ArrayList<Name2> getUnlovedNames() {
        return unlovedNames;
    }

    public static ArrayList<Name2> getLovedNames() {
        return lovedNames;
    }

    public static ArrayList<Name2> getUntaggedNames() {
        return untaggedNames;
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

    public static TextView getUntaggedNamesView() {
        return untaggedNamesView.textView;
    }

    public static ImageView getDisloveImage() {
        return disloveImage;
    }

    public static ImageView getLoveImage() {
        return loveImage;
    }

    public static void getNamesFromDb() throws IOException {
        DbAccess databaseAccess = DbAccess.getInstance(activity);
        databaseAccess.initialDbPopulate();
        databaseAccess.open();
        String user = GroupSettings.getCurrentUser();
        untaggedNames = databaseAccess.getUntaggedNames(user);
        lovedNames = databaseAccess.getLovedNames(user);
        unlovedNames = databaseAccess.getUnlovedNames(user);
        databaseAccess.close();
        partnerlovedNames = getPartnerLovedNames();
        updateUntaggedNames();
        untaggedPartnerlovedNames = getUntaggedPartnerLovedNames();
        Collections.sort(matchedNames, new Comparator<Name2>() {
            @Override
            public int compare(Name2 s, Name2 t1) {
                return s.name.compareTo(t1.name);
            }
        });
        Collections.sort(lovedNames, new Comparator<Name2>() {
            @Override
            public int compare(Name2 s, Name2 t1) {
                return s.name.compareTo(t1.name);
            }
        });
        Collections.sort(unlovedNames, new Comparator<Name2>() {
            @Override
            public int compare(Name2 s, Name2 t1) {
                return s.name.compareTo(t1.name);
            }
        });
    }


    public static abstract class SwitchListsCallBack {

        void switchLists(Name2 name){

        }
    }


    private static void swipeLeft(){
        unlikeSound.start();
        markNameUnloved(untaggedNamesView.getCurrentName());
        emphesize_animation(disloveImage);
    }

    private static void swipeRight(){
        boolean isMatch = markNameLoved(untaggedNamesView.getCurrentName());
        if (isMatch)
            matchSound.start();
        else
            loveSound.start();
        emphesize_animation(loveImage);
    }

    private static void emphesize_animation(View view){
        PropertyValuesHolder scalex = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.3f);
        PropertyValuesHolder scaley = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.3f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, scalex, scaley);
        anim.setRepeatCount(1);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setDuration(500);
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                untaggedNamesView.setName(getNextUntaggedName());
            }
        });
    }


    private static void setListAdapters() {
        loveImage = (ImageView) activity.findViewById(R.id.love_image);
        disloveImage = (ImageView) activity.findViewById(R.id.dislove_image);
        lovedNamesListView = (ListView) activity.findViewById(R.id.loved_names);

        final SwitchListsCallBack lovedToUnlovedSwitch = new SwitchListsCallBack() {
            @Override
            public void switchLists(Name2 name) {
                markNameUnloved(name);
            }
        };

        lovedAdapter = new EditableListViewAdapter(lovedToUnlovedSwitch, lovedNames, activity,
                activity.getString(R.string.mark_unloved_dialog_title), activity.getString(R.string.mark_unloved_dialog_body), R.drawable.edit_unlove);

        lovedNamesListView.setAdapter(lovedAdapter);


        unlovedNamesListView = (ListView) activity.findViewById(R.id.unloved_names);

        SwitchListsCallBack unlovedToLovedSwitch = new SwitchListsCallBack() {
            @Override
            public void switchLists(Name2 name) {
                markNameLoved(name);
            }
        };

        unlovedAdapter = new EditableListViewAdapter(unlovedToLovedSwitch, unlovedNames, activity,
                activity.getString(R.string.mark_loved_dialog_title), activity.getString(R.string.mark_loved_dialog_body), R.drawable.edit_love);
        unlovedNamesListView.setAdapter(unlovedAdapter);

        matchedNamesListView = (ListView) activity.findViewById(R.id.matched_names);

        // TODO: change to a regular ListAdapter below
        matchedAdapter = new SimpleListViewAdapter(matchedNames, context);
        matchedNamesListView.setAdapter(matchedAdapter);

        updateMatchedNames();

        untaggedNamesView = new NameTextView((TextView) activity.findViewById(R.id.untagged_names_view));
        untaggedNamesView.setName(getNextUntaggedName());
        untaggedNamesView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeRight() {
                swipeRight();
            }
            public void onSwipeLeft() {
                swipeLeft();
            }

        });

        loveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeRight();
            }
        });

        disloveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipeLeft();
            }
        });
    }




    public static void updateUntaggedNames(){
        Name.setLovedByPartner(untaggedNames, partnerlovedNames);
    }

    public static ArrayList<Name2> getUntaggedPartnerLovedNames(){
        ArrayList<Name2> untaggedPartnerLovedNamed = new ArrayList<Name2>();
        for (Name2 name : untaggedNames){
            if (name.lovedByPartner == true)
                untaggedPartnerLovedNamed.add(name);
        }
        return untaggedPartnerLovedNamed;
    }

    public static void updateMatchedNames(){
        ArrayList<Name2> partnerLovedNames = (ArrayList<Name2>) partnerlovedNames.clone();
        partnerLovedNames.retainAll(lovedNames);
        matchedNames.clear();
        matchedNames.addAll(partnerLovedNames);
        matchedAdapter.notifyDataSetChanged();
    }

    private static ArrayList<Name2> getPartnerLovedNames(){
        DbAccess databaseAccess = DbAccess.getInstance(context);
        databaseAccess.open();
        ArrayList<Name2> partnerLovedNames = databaseAccess.getLovedNames(GroupSettings.getNotCurrentUser());
        databaseAccess.close();
        return partnerLovedNames;
    }

    public static boolean markNameLoved(Name2 name) {
        boolean isMatch = false;
        if (!name.equals(END_OF_LIST)) {
            lovedNames.add(name);
            if (untaggedPartnerlovedNames.remove(name)){
                isMatch = true;
                int unseenMatchesCount = GroupSettings.getCurrentUserUnseenMatches();
                unseenMatchesCount++;
                GroupSettings.setCurrentUserUnseenMatches(unseenMatchesCount);
                GroupSettings.increasePartnerUnseenMatches();
                setMatchTabCount(unseenMatchesCount);
            }
            untaggedNames.remove(name);
            unlovedNames.remove(name);
            DbAccess databaseAccess = DbAccess.getInstance(context);
            databaseAccess.open();
            databaseAccess.markNameLoved(GroupSettings.getCurrentUser(), name);
            databaseAccess.close();
        }
        return isMatch;
    }

    private static void removeFromUntaggedNameList(Name2 name){
        Name2 nameToRemove = null;
        for (Name2 untaggedName : untaggedNames) {
            if (untaggedName.equals(name))
                nameToRemove = untaggedName;

        }
        untaggedNames.remove(nameToRemove);
    }

    public static void markNameUnloved(Name2 name) {
        if (!name.equals(END_OF_LIST)) {
            unlovedNames.add(name);
            removeFromUntaggedNameList(name);
            untaggedPartnerlovedNames.remove(name);
            lovedNames.remove(name);
            DbAccess databaseAccess = DbAccess.getInstance(context);
            databaseAccess.open();
            databaseAccess.markNameUnloved(GroupSettings.getCurrentUser(), name);
            databaseAccess.close();
        }
    }

    private static Name2 getNextUntaggedName() {
        if (untaggedNames.isEmpty())
            return END_OF_LIST;
        else{
            if (getFromPartnerLovedNamesRandonChoice())
                return getRandomFromPartnerLovedNames();
            else
                return getRandomFromUntaggedPopularityBias();
        }
    }

    private static Name2 getRandomFromUntaggedPopularityBias(){
        Collections.sort(untaggedNames, new Comparator<Name2>() {
            @Override
            public int compare(Name2 name1, Name2 name2) {
                return name1.popularity - name2.popularity;
            }
        });
        return untaggedNames.get(rgenerator.nextInt(10));
    }

    private static Name2 getRandomFromPartnerLovedNames(){
        Log.i("size", untaggedPartnerlovedNames.size() + "");
        return untaggedPartnerlovedNames.get(rgenerator.nextInt(untaggedPartnerlovedNames.size()));
    }

    private static boolean getFromPartnerLovedNamesRandonChoice(){
        if (untaggedPartnerlovedNames.isEmpty()) return false;
        int rnd = rgenerator.nextInt(100);
        if (rnd < 20)
            return false;
        else
            return true;
    }


    public static void addName(String name) {
        // replace non-letters and trim edge white spaces
        name = name.replaceAll("-", " ");
        name = name.replaceAll("[^\\p{L}\\s]", "").trim();
        if (name.isEmpty() || lovedNames.contains(name) || unlovedNames.contains(name))
            return;

        markNameLoved(new Name2(name, GroupSettings.getSexString(), 100));
        if (untaggedNamesView.getText().equals(name)) {
            if (untaggedNamesView.getText().equals(name)) {
                untaggedNamesView.setName(getNextUntaggedName());
            }
            getLovedAdapter().notifyDataSetChanged();
            getMatchedAdapter().notifyDataSetChanged();
        }
    }

}
