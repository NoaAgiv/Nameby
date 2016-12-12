package com.agiv.names2;

import android.app.Activity;
import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
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

    public static void getNamesFromDb() throws IOException {
        DbAccess databaseAccess = DbAccess.getInstance(activity);
        databaseAccess.initialDbPopulate();
        databaseAccess.open();
        untaggedNames = databaseAccess.getUntaggedNames("Noa");
        lovedNames = databaseAccess.getLovedNames("Noa");
        unlovedNames = databaseAccess.getUnlovedNames("Noa");
        databaseAccess.close();
    }


    private static void setListAdapters() {
        lovedNamesListView = (ListView) activity.findViewById(R.id.loved_names);
        lovedAdapter = new EditableListViewAdapter(lovedNames, unlovedNames, activity,
                activity.getString(R.string.mark_unloved_dialog_title), activity.getString(R.string.mark_unloved_dialog_body), R.drawable.dislove);

        lovedNamesListView.setAdapter(lovedAdapter);


        unlovedNamesListView = (ListView) activity.findViewById(R.id.unloved_names);

        unlovedAdapter = new EditableListViewAdapter(unlovedNames, lovedNames, activity,
                activity.getString(R.string.mark_loved_dialog_title), activity.getString(R.string.mark_loved_dialog_body), R.drawable.love);
        unlovedNamesListView.setAdapter(unlovedAdapter);
        untaggedNamesView = (TextView) activity.findViewById(R.id.untagged_names_view);
        untaggedNamesView.setText(getNextUntaggedName());

        untaggedNamesView.setOnTouchListener(new OnSwipeTouchListener(context) {

            public void onSwipeRight() {

                Toast.makeText(context, "unloved", Toast.LENGTH_SHORT).show();
                markNameUnloved(untaggedNamesView.getText().toString());
                untaggedNamesView.setText(getNextUntaggedName());

            }

            public void onSwipeLeft() {
                Toast.makeText(context, "loved", Toast.LENGTH_SHORT).show();
                markNameLoved(untaggedNamesView.getText().toString());
                untaggedNamesView.setText(getNextUntaggedName());
            }

        });
    }

    private static void markNameLoved(String name) {
        if (!name.equals(END_OF_LIST)) {
            lovedNames.add(name);
            untaggedNames.remove(name);
            DbAccess databaseAccess = DbAccess.getInstance(context);
            databaseAccess.open();
            databaseAccess.markNameLoved("Noa", name);
            databaseAccess.close();
        }
    }

    private static void markNameUnloved(String name) {
        if (!name.equals(END_OF_LIST)) {
            unlovedNames.add(name);
            untaggedNames.remove(name);
            DbAccess databaseAccess = DbAccess.getInstance(context);
            databaseAccess.open();
            databaseAccess.markNameUnloved("Noa", name);
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

        lovedNames.add(name);
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
