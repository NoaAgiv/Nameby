package com.agiv.nameby;

import java.security.acl.Group;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Noa Agiv on 1/9/2017.
 */

public class Name {
    public int id;
    public String name;
    public String gender;
    public int popularity;
    public Map<String, NameTag> userTags = new HashMap<>();

    enum NameTag{
        untagged,
        loved,
        unloved,
        maybe;


        public static boolean isTagPositive(NameTag tag){
            return tag.equals(NameTag.loved) || tag.equals(NameTag.maybe);
        }
    }



    public Name(String name, String gender, int popularity){
      this.name = name;
      this.gender = gender;
      this.popularity = popularity;
    }

    public Name() {
    }

    public boolean isFemale(){
        return gender.equals("f");
    }

    public boolean isMale(){
        return gender.equals("m");
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void tagName(String tag) {
        NameTag tagEnum = NameTag.valueOf(tag);
        userTags.put(GroupSettings.currentUser, tagEnum);
    }

    public void tagName(NameTag tag) {
        userTags.put(GroupSettings.currentUser, tag);
    }

    public void tagName(String user, String tag) {
        NameTag tagEnum = NameTag.valueOf(tag);
        userTags.put(user, tagEnum);
    }

    public void tagName(String user, NameTag tag) {
       userTags.put(user, tag);
    }

    public NameTag getTag() {
        return userTags.get(GroupSettings.getCurrentUser());
    }

    public boolean isUnanimouslyPositive() {
        for (NameTag tag : userTags.values()) {
            if (!NameTag.isTagPositive(tag))
                return false;
        }
        return true;
    }

    public boolean isPositiveForAnyPartner() {
        for (NameTag tag : userTags.values()) {
            if (NameTag.isTagPositive(tag))
                return true;
        }
        return false;
    }



    public NameTag getTag(String user) {
        return userTags.get(user);
    }

//    public void setLovedByPartner(){
//        lovedByPartner = true;
//    }

//    public static void setLoved(List<Name> untaggedNames, List<Integer> ids){
//        for (Name name : untaggedNames){
//            if (ids.contains(name.id))
//                name.setLoved(true);
//        }
//    }

//    public static void setLovedByPartner(List<Name> allNames, List<Name> lovedByPartner){
//        for (Name name : allNames){
//            if (lovedByPartner.contains(name))
//                name.setLovedByPartner();
//        }
//    }

    public boolean isNameComplete(){
        // when a new name is added, its field might be added one by one,
        // resulting in temporarily missing fields
        return name != null && gender != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name name = (Name) o;

        return id == name.id;

    }

    @Override
    public int hashCode() {
        return id;
    }


}
