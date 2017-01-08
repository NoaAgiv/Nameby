package com.agiv.names2;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Noa Agiv on 12/12/2016.
 */

public class NameTagger {
    public static ArrayList<String> untaggedNames;
    public static ArrayList<String> lovedNames = new ArrayList<>();
    public static ArrayList<String> unlovedNames = new ArrayList<>();
    private static String END_OF_LIST = "Congrats! You tagged all the names!";
    private static final Random rgenerator = new Random();
    private static ListView lovedNamesListView;
    private static ListView unlovedNamesListView;
    private static TextView untaggedNamesView;
    private static BaseAdapter lovedAdapter;
    private static BaseAdapter unlovedAdapter;
    private static ImageView loveImage;
    private static ImageView disloveImage;
    public static int userId;
    public static Context context;
    public static Activity activity;

//    private static DbAccess databaseAccess = DbAccess.getInstance(activity);

    public static void initData(Context context, Activity activity, int userId) throws IOException{
        NameTagger.context = context;
        NameTagger.activity = activity;
        NameTagger.userId = userId;
        getNamesFromDb();
        setListAdapters();
    }

    public static ArrayList<String> getUnlovedNames() {
        return unlovedNames;
    }

    public static ArrayList<String> getLovedNames() {
        return lovedNames;
    }

    public static ArrayList<String> getUntaggedNames() {
        return untaggedNames;
    }

    public static ListView getLovedNamesListView() {
        return lovedNamesListView;
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
    }


    public static abstract class SwitchListsCallBack {

