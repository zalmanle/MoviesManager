package com.example.user.moviesmanager.db;

import android.net.Uri;

/**
 * Created by User on 18/02/2016.
 */
public class MoviesContract {

        private MoviesContract() { }


        private static final int DATABASE_VERSION = MoviesDBHelper.DATABASE_VERSION;
        private static final String DATABASE_NAME = MoviesDBHelper.DATABASE_NAME;

        public static final String AUTHORITY = "com.example.user.moviesmanager.provider.Movies";
        public static final Uri CONTENT_URI = Uri.parse(
                "content://" + AUTHORITY + "/movies");



        public static final String CONTENT_TYPE_MOVIES_LIST = "vnd.android.cursor.dir/vnd.com.movies";
        public static final String CONTENT_TYPE_MOVIES_ONE = "vnd.android.cursor.item/vnd.pete.movies";


    public class Table {
        //region TABLE NAME
        public static final String TABLE_NAME = MoviesDBConstants.TABLE_NAME;
        //endregion
        //region TABLE COLUMNS NAMES
        public static final String ID = MoviesDBConstants.ID;

        public static final String SUBJECT = MoviesDBConstants.SUBJECT;

        public static final String BODY = MoviesDBConstants.BODY;

        public static final String IMAGE_URL = MoviesDBConstants.IMAGE_URL;

        public static final String WATCHED = MoviesDBConstants.WATCHED;

        public static final String RATE = MoviesDBConstants.RATE;

        public static final String DATE = MoviesDBConstants.DATE;
        //endregion
    }

}
