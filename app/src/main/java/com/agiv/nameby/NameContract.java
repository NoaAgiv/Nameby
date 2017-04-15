package com.agiv.nameby;

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


        public static final String TABLE_NAMES = "untaggedNames";
        public static final String TABLE_NAMES_NAME = "name";
        public static final String TABLE_NAMES_INSERTED_BY = "inserted_by";
        public static final String TABLE_NAMES_DATE_INSERTED = "data_inserted";
        public static final String TABLE_NAMES_SEX = "sex";
        public static final String TABLE_NAMES_POPULARITY = "popularity";
    }

}
