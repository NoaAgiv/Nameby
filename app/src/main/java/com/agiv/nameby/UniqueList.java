package com.agiv.nameby;

import android.util.Log;

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
        if (this.contains(object))
            this.remove(object);
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

    public void conditionalAddAll(Collection<T> collection, ListCondition<T> listCond){
        for (T obj : collection) {
            if (listCond.apply(obj)) {
                this.add(obj);
            }
        }
    }

    public void addIf(T obj, ListCondition<T> listCond){
        if (listCond.apply(obj)) {
            this.add(obj);
        }

    }

    public interface ListCondition<T>{
        boolean apply(T obj);
    }

}
