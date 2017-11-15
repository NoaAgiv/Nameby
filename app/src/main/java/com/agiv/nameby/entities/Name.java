package com.agiv.nameby.entities;

import com.agiv.nameby.NameGenerator;
import com.agiv.nameby.R;
import com.agiv.nameby.Settings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.agiv.nameby.NameTagger.context;

/**
 * Created by Noa Agiv on 1/9/2017.
 */

public class Name {
    public String id;
    public String name;
    public String gender;
    public int popularity;
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();


    public static Pattern namePattern = Pattern.compile("[\\u0590-\\u05FF \\\\p{Graph} \\\\s]+", Pattern.UNICODE_CASE);

    public Name(String name, String gender, int popularity){
      if (!setName(name))
          throw new InvalidParameterException(context.getString(R.string.error_set_name) + " " + name);
      if (gender.equals("f") || gender.equals("m")) {
          this.gender = gender;
      } else
      {
          throw new InvalidParameterException("no such gender : " + gender);
      }
      this.popularity = popularity;
    }

    public Name(String name, String gender){
        if (!setName(name))
            throw new InvalidParameterException(context.getString(R.string.error_set_name));
        if (gender.equals("f") || gender.equals("m")) {
            this.gender = gender;
        } else
        {
            throw new InvalidParameterException("no such gender : " + gender);
        }
        this.popularity = 500;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Name() {
    }

    public boolean female(){
        return gender.equals("f");
    }

    public boolean male(){
        return gender.equals("m");
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public boolean setName(String name) {
        if (!Name.namePattern.matcher(name).matches())
            return false;

        this.name = name;
        return true;
    }

    public void save() {
        DatabaseReference membersRef = database.getReference("names");
        id = membersRef.push().getKey();
        membersRef.child(id).setValue(this);
    }

    public void increasePopularity(int addition){
        setPopularity(popularity + addition);
        DatabaseReference membersRef = database.getReference("names");
        membersRef.child(id).setValue(this);
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

    public boolean nameIsComplete(){
        // when a new name is added, its field might be added one by one,
        // resulting in temporarily missing fields
        return name != null && gender != null;
    }


    @Override
    public String toString() {
        return "Name{" +
                name  +
                "(" + gender + '\'' +
                ") #" + popularity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name name1 = (Name) o;

        if (!name.equals(name1.name)) return false;
        return gender.equals(name1.gender);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + gender.hashCode();
        return result;
    }
}
