package com.agiv.names2;

import android.provider.BaseColumns;

/**
 * Created by Noa Agiv on 12/11/2016.
 */

public class NameContract {

    public static class NameEntry implements BaseColumns {
        public static final String TABLE_NAMES_USERS = "names_users";
        public static final String TABLE_NAMES_USERS_NAME_ID = "name_id";
        public static final String TABLE_NAMES_USERS_USER_ID = "user_id";
        public static final String TABLE_NAMES_IS_LIKED = "is_liked";


        public static final String TABLE_NAMES = "names";
        public static final String TABLE_NAMES_COLUMN_NAME = "name";
    }

}
