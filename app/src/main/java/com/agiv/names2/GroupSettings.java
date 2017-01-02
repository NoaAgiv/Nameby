package com.agiv.names2;

/**
 * Created by Noa Agiv on 12/31/2016.
 */

public class GroupSettings {
    static Sex sex;

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
}
