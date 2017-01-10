package com.agiv.names2;

import java.util.List;

/**
 * Created by Noa Agiv on 1/9/2017.
 */

public class Name {
    public String name;
    public int popularity;
    public boolean lovedByPartner = false;

    public Name(String name, int popularity){
      this.name = name;
      this.popularity = popularity;
    }

    public void setLovedByPartner(){
        lovedByPartner = true;
    }

    public static void setLovedByPartner(List<Name> allNames, List<String> lovedByPartner){
        for (Name name : allNames){
            if (lovedByPartner.contains(name.name))
                name.setLovedByPartner();
        }
    }

}
