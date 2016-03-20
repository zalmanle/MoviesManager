package com.example.user.moviesmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.user.moviesmanager.data.DataConstants;
import com.example.user.moviesmanager.data.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10/12/2015.
 */
public class MoviesTableHandler {

    //region CONSTANTS
    private static final String TAG = "22:MoviesTableHandler";
    //endregion

    //region INSTANCE VARIABLES
    private MoviesDBHelper dbHelper;

    private SQLiteDatabase database;

    private String clauseString;

    private String[] params;
    //endregion

    //region CONSTRUCTOR
    public MoviesTableHandler(Context context) {
        this.dbHelper = new MoviesDBHelper(context);
    }
    //endregion

    //region ADD MOVIE METHOD

    /**
     * This function add movie to data base
     * @param movie - movie object to add
     * @return true if add successful otherwise return false
     */
    public boolean addMovie(Movie movie) {

        //check if movie object exist
        if(movie == null) {
            return false;
        }

        database = dbHelper.getWritableDatabase();
        long result = 0;

        try {

            ContentValues values = prepareMovieToInsert(movie);
            result = database.insertOrThrow(MoviesDBConstants.TABLE_NAME, null, values);

            return (result == MoviesDBConstants.INSERT_FAILED ? false : true) ;
        }
        catch (SQLException e){
           Log.d(TAG,e.getMessage());
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        finally {
            database.close();
        }

        return false;
    }
    //endregion

    //region EDIT MOVIE METHOD

    /**
     * This function edit movie object in database
     * @param movie movie in new data to insert
     * @return true if movie updated successfully
     *         otherwise false
     */
    public boolean editMovie(Movie movie){

        if(movie == null){
            return false;
        }
        //get database object from helper
        database = dbHelper.getWritableDatabase();
        int id = movie.getId();
        int resultCount;
        try {
            ContentValues values = prepareMovieToUpdate(movie);
            clauseString = MoviesDBConstants.ID + "=?";
            params = new String[] {String.valueOf(id)};
            resultCount = database.update(MoviesDBConstants.TABLE_NAME, values, clauseString, params);

            return resultCount == MoviesDBConstants.UPDATE_SUCCESS_CODE ? true : false;
        }
        catch (SQLiteException e) {
            Log.d(TAG,e.getMessage());
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        finally {
            database.close();
        }

        return false;
    }
    //endregion

    //region GET MOVIE BY SUBJECT FRAGMENT
    public List<Movie> getMovieBySubjectFragment(String subject) {

        database = dbHelper.getReadableDatabase();

        List<Movie>list = null;
        try {
            if (subject.length() != 0) {

                subject = "%" + subject + "%";
            }
            String selectQuery = " select * from " + MoviesDBConstants.TABLE_NAME +
                    " where " + MoviesDBConstants.SUBJECT + " like  '"
                    + subject
                    + "'";
            //execute query
            Cursor cursor = database.rawQuery(selectQuery, null);
            //get data and convert it
            list = getCursorData(cursor);
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        finally {
            database.close();
        }
        return list;
    }
    //endregion
    //region GET MOVIE BY SUBJECT
    public List<Movie> getMovieBySubject(String subject) {

        database = dbHelper.getReadableDatabase();
        List<Movie>list = null;
        try {
            clauseString = MoviesDBConstants.SUBJECT + "=?";
            params = new String[] {subject};
            //execute query
            Cursor cursor = database.query(MoviesDBConstants.TABLE_NAME,null,clauseString,params,null,null, DataConstants.SUBJECT);
            //get data and convert it
            list = getCursorData(cursor);
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        finally {
            database.close();
        }
        return list;
    }
    //endregion
    //region GET MOVIE BY SUBJECT
    public List<Movie> getMovieByYear(String year) {

        database = dbHelper.getReadableDatabase();
        List<Movie>list = null;
        try {
            clauseString = MoviesDBConstants.DATE + "=?";
            params = new String[] {year};
            //execute query
            Cursor cursor = database.query(MoviesDBConstants.TABLE_NAME,null,clauseString,params,null,null, DataConstants.SUBJECT);
            //get data and convert it
            list = getCursorData(cursor);
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        finally {
            database.close();
        }
        return list;
    }
    //endregion
    //region DELETE MOVIE METHOD

    /**
     *  This method delete current movie from data base
     * @param movie - movie to delete
     * @return  true id deleting execute successful
     */
    public boolean deleteMovie(Movie movie) {

        //check if movie object exist
        if(movie == null) {
            return false;
        }

        database = dbHelper.getWritableDatabase();
        int id = movie.getId();
        int resultCount;
        try {
            clauseString = MoviesDBConstants.ID + "=?";
            params = new String[] {String.valueOf(id)};
            resultCount = database.delete(MoviesDBConstants.TABLE_NAME, clauseString, params);

            return resultCount == MoviesDBConstants.DELETE_SUCCESS_CODE ? true:false;

        }
        catch (SQLiteException e) {
           Log.d(TAG,e.getMessage());
        }
        catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
        finally {
            database.close();
        }
        return false;
    }
    //endregion

    //region DELETE MOVIES LIST
    public void deleteMoviesList(List<Movie>list){
        if((list != null)&&(!list.isEmpty())){
            for(Movie movie:list){
                deleteMovie(movie);
            }
        }
    }
    //endregion

    //region DELETE MOVIES LIST
    public void addMoviesList(List<Movie>list){
        if((list != null)&&(!list.isEmpty())){
            for(Movie movie:list){
                addMovie(movie);
            }
        }
    }
    //endregion
    //region DELETE ALL MOVIES METHOD

    /**
     * This function remove all movies from database
     * @return
     */
    public boolean deleteAllMovies() {


        database = dbHelper.getWritableDatabase();

        try {

            database.delete(MoviesDBConstants.TABLE_NAME,null,null);
            return true;

        }
        catch (SQLiteException e) {
            Log.d(TAG,e.getMessage());
        }
        catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
        finally {
            database.close();
        }
        return false;
    }
    //endregion

    //region GET ALL MOVIES
    public List<Movie> getAllMovies() {

        database = dbHelper.getReadableDatabase();
        List<Movie>list = null;
        try {
            //execute query
            Cursor cursor = database.query(MoviesDBConstants.TABLE_NAME,null,null,null,null,null,null);
            //get data and convert it
            list = getCursorData(cursor);
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        finally {
           database.close();
        }
        return list;
    }
    //endregion

    //region SERVICE FUNCTIONS
    //region PREPARE NEW MOVIE TO INSERT
    private static ContentValues prepareMovieToInsert(Movie movie) {

        //create values object
        ContentValues values = new ContentValues();

        //insert data
        values.put(MoviesDBConstants.ID,movie.getId());
        values.put(MoviesDBConstants.SUBJECT,movie.getSubject());
        values.put(MoviesDBConstants.BODY,movie.getBody());
        values.put(MoviesDBConstants.IMAGE_URL,movie.getImageUrl());
        values.put(MoviesDBConstants.RATE,movie.getRate());

        //if is watched true assign 1 otherwise 0
        int watched = (movie.isWatched() == true ? 1:0);
        values.put(MoviesDBConstants.WATCHED,watched);
        values.put(MoviesDBConstants.DATE,movie.getYear());
        return values;
    }
    //endregion

    //region PREPARE MOVIE TO UPDATE
    private static ContentValues prepareMovieToUpdate(Movie movie) {

        //create values object
        ContentValues values = new ContentValues();

        //insert data
        values.put(MoviesDBConstants.SUBJECT,movie.getSubject());
        values.put(MoviesDBConstants.BODY,movie.getBody());
        values.put(MoviesDBConstants.IMAGE_URL,movie.getImageUrl());
        values.put(MoviesDBConstants.RATE,movie.getRate());

        //if is watched true assign 1 otherwise 0
        int watched = (movie.isWatched() == true ? 1:0);
        values.put(MoviesDBConstants.WATCHED,watched);
        values.put(MoviesDBConstants.DATE,movie.getYear());

        return values;
    }
    //endregion

    //region PACKAGE DATA TO RETURN

    /*
     * This function convert data
     * from cursor object to list of movies
     *
     * @param cursor - cursor to convert
     * @return List<Movie> ,if data not exist return null
     */
    public static List<Movie>getCursorData(Cursor cursor) {

        //check if cursor not equals null
        if(cursor == null) {
            return null;
        }

        List<Movie>list = new ArrayList<Movie>();
        int id = 0;
        String subject = null;
        String body = null;
        String imageUrl = null;
        int rate = 0;
        boolean watched;
        String date;
        int temp = 0;

        if((!cursor.isClosed())&&(cursor.moveToFirst())){
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                id = cursor.getInt(cursor.getColumnIndex(MoviesDBConstants.ID));
                subject = cursor.getString(cursor.getColumnIndex(MoviesDBConstants.SUBJECT));
                body = cursor.getString(cursor.getColumnIndex(MoviesDBConstants.BODY));
                imageUrl = cursor.getString(cursor.getColumnIndex(MoviesDBConstants.IMAGE_URL));
                rate = cursor.getInt(cursor.getColumnIndex(MoviesDBConstants.RATE));
                temp = cursor.getInt(cursor.getColumnIndex(MoviesDBConstants.WATCHED));
                watched = (temp == 1 ? true : false);
                date = cursor.getString(cursor.getColumnIndex(MoviesDBConstants.DATE));
                Movie movie = new Movie(id,subject,body,imageUrl,watched,rate,date);
                list.add(movie);

            }
        }

        cursor.close();

        return list;
    }
    //endregion
    //region IS MOVIE EXIST FUNCTION
    public boolean isMovieExist(Movie movie) {
        List<Movie>testList = getMovieBySubject(movie.getSubject());
        if(testList == null){
            return false;
        }
        else {
            for(int i = 0;i < testList.size();i++){
                if(movie.equals(testList.get(i))){
                    return true;
                }
            }
            return false;
        }
    }
    //endregion
    //endregion


}
