package com.agiv.nameby;

import java.util.List;

/**
 * Created by Noa Agiv on 1/9/2017.
 */

public class Name {
    public String name;
    public String gender;
    public int popularity;
    public boolean lovedByPartner = false;

    public Name(String name, String gender, int popularity){
      this.name = name;
      this.gender = gender;
      this.popularity = popularity;
    }

    public Name() {
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

    public void setLovedByPartner(){
        lovedByPartner = true;
    }

    public static void setLovedByPartner(List<Name> allNames, List<Name> lovedByPartner){
        for (Name name : allNames){
            if (lovedByPartner.contains(name))
                name.setLovedByPartner();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name name = (Name) o;

        if (!this.name.equals(name.name)) return false;
        return gender.equals(name.gender);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + gender.hashCode();
        return result;
    }
}
