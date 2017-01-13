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
