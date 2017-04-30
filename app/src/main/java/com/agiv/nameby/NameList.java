package com.agiv.nameby;

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
            return  name.isNameComplete();
        }
    };

    public static UniqueList.ListCondition femaleFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return  name.isNameComplete() && name.isFemale();
        }
    };

    public static UniqueList.ListCondition maleFilter = new UniqueList.ListCondition<Name>() {
        @Override
        public boolean apply(Name name) {
            return name.isNameComplete() && name.isMale();
        }
    };

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

    public Name getById(String id){
        return this.firstSatisfies(new NameList.IdCondition(id));
    }
}
