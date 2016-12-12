package com.agiv.names2;

import java.util.ArrayList;

/**
 * Created by Noa Agiv on 12/12/2016.
 */

public class NameTagger {
    public ArrayList<String> untaggedNames;
    public ArrayList<String> lovedNames = new ArrayList<>();
    public ArrayList<String> unlovedNames = new ArrayList<>();

    private void markNameLoved(String name) {
        if (!name.equals(END_OF_LIST)) {
            lovedNames.add(name);
            untaggedNames.remove(name);
            DbAccess databaseAccess = DbAccess.getInstance(this);
            databaseAccess.open();
            databaseAccess.markNameLoved("Noa", name);
            databaseAccess.close();
        }
    }

    private void markNameUnloved(String name) {
        if (!name.equals(END_OF_LIST)) {
            unlovedNames.add(name);
            untaggedNames.remove(name);
            DbAccess databaseAccess = DbAccess.getInstance(MainActivity.this);
            databaseAccess.open();
            databaseAccess.markNameUnloved("Noa", name);
            databaseAccess.close();
        }
    }
}
