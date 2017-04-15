package com.agiv.nameby;

import java.util.Objects;

/**
 * Created by Noa Agiv on 2/28/2017.
 */

public class NameList extends UniqueList<Name> {

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
        int id;

        public IdCondition(int id){
            this.id = id;
        }
        @Override
        public boolean apply(Name name){
            return name.id == id;
        }
    }

    public Name getById(int id){
        return this.firstSatisfies(new NameList.IdCondition(id));
    }
}
