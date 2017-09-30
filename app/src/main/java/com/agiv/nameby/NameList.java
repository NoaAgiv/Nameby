package com.agiv.nameby;

import com.agiv.nameby.entities.Member;
import com.agiv.nameby.entities.Name;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noa Agiv on 2/28/2017.
 */

public class NameList extends UniqueList<Name> {

    public List<String> getIds(){
        List<String> ids = new ArrayList<>();
        for (Name name : this){
            ids.add(name.id);
        }
        return ids;
    }

    public static UniqueList.ListCondition validNameFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return  name.nameIsComplete();
        }
    };

    public static UniqueList.ListCondition femaleFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return  name.nameIsComplete() && name.female();
        }
    };

    public static UniqueList.ListCondition maleFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return name.nameIsComplete() && name.male();
        }
    };

    public static UniqueList.ListCondition untaggedFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            Member member = Settings.getMember();
            if (member==null)
                return false;
            Member.NameTag tag = member.getTag(name);
            if (tag==null){
                return false;
            }
            return tag.equals(Member.NameTag.untagged);
        }
    };

    public static UniqueList.ListCondition someFamilyMembersLovedFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return Settings.getFamily().isPositiveForSomeone(name);
        }
    };

    public static UniqueList.ListCondition unanimouslyPositiveFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return Settings.getFamily().isUnanimouslyPositive(name);
        }
    };

    public static UniqueList.ListCondition fullyInitiatedFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return Settings.getFamily().isFullyInitiated(name);
        }
    };

    public static UniqueList.ListCondition identityFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return true;
        }
    };

    public static class tagFilter implements ListCondition<Name>{
        Member.NameTag tag;

        public tagFilter(Member.NameTag tag){
            this.tag = tag;
        }
        @Override
        public boolean apply(Name name){
            return tag.equals(Settings.getMember().getTag(name));
        }
    }


    public static class IdCondition implements ListCondition<Name>{
        String id;

        public IdCondition(String id){
            this.id = id;
        }
        @Override
        public boolean apply(Name name){
            return name.id == id;
        }
    }

    public static class NameCondition implements ListCondition<Name>{
        String nameStr;

        public NameCondition(String nameStr){
            this.nameStr = nameStr;
        }
        @Override
        public boolean apply(Name name){
            return name.name.equals(nameStr);
        }
    }

    public Name getById(String id){
        return this.firstSatisfies(new NameList.IdCondition(id));
    }

    public Name get(String name){
        return this.firstSatisfies(new NameList.NameCondition(name));
    }

}
