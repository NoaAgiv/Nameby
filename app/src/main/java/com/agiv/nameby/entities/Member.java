package com.agiv.nameby.entities;

import android.util.Log;
import android.view.View;
import android.widget.Adapter;

import com.agiv.nameby.NameList;
import com.agiv.nameby.Settings;

import java.util.HashMap;
import java.util.Map;
import static com.agiv.nameby.NameTagger2.*;
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
        untagged(untaggedNames, untaggedAdapter),
        loved(lovedNames, lovedAdapter),
        unloved(unlovedNames, unlovedAdapter),
        maybe(maybeNames, maybeAdapter);

        public NameList nameList;
        public Adapter adapter;

        NameTag(NameList nameList, Adapter adapter){
            this.nameList = nameList;
            this.adapter = adapter;
        }

        public static boolean isTagPositive(NameTag tag){
            return tag.equals(NameTag.loved) || tag.equals(NameTag.maybe);
        }
    }

    public void tagName(Name name, NameTag tag) {
        nameTags.put(name.id, tag);
    }

    public void tagName(Name name, String tag) {
        nameTags.put(name.id, NameTag.valueOf(tag));
    }

    public NameTag getTag(Name name) {
        return nameTags.get(name.id);
    }

    public boolean isPositiveTag(Name name) {
        return NameTag.isTagPositive(getTag(name));
    }

    public void updateDetails(Member member){
        String old = member.toString();
        setName(member.name);
        setEmail(member.email);
        Log.i("Member", String.format("updated $s to $s", old, member));
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
