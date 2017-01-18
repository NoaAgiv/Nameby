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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * Created by Noa Agiv on 12/12/2016.
 */

public class NameTagger {
    public static ArrayList<Name> untaggedNames;
    public static ArrayList<String> partnerlovedNames = new ArrayList<>();
    public static ArrayList<String> untaggedPartnerlovedNames = new ArrayList<>();
    public static ArrayList<String> lovedNames = new ArrayList<>();
    public static ArrayList<String> unlovedNames = new ArrayList<>();
    public static ArrayList<String> matchedNames = new ArrayList<>();
    private static String END_OF_LIST = "Congrats! You tagged all the names!";
    private static final Random rgenerator = new Random();
    private static ListView lovedNamesListView;
    private static ListView matchedNamesListView;
    private static ListView unlovedNamesListView;
    private static TextView untaggedNamesView;
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
    public static ArrayList<String> getUnlovedNames() {
        return unlovedNames;
    }

    public static ArrayList<String> getLovedNames() {
        return lovedNames;
    }

    public static ArrayList<Name> getUntaggedNames() {
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
        return untaggedNamesView;
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
        Collections.sort(matchedNames, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        Collections.sort(lovedNames, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        Collections.sort(unlovedNames, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
    }


    public static abstract class SwitchListsCallBack {

        void switchLists(String name){

        }
    }


    private static void swipeLeft(){
        unlikeSound.start();
        markNameUnloved(untaggedNamesView.getText().toString());
        emphesize_animation(disloveImage);
    }

    private static void swipeRight(){
        boolean isMatch = markNameLoved(untaggedNamesView.getText().toString());
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
                untaggedNamesView.setText(getNextUntaggedName());
            }
        });
    }


    private static void setListAdapters() {
        loveImage = (ImageView) activity.findViewById(R.id.love_image);
        disloveImage = (ImageView) activity.findViewById(R.id.dislove_image);
        lovedNamesListView = (ListView) activity.findViewById(R.id.loved_names);

        final SwitchListsCallBack lovedToUnlovedSwitch = new SwitchListsCallBack() {
            @Override
            public void switchLists(String name) {
                markNameUnloved(name);
            }
        };

        lovedAdapter = new EditableListViewAdapter(lovedToUnlovedSwitch, lovedNames, activity,
                activity.getString(R.string.mark_unloved_dialog_title), activity.getString(R.string.mark_unloved_dialog_body), R.drawable.edit_unlove);

        lovedNamesListView.setAdapter(lovedAdapter);


        unlovedNamesListView = (ListView) activity.findViewById(R.id.unloved_names);

        SwitchListsCallBack unlovedToLovedSwitch = new SwitchListsCallBack() {
            @Override
            public void switchLists(String name) {
                markNameLoved(name);
            }
        };

        unlovedAdapter = new EditableListViewAdapter(unlovedToLovedSwitch, unlovedNames, activity,
                activity.getString(R.string.mark_loved_dialog_title), activity.getString(R.string.mark_loved_dialog_body), R.drawable.edit_love);
        unlovedNamesListView.setAdapter(unlovedAdapter);

        matchedNamesListView = (ListView) activity.findViewById(R.id.matched_names);
        matchedNamesListView.setEnabled(false);

        // TODO: change to a regular ListAdapter below
        matchedAdapter = new SimpleListViewAdapter(matchedNames, context);
        matchedNamesListView.setAdapter(matchedAdapter);

        updateMatchedNames();

        untaggedNamesView = (TextView) activity.findViewById(R.id.untagged_names_view);
        untaggedNamesView.setText(getNextUntaggedName());
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

    public static ArrayList<String> getUntaggedPartnerLovedNames(){
        ArrayList<String> untaggedPartnerLovedNamed = new ArrayList<String>();
        for (Name name : untaggedNames){
            if (name.lovedByPartner == true)
                untaggedPartnerLovedNamed.add(name.name);
        }
        return untaggedPartnerLovedNamed;
    }

    public static void updateMatchedNames(){
        ArrayList<String> partnerLovedNames = (ArrayList<String>) partnerlovedNames.clone();
        partnerLovedNames.retainAll(lovedNames);
        matchedNames.clear();
        matchedNames.addAll(partnerLovedNames);
        matchedAdapter.notifyDataSetChanged();
    }

    private static ArrayList<String> getPartnerLovedNames(){
        DbAccess databaseAccess = DbAccess.getInstance(context);
        databaseAccess.open();
        ArrayList<String> partnerLovedNames = databaseAccess.getLovedNames(GroupSettings.getNotCurrentUser());
        databaseAccess.close();
        return partnerLovedNames;
    }

    public static boolean markNameLoved(String name) {
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
            removeFromUntaggedNameList(name);
            unlovedNames.remove(name);
            DbAccess databaseAccess = DbAccess.getInstance(context);
            databaseAccess.open();
            databaseAccess.markNameLoved(GroupSettings.getCurrentUser(), name);
            databaseAccess.close();
        }
        return isMatch;
    }

    private static void removeFromUntaggedNameList(String name){
        Name nameToRemove = null;
        for (Name untaggedName : untaggedNames) {
            if (untaggedName.name.equals(name))
                nameToRemove = untaggedName;

        }
        untaggedNames.remove(nameToRemove);
    }

    public static void markNameUnloved(String name) {
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

    private static String getNextUntaggedName() {
        if (untaggedNames.isEmpty())
            return END_OF_LIST;
        else{
            if (getFromPartnerLovedNamesRandonChoice())
                return getRandomFromPartnerLovedNames();
            else
                return getRandomFromUntaggedPopularityBias();
        }
    }

    private static String getRandomFromUntaggedPopularityBias(){
        Collections.sort(untaggedNames, new Comparator<Name>() {
            @Override
            public int compare(Name name1, Name name2) {
                return name1.popularity - name2.popularity;
            }
        });
        return untaggedNames.get(rgenerator.nextInt(10)).name;
    }

    private static String getRandomFromPartnerLovedNames(){
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

        markNameLoved(name);
        if (untaggedNamesView.getText().equals(name)) {
            untaggedNamesView.setText(getNextUntaggedName());
        }
        getLovedAdapter().notifyDataSetChanged();
        getMatchedAdapter().notifyDataSetChanged();
    }

}
