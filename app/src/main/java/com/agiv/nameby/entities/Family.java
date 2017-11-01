package com.agiv.nameby.entities;

import android.util.Log;

import com.agiv.nameby.Settings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Noa Agiv on 4/16/2017.
 */

public class Family {
    public String id;
    public String name;
    public String gender;
    private List<String> members; // has no meaning, just for POJO conversion to work
    public List<Member> familyMembers = new ArrayList<>(); //must not be called members as the json key
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String notificationTopic;

    public void setId(String id) {
        this.id = id;
        this.notificationTopic = "Family_" + id;
    }

    private void subscribeToNotifications(){
        FirebaseMessaging.getInstance().subscribeToTopic(notificationTopic);
    }

    public void sendNotification(String title, String message){
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("message", message);
        data.put("topic", notificationTopic);
        database.getReference("messages").push().setValue(data);
    }

    public Member getMember(String memberId){
        for (Member m : familyMembers){
            if (m.id.equals(memberId))
                return m;
        }
        return null;
    }
    public void addMember(Member member) {
        for (Member m : familyMembers){
            if (m.id.equals(member.id)) { //same email as well
                m.setName(member.name);
                return;
            }
        }
        familyMembers.add(member);
        subscribeToNotifications();
        Log.i("Family",String.format("added %s to %s", member, this));
    }

    public static Family addSaveFamily(String defaultFamilyName){
        Family family = new Family();
        family.gender = Settings.Gender.toOneLetter(Settings.Gender.FEMALE);
        family.name = defaultFamilyName;
        DatabaseReference familiesRef = database.getReference("families");
        String id = familiesRef.push().getKey();
        family.setId(id);
        familiesRef.child(id).setValue(family);
        return family;
    }

    public void addSaveMember(Member member) {
        addMember(member);
        DatabaseReference familyMembersRef = database.getReference("families/" + this.id + "/members");
        DatabaseReference familyMemberRef = familyMembersRef.child(String.valueOf(member.id));
        familyMemberRef.setValue(true);
    }

    public void removeSaveMember(Member member) {
        familyMembers.remove(member);
        DatabaseReference familyMembersRef = database.getReference("families/" + this.id + "/members");
        familyMembersRef.child(String.valueOf(member.id)).removeValue();
        Log.i("Family",String.format("removed %s from %s", member, this));
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
            if (member.getTag(name)==null) {
                return false;
            }
        }
        return true;
    }

    public boolean setName(String name) {
        if (!Name.namePattern.matcher(name).matches())
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
