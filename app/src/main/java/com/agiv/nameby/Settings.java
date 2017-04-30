package com.agiv.nameby;

import android.content.SharedPreferences;

import com.agiv.nameby.entities.Family;
import com.agiv.nameby.entities.Member;

/**
 * Created by Noa Agiv on 12/31/2016.
 */

public class Settings {
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;
    static Gender gender;
    public static String currentUser;

    static String notCurrentUser;
    static String greenUser;
    static String yellowUser;
    static int yellowUserUnseenMatches;
    static int greenUserUnseenMatches;
    static boolean familyMembersEdited = false;
    static boolean helpScreenSeen = false;

    static String memberId;
    static Member member;
    static String familyId = "1";
    static Family family;

    public enum Gender {
        FEMALE,
        MALE
    }

    public static Family getFamily() {
        return family;
    }

    public static void setFamily(Family family) {
        Settings.family = family;
    }

    public static String getFamilyId() {
        return familyId;
    }

    public static void setFamilyId(String familyId) {
        Settings.familyId = familyId;
    }

    public static Member getMember() {
        return member;
    }

    public static void setMember(Member member) {
        Settings.member = member;
    }

    public static void setMemberId(String id) {
        Settings.memberId = id;
    }

    public static void init(SharedPreferences shp){
        sharedPref = shp;
        editor = shp.edit();
    }

    public static boolean isHelpScreenSeen() {
        return sharedPref.getBoolean("help_screen_screen", false);
    }

    public static void setIsHelpScreenSeen(boolean isHelpScreenSeen) {
        editor.putBoolean("help_screen_screen", isHelpScreenSeen);
        editor.commit();
        Settings.helpScreenSeen = isHelpScreenSeen;
    }

    public static boolean isFamilyMembersEdited() {
        return sharedPref.getBoolean("family_member_edited", false);
    }

    public static void setFamilyMembersEdited(boolean familyMembersEdited) {
        editor.putBoolean("family_member_edited", familyMembersEdited);
        editor.commit();
        Settings.familyMembersEdited = familyMembersEdited;
    }

    public static int getYellowUserUnseenMatches() {
        return sharedPref.getInt("yellow_user_unseen_mathces", 0);
    }

    public static void setYellowUserUnseenMatches(int yellowUserUnseenMatches) {
        editor.putInt("yellow_user_unseen_mathces", yellowUserUnseenMatches);
        editor.commit();
        Settings.yellowUserUnseenMatches = yellowUserUnseenMatches;
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
        Settings.greenUserUnseenMatches = greenUserUnseenMatches;
    }

    public static Gender getGender() {
        return sharedPref.getInt("gender", -1)==-1 ? null : Gender.values()[sharedPref.getInt("gender", -1)];
    }

    public static String getGenderString() {
        if (getGender().equals(Gender.FEMALE))
            return "f";
        else
            return "m";
    }

    public static void setGender(Gender gender) {
        Settings.gender = gender;
        editor.putInt("gender", gender.ordinal());
        editor.commit();

    }

    public static void unsetGender() {
        editor.putInt("gender", -1);
        editor.commit();
        Settings.gender = null;
    }

    public static String getMemberId(){
        return memberId;
    }

    public static String getCurrentUser() {
        return sharedPref.getString("user", null);
    }

    public static void setCurrentUser(String currentUser) {
        Settings.currentUser = currentUser;
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
        Settings.greenUser = greenUser;
    }

    public static String getYellowUser() {
        return yellowUser;
    }

    public static void setYellowUser(String yellowUser) {
        Settings.yellowUser = yellowUser;
    }

    public static String getNotCurrentUser() {
        return notCurrentUser;
    }

    public static void setNotCurrentUser(String notCurrentUser) {
        Settings.notCurrentUser = notCurrentUser;
    }
}
