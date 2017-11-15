package com.agiv.nameby;

import android.util.Log;

import com.agiv.nameby.entities.Name;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Noa Agiv on 2/27/2017.
 */

public class UniqueList<T> extends ArrayList<T> {
    @Override
    public boolean add(T object){
        int existsInIndex = this.indexOf(object);
        if (existsInIndex >= 0) {
            this.set(existsInIndex, object);
            return true;
        }
        else
            return super.add(object);
    };

    public T firstSatisfies(ListCondition<T> listCond){
        for (T obj : this){
            if (listCond.apply(obj)) {
                return obj;
            }
        }
        return null;
    }

    public void conditionalAddAll(Collection<T> collection, ListCondition<T> ... listConds){
        for (T obj : collection) {
            addIf(obj, listConds);
        }
    }

    public void addIf(T obj, ListCondition<T> ... listConds){
        for (ListCondition cond : listConds) {
            if (!cond.apply(obj))
                return;
        }
        this.add(obj);
    }

    public interface ListCondition<T>{
        boolean apply(T obj);
    }

}
