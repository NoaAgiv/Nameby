package com.agiv.nameby.entities;

import android.content.Context;
import android.util.Log;
import android.widget.Adapter;

import com.agiv.nameby.NameList;
import com.agiv.nameby.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import static com.agiv.nameby.NameTagger.*;
/**
 * Created by Noa Agiv on 4/16/2017.
 */

public class Member {
    public String id;
    public String name;
    public String email;
    public String family;
    public Map<String, NameTag> nameTags = new HashMap<>();
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public Member() {
    }

    public Member(String name, String email) {
        if (!this.setName(name))
            throw new InvalidParameterException(context.getString(R.string.error_set_name));
        if (!this.setEmail(email))
            throw new InvalidParameterException(context.getString(R.string.error_set_email));


    }

    public static boolean isValidEmailAddress(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void save() {
        DatabaseReference membersRef = database.getReference("users");
        setId(Member.generateId(email));
        membersRef.child(id).setValue(this);
    }

    public boolean setName(String name) {
        if (!Name.namePattern.matcher(name).matches())
            return false;

        this.name = name;
        return true;
    }

    public boolean setEmail(String email) {
        if (!isValidEmailAddress(email))
            return false;
        this.email = email;
        return true;
    }

    public enum NameTag{
        untagged(R.string.untagged, untaggedNames, untaggedAdapter),
        loved(R.string.loved, lovedNames, lovedAdapter, R.drawable.love),
        unloved(R.string.unloved, unlovedNames, unlovedAdapter, R.drawable.dislove),
        maybe(R.string.maybe, maybeNames, maybeAdapter, R.drawable.next);

        public int displayName;
        public NameList nameList;
        public Adapter adapter;
        public Integer imageResId;

        NameTag(int displayName, NameList nameList, Adapter adapter){
            this.displayName = displayName;
            this.nameList = nameList;
            this.adapter = adapter;
            this.imageResId = 0;
        }

        NameTag(int displayName, NameList nameList, Adapter adapter, int imageResId){
            this(displayName, nameList, adapter);
            this.imageResId = imageResId;
        }

        public static boolean isTagPositive(NameTag tag){
            return tag != null && (tag.equals(NameTag.loved) || tag.equals(NameTag.maybe));
        }

        public static NameTag getTag(Context context, String displayName){
            for (NameTag tag : values()){
                if (displayName.equals(context.getString(tag.displayName))){
                    return tag;
                }
            }
            return null;
        }
    }

    public void tagName(Name name, NameTag tag) {
        System.out.println("tagging");
        nameTags.put(name.id, tag);
    }

    public void tagName(Name name, String tag) {
        nameTags.put(name.id, NameTag.valueOf(tag));
    }

    public NameTag getTag(Name name) {
        return nameTags.get(name.id);
    }

    public boolean isPositiveTag(Name name) {
        System.out.println("hi" + nameTags);
        return NameTag.isTagPositive(getTag(name));
    }

    public void updateDetails(Member member){
        String old = member.toString();
        setName(member.name);
        setEmail(member.email);
        Log.i("Member", String.format("updated %s to %s", old, member));
    }

    public static String generateId(String email){
        return email.replace(".", "").replace("@", "");
    }

    @Override
    public String toString() {
        return name + ": " + email;
//        return "Member{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                '}';
    }
}