        void switchLists(String name){

        }
    }
    private static void swipeLeft(){
        final ImageView imageCopy = new ImageView(context);
        final ImageView imageCopy2 = new ImageView(context);
        final ImageView imageCopy3 = new ImageView(context);

        imageCopy.setImageResource(R.drawable.dislove);
        imageCopy.setX(disloveImage.getX());
        imageCopy.setY(disloveImage.getY());
        imageCopy2.setImageResource(R.drawable.dislove);
        imageCopy2.setX(disloveImage.getX());
        imageCopy2.setY(disloveImage.getY());
        imageCopy3.setImageResource(R.drawable.dislove);
        imageCopy3.setX(disloveImage.getX());
        imageCopy3.setY(disloveImage.getY());

        final View mainView = activity.findViewById(R.id.content_main);
        ((ViewGroup) mainView).addView(imageCopy);
        ((ViewGroup) mainView).addView(imageCopy2);
        ((ViewGroup) mainView).addView(imageCopy3);

        markNameUnloved(untaggedNamesView.getText().toString());
        final ViewPropertyAnimator anim = imageCopy.animate();

        anim.setListener(new android.animation.Animator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation){}

            @Override
            public void onAnimationCancel(Animator animation){}

            @Override
            public void onAnimationRepeat(Animator animation){}

            @Override
            public void onAnimationEnd(Animator animation){
                untaggedNamesView.setText(getNextUntaggedName());
                ((ViewGroup) mainView).removeView(imageCopy);
                ((ViewGroup) mainView).removeView(imageCopy2);
                ((ViewGroup) mainView).removeView(imageCopy3);
            }
        });
        anim.translationXBy(700).setDuration(1000);
        anim.translationYBy(-500).setDuration(500);
        imageCopy2.animate().translationXBy(600).setDuration(1000);
        imageCopy2.animate().translationYBy(200).setDuration(1000);
        imageCopy3.animate().translationXBy(1500).setDuration(1000);
    }
    private static void swipeRight(){
        final ImageView loveImageCopy = new ImageView(context);
        final ImageView loveImageCopy2 = new ImageView(context);
        final ImageView loveImageCopy3 = new ImageView(context);

        loveImageCopy.setImageResource(R.drawable.love);
        loveImageCopy.setX(loveImage.getX());
        loveImageCopy.setY(loveImage.getY());
        loveImageCopy2.setImageResource(R.drawable.love);
        loveImageCopy2.setX(loveImage.getX());
        loveImageCopy2.setY(loveImage.getY());
        loveImageCopy3.setImageResource(R.drawable.love);
        loveImageCopy3.setX(loveImage.getX());
        loveImageCopy3.setY(loveImage.getY());

        final View mainView = activity.findViewById(R.id.content_main);
        ((ViewGroup) mainView).addView(loveImageCopy);
        ((ViewGroup) mainView).addView(loveImageCopy2);
        ((ViewGroup) mainView).addView(loveImageCopy3);

        markNameLoved(untaggedNamesView.getText().toString());
        final ViewPropertyAnimator anim = loveImageCopy.animate();

        anim.setListener(new android.animation.Animator.AnimatorListener(){

            @Override
            public void onAnimationStart(Animator animation){}

            @Override
            public void onAnimationCancel(Animator animation){}

            @Override
            public void onAnimationRepeat(Animator animation){}

            @Override
            public void onAnimationEnd(Animator animation){
                untaggedNamesView.setText(getNextUntaggedName());
                ((ViewGroup) mainView).removeView(loveImageCopy);
                ((ViewGroup) mainView).removeView(loveImageCopy2);
                ((ViewGroup) mainView).removeView(loveImageCopy3);
            }
        });
        anim.translationXBy(-700).setDuration(1000);
        anim.translationYBy(-500).setDuration(500);
        loveImageCopy2.animate().translationXBy(-600).setDuration(1000);
        loveImageCopy2.animate().translationYBy(200).setDuration(1000);
        loveImageCopy3.animate().translationXBy(-1500).setDuration(1000);
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
                activity.getString(R.string.mark_unloved_dialog_title), activity.getString(R.string.mark_unloved_dialog_body), R.drawable.dislove);

        lovedNamesListView.setAdapter(lovedAdapter);


        unlovedNamesListView = (ListView) activity.findViewById(R.id.unloved_names);

        SwitchListsCallBack unlovedToLovedSwitch = new SwitchListsCallBack() {
            @Override
            public void switchLists(String name) {
                markNameLoved(name);
            }
        };

        unlovedAdapter = new EditableListViewAdapter(unlovedToLovedSwitch, unlovedNames, activity,
                activity.getString(R.string.mark_loved_dialog_title), activity.getString(R.string.mark_loved_dialog_body), R.drawable.love);
        unlovedNamesListView.setAdapter(unlovedAdapter);
        untaggedNamesView = (TextView) activity.findViewById(R.id.untagged_names_view);
        untaggedNamesView.setText(getNextUntaggedName());

        untaggedNamesView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeRight() {
                swipeLeft();
            }
            public void onSwipeLeft() {
                swipeRight();
            }

        });
    }

    public static void markNameLoved(String name) {
        if (!name.equals(END_OF_LIST)) {
            lovedNames.add(name);
            untaggedNames.remove(name);
            unlovedNames.remove(name);
            DbAccess databaseAccess = DbAccess.getInstance(context);
            databaseAccess.open();
            databaseAccess.markNameLoved(GroupSettings.getCurrentUser(), name);
            databaseAccess.close();
        }
    }

    public static void markNameUnloved(String name) {
        if (!name.equals(END_OF_LIST)) {
            unlovedNames.add(name);
            untaggedNames.remove(name);
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
        else
            return untaggedNames.get(rgenerator.nextInt(untaggedNames.size()));
    }

    public static void addName(String name) {
        // replace non-letters and trim edge white spaces
        name = name.replaceAll("-", " ");
        name = name.replaceAll("[^\\p{L}\\s]", "").trim();
        if (name.isEmpty() || lovedNames.contains(name) || unlovedNames.contains(name))
            return;

        markNameLoved(name);
        untaggedNames.remove(name);
        if (untaggedNamesView.getText().equals(name)) {
            untaggedNamesView.setText(getNextUntaggedName());
        }

//        if (!names.contains(name)) {
//            names.add(name);
            // TODO: and add to db
//        }


    }

}

