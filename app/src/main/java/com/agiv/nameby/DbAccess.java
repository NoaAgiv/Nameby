package com.agiv.nameby;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Noa Agiv on 12/12/2016.
 */

public class DbAccess {


    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DbAccess instance;
    private Context context;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DbAccess(Context context) {
        this.openHelper = new NameTableDBHelper(context);
        this.context = context;
    }

    public void initialDbPopulate() throws IOException {
        // Check if the database exists before copying
        boolean initialiseDatabase = context.getDatabasePath("names.db").exists();
        if (initialiseDatabase == false) {

            // Open the .db file in your assets directory
            InputStream is = context.getAssets().open("databases/names.db");

            // Copy the database into the destination
            OutputStream os = new FileOutputStream(context.getDatabasePath("names.db"));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0){
                os.write(buffer, 0, length);
            }
            os.flush();

            os.close();
            is.close();
        }
    }


    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DbAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DbAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    public ArrayList<Name> getNames() {
        Cursor cursor = database.rawQuery("SELECT name, popularity FROM names where sex = \"" + GroupSettings.getSexString() + "\"", null);

        ArrayList<Name> list = populateNameList(cursor);
        cursor.close();
        return list;
    }

    public ArrayList<String> getUsers() {
        Cursor cursor = database.rawQuery("SELECT name FROM users", null);
        ArrayList<String> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public void editUserName(String oldUserName, String newUserName) {
        ContentValues values = new ContentValues();
        values.put("name", newUserName);
        database.update("users", values, "name = \"" + oldUserName + "\"", null);
    }

    private ArrayList<Name> getNames(String user, boolean loved) {
        int loved_int = loved? 1 : 0;
        String query =
                "SELECT names.name, names.popularity from names " +
                "JOIN names_users on names.id = names_users.name_id " +
                "JOIN users on users.id = names_users.user_id " +
                "WHERE sex =\"" + GroupSettings.getSexString() + "\"" +
                        "and users.name = \"" + user + "\" " +
                        "and names_users.is_liked = " + loved_int;


        Cursor cursor = database.rawQuery(query, null);
        ArrayList<Name> list = populateNameList(cursor);
        cursor.close();
        return list;
    }

    public ArrayList<Name> populateNameList(Cursor cursor){
        ArrayList<Name> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Name(cursor.getString(0), cursor.getInt(1)));
            cursor.moveToNext();
        }
        return list;
    }

    public ArrayList<String> getLovedNames(String user) {
        ArrayList<String> lovedNames = new ArrayList<>();
        for (Name name : getNames(user, true)){
            lovedNames.add(name.name);
        }
        return lovedNames;
    }

    public ArrayList<String> getUnlovedNames(String user) {
        ArrayList<String> unlovedNames = new ArrayList<>();
        for (Name name : getNames(user, false)){
            unlovedNames.add(name.name);
        }
        return unlovedNames;
    }

    public ArrayList<Name> getUntaggedNames(String user) {
        ArrayList<Name> untaggedNames = new ArrayList<>();
        ArrayList<Name> names = ((ArrayList<Name>) getNames().clone());
        for (Name name : names){
            if (!getLovedNames(user).contains(name.name) && !getUnlovedNames(user).contains(name.name))
                    untaggedNames.add(name);
        }
        return untaggedNames;
    }
    public void markNameLoved(String user, String name) {
        insertNameTag(user, name, true);
    }

    public void markNameUnloved(String user, String name) {
        insertNameTag(user, name, false);
    }

    private void insertNameTag(String user, String name, boolean loved) {
        Cursor cursor = database.rawQuery("SELECT id FROM users WHERE name = \""+ user +"\"", null);
        cursor.moveToFirst();
        int user_id = cursor.getInt(0);

        int name_id;
        cursor = database.rawQuery("SELECT id FROM names WHERE sex = \""+ GroupSettings.getSexString() +"\" and name = \""+ name + "\"", null);
        if (cursor.getCount() == 0){ //name does not exist
            ContentValues nameTableValues = new ContentValues();
            nameTableValues.put(NameContract.NameEntry.TABLE_NAMES_NAME, name);
            nameTableValues.put(NameContract.NameEntry.TABLE_NAMES_INSERTED_BY, user_id);
            nameTableValues.put(NameContract.NameEntry.TABLE_NAMES_POPULARITY, -1);
            nameTableValues.put(NameContract.NameEntry.TABLE_NAMES_SEX, GroupSettings.getSexString());
            nameTableValues.put(NameContract.NameEntry.TABLE_NAMES_DATE_INSERTED, new SimpleDateFormat("yyyy-MM-dd").toString());
            name_id = ( (int) database.insert(NameContract.NameEntry.TABLE_NAMES, null , nameTableValues));
        }
        else {
            cursor.moveToFirst();
            name_id = cursor.getInt(0);
        }

        ContentValues values = new ContentValues();
        values.put(NameContract.NameEntry.TABLE_NAMES_IS_LIKED, loved);
        cursor = database.rawQuery("SELECT is_liked FROM names_users WHERE user_id = \""+ user_id +"\" and name_id =  \""+ name_id + "\" ", null);
        if (cursor.getCount()==0) {
            values.put(NameContract.NameEntry.TABLE_NAMES_USERS_NAME_ID, name_id);
            values.put(NameContract.NameEntry.TABLE_NAMES_USERS_USER_ID, user_id);
            database.insert(NameContract.NameEntry.TABLE_NAMES_USERS, null, values);
        }
        else {
            database.update(NameContract.NameEntry.TABLE_NAMES_USERS, values, "user_id = \"" + user_id + "\" and name_id =  \"" + name_id + "\"", null);
        }
        database.close();
    }


}


