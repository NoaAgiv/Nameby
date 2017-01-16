package com.agiv.names2;

import android.content.SharedPreferences;

/**
 * Created by Noa Agiv on 12/31/2016.
 */

public class GroupSettings {
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;
    static Sex sex;
    static String currentUser;
    static String notCurrentUser;
    static String greenUser;
    static String yellowUser;
    static int yellowUserUnseenMatches;
    static int greenUserUnseenMatches;
    static boolean familyMembersEdited = false;

    public enum Sex{
        FEMALE,
        MALE
    }

    public static void init(SharedPreferences shp){
        sharedPref = shp;
        editor = shp.edit();
    }


    public static boolean isFamilyMembersEdited() {
        return sharedPref.getBoolean("family_member_edited", false);
    }

    public static void setFamilyMembersEdited(boolean familyMembersEdited) {
        editor.putBoolean("family_member_edited", familyMembersEdited);
        editor.commit();
        GroupSettings.familyMembersEdited = familyMembersEdited;
    }

    public static int getYellowUserUnseenMatches() {
        return sharedPref.getInt("yellow_user_unseen_mathces", 0);
    }

    public static void setYellowUserUnseenMatches(int yellowUserUnseenMatches) {
        editor.putInt("yellow_user_unseen_mathces", yellowUserUnseenMatches);
        editor.commit();
        GroupSettings.yellowUserUnseenMatches = yellowUserUnseenMatches;
    }

    public static int getCurrentUserUnseenMatches() {
        if (currentUser.equals(getGreenUser())){
            return getGreenUserUnseenMatches();
        }
        else if (currentUser.equals(getYellowUser())){
            return getYellowUserUnseenMatches();
        }
        return 0;
    }

    public static void setCurrentUserUnseenMatches(int userUnseenMatches) {
        if (currentUser.equals(getGreenUser())){
            setGreenUserUnseenMatches(userUnseenMatches);
        }
        else if (currentUser.equals(getYellowUser())){
            setYellowUserUnseenMatches(userUnseenMatches);
        }
    }

    public static void increasePartnerUnseenMatches() {
        if (currentUser.equals(getGreenUser())){
            setYellowUserUnseenMatches(getYellowUserUnseenMatches() + 1);
        }
        else if (currentUser.equals(getYellowUser())){
            setGreenUserUnseenMatches(getGreenUserUnseenMatches() + 1);
        }
    }

    public static int getGreenUserUnseenMatches() {
        return sharedPref.getInt("green_user_unseen_mathces", 0);
    }

    public static void setGreenUserUnseenMatches(int greenUserUnseenMatches) {
        editor.putInt("green_user_unseen_mathces", greenUserUnseenMatches);
        editor.commit();
        GroupSettings.greenUserUnseenMatches = greenUserUnseenMatches;
    }

    public static Sex getSex() {
        return sharedPref.getInt("sex", -1)==-1 ? null : GroupSettings.Sex.values()[sharedPref.getInt("sex", -1)];
    }

    public static String getSexString() {
        if (getSex().equals(Sex.FEMALE))
            return "f";
        else
            return "m";
    }

    public static void setSex(Sex sex) {
        GroupSettings.sex = sex;
        editor.putInt("sex", sex.ordinal());
        editor.commit();

    }

    public static void unsetSex() {
        editor.putInt("sex", -1);
        editor.commit();
        GroupSettings.sex = null;
    }

    public static String getCurrentUser() {
        return sharedPref.getString("user", null);
    }

    public static void setCurrentUser(String currentUser) {
        GroupSettings.currentUser = currentUser;
        editor.putString("user", currentUser);
        editor.commit();
        if (currentUser.equals(getGreenUser())){
            setNotCurrentUser(getYellowUser());
        }
        else if (currentUser.equals(getYellowUser())){
            setNotCurrentUser(getGreenUser());
        }
    }

    public static void changeUser(){
        String curUserTmp = currentUser;
        setCurrentUser(notCurrentUser);
        setNotCurrentUser(curUserTmp);
    }

    public static String getGreenUser() {
        return greenUser;
    }

    public static void setGreenUser(String greenUser) {
        GroupSettings.greenUser = greenUser;
    }

    public static String getYellowUser() {
        return yellowUser;
    }

    public static void setYellowUser(String yellowUser) {
        GroupSettings.yellowUser = yellowUser;
    }

    public static String getNotCurrentUser() {
        return notCurrentUser;
    }

    public static void setNotCurrentUser(String notCurrentUser) {
        GroupSettings.notCurrentUser = notCurrentUser;
    }
}
