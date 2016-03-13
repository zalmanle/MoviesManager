package com.example.user.moviesmanager.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by User on 10/12/2015.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {

    //region CONSTANTS
    static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "movies_data_base";

    private static final String TAG = "22:MoviesDBHelper";
    //endregion

    //region CONSTRUCTOR
    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //endregion

    //region OVERRIDING FUNCTIONS
    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = getCreateMoviesTableQuery();
        try {
            db.execSQL(query);
        }
        catch (SQLException e){
            Log.d(TAG,e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // simple database upgrade operation:
        // 1) drop the old table
        try {
            db.execSQL("DROP TABLE IF EXISTS " + MoviesDBConstants.TABLE_NAME);
        }
        catch (SQLException e) {
            Log.d(TAG,e.getMessage());
        }

        // 2) create a new database
        onCreate(db);
    }
    //endregion

    //region FUNCTIONS
    /*
     * This function get create movie table sql query
     */
    private String getCreateMoviesTableQuery() {

        String query = "CREATE TABLE " + MoviesDBConstants.TABLE_NAME + "("
                + MoviesDBConstants.ID + " INTEGER PRIMARY KEY,"
                + MoviesDBConstants.SUBJECT + " TEXT NOT NULL,"
                + MoviesDBConstants.BODY + " TEXT NOT NULL,"
                + MoviesDBConstants.IMAGE_URL + " TEXT NOT NULL,"
                + MoviesDBConstants.RATE + " INTEGER NOT NULL,"
                + MoviesDBConstants.DATE + " TEXT NOT NULL,"
                + MoviesDBConstants.WATCHED + " INTEGER NOT NULL" + ")";

        return query;
    }
    //endregion
}
