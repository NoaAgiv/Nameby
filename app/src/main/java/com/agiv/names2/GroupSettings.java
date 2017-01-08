package com.agiv.names2;

import android.content.SharedPreferences;

/**
 * Created by Noa Agiv on 12/31/2016.
 */

public class GroupSettings {
    static Sex sex;
    static String currentUser;

    public enum Sex{
        FEMALE,
        MALE
    }

    public static Sex getSex() {
        return sex;
    }

    public static String getSexString() {
        if (sex.equals(Sex.FEMALE))
            return "f";
        else
            return "m";
    }

    public static void setSex(Sex sex) {
        GroupSettings.sex = sex;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(String currentUser, SharedPreferences.Editor editor) {
        GroupSettings.currentUser = currentUser;
        editor.putString("user", currentUser);
        editor.commit();
    }
}
