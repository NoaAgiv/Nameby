package com.agiv.nameby;

import java.util.List;

/**
 * Created by Noa Agiv on 1/9/2017.
 */

public class Name2 {
    public String name;
    public String gender;
    public int popularity;
    public boolean lovedByPartner = false;

    public Name2(String name, String gender, int popularity){
      this.name = name;
      this.gender = gender;
      this.popularity = popularity;
    }

    public Name2() {
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

    public static void setLovedByPartner(List<Name2> allNames, List<Name2> lovedByPartner){
        for (Name2 name : allNames){
            if (lovedByPartner.contains(name))
                name.setLovedByPartner();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Name2 name2 = (Name2) o;

        if (!name.equals(name2.name)) return false;
        return gender.equals(name2.gender);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + gender.hashCode();
        return result;
    }
}
