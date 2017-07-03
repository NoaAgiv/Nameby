package com.agiv.nameby.entities;

import android.content.Context;
import android.util.Log;
import android.widget.Adapter;

import com.agiv.nameby.NameList;
import com.agiv.nameby.R;

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
    public Map<String, NameTag> nameTags = new HashMap<>();

    public Member() {
    }

    public Member(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
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
            return tag.equals(NameTag.loved) || tag.equals(NameTag.maybe);
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
        nameTags.put(name.id, tag);
        System.out.println("set" + name.name + tag + nameTags.get(name.id));
    }

    public void tagName(Name name, String tag) {
        nameTags.put(name.id, NameTag.valueOf(tag));
    }

    public NameTag getTag(Name name) {
        System.out.println("get" + name.name +  nameTags.get(name.id));
        return nameTags.get(name.id);
    }

    public boolean isPositiveTag(Name name) {
        return NameTag.isTagPositive(getTag(name));
    }

    public void updateDetails(Member member){
        String old = member.toString();
        setName(member.name);
        setEmail(member.email);
        Log.i("Member", String.format("updated %s to %s", old, member));
    }

    @Override
    public String toString() {
        return name + "\n" + email;
//        return "Member{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                '}';
    }
}
