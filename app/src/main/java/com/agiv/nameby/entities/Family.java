package com.agiv.nameby.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noa Agiv on 4/16/2017.
 */

public class Family {
    public String id;
    public String name;
    public String gender;
    private List<String> members; // has no meaning, just for POJO conversion to work
    public List<Member> familyMembers = new ArrayList<>(); //must not be called members as the json key

    public void setId(String id) {
        this.id = id;
    }

    public void addMember(Member member) {
        familyMembers.add(member);
    }
}
