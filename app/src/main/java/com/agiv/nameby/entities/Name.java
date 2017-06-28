package com.agiv.nameby.entities;

import com.agiv.nameby.Settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Noa Agiv on 1/9/2017.
 */

public class Name {
    public String id;
    public String name;
    public String gender;
    public int popularity;
//    public Map<String, NameTag> userTags = new HashMap<>();

    public Name(String name, String gender, int popularity){
      this.name = name;
      this.gender = gender;
      this.popularity = popularity;
    }

    public void setId(String id) {
        this.id = id;
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




//    public NameTag getTag(String user) {
//        return userTags.get(user);
//    }

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
        return Integer.valueOf(id);
    }

    @Override
    public String toString() {
        return "Name{" +
                name  +
                "(" + gender + '\'' +
                ") #" + popularity +
                '}';
    }
}
