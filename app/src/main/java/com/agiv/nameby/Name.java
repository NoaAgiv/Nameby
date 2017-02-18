package com.agiv.nameby;

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

    public static void setLovedByPartner(List<Name2> allNames, List<Name2> lovedByPartner){
        for (Name2 name : allNames){
            if (lovedByPartner.contains(name))
                name.setLovedByPartner();
        }
    }

}
