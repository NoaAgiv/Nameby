package com.agiv.nameby.entities;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Noa Agiv on 4/16/2017.
 */

public class Family {
    public String id;
    public String name;
    public String gender;
    private List<String> members; // has no meaning, just for POJO conversion to work
    public List<Member> familyMembers = new ArrayList<>(); //must not be called members as the json key
    Pattern namePattern = Pattern.compile("[\\u0590-\\u05FF \\\\p{Graph} \\\\s]+", Pattern.UNICODE_CASE);
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public void setId(String id) {
        this.id = id;
    }

    public Member getMember(String memberId){
        for (Member m : familyMembers){
            if (m.id.equals(memberId))
                return m;
        }
        return null;
    }
    public void addMember(Member member) {
        familyMembers.add(member);
        Log.i("Family",String.format("added %s to %s", member, this));
    }

    public boolean isUnanimouslyPositive(Name name) {
        for (Member member : familyMembers) {
            if (!member.isPositiveTag(name))
                return false;
        }
        return true;
    }

    public boolean isPositiveForSomeone(Name name) {
        for (Member member : familyMembers) {
            if (member.isPositiveTag(name))
                return true;
        }
        return false;
    }

    public boolean isFullyInitiated(Name name) {
        for (Member member : familyMembers) {
            System.out.println(name.name);
            if (member.getTag(name)==null) {
                System.out.println("untagged" + name.name);
                return false;
            }
        }
        return true;
    }

    public boolean setName(String name) {
        if (!namePattern.matcher(name).matches())
            return false;

        this.name = name;
        DatabaseReference familyNameRef = database.getReference("families/" + this.id + "/name");
        familyNameRef.setValue(name);
        return true;
    }

    @Override
    public String toString() {
        return "Family{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender +
                '}';
    }
}
