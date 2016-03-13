package com.example.user.moviesmanager.networking;

import android.util.Log;

import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by User on 13/12/2015.
 */
public class MoviesLoader extends Loader {

    //region CONSTANTS
    private static final String JSON_ARRAY_NAME = "Search";

    private static final String SUBJECT_KEY_NAME = "Title";

    private static final String IMAGE_URL_KEY_NAME = "Poster";

    private static final String YEAR_KEY_NAME = "Year";

    private static final String TAG = "->MOVIES LOADER CLASS";

    private static final String ERROR_KEY_NAME = "Error";

    //endregion

    //region INSTANCE VARIABLES
    private List<Movie> list = null;
    //endregion

    //region CONSTRUCTOR
    /**
     * Constructor
     * @param searchPhrase
     */
    public MoviesLoader(String searchPhrase) {

        this.searchPhrase = searchPhrase;
    }
    //endregion

    //region MAIN FUNCTION
    /**
     * This function get list of movies from web
     * and return it as list of movies objects
     * @return list of movies
     */
    public List<Movie> getMoviesList()throws Exception{

        //check if search phrase not null or not empty string
        if((this.searchPhrase == null)||(this.searchPhrase.equals(Constants.EMPTY_STRING))) {
            return null;
        }
        //build search query
        buildSearchQuery();
        try {
            this.resultStr = sendHTTPRequest();
            parseData();
            return list;

        }
        catch(IOException e) {
            Log.d(TAG, Constants.ERROR_MESSAGE,e);
        }
        catch(JSONException e) {
            Log.d(TAG,Constants.ERROR_MESSAGE,e);
        }
        catch (Exception e) {
            Log.d(TAG,Constants.ERROR_MESSAGE,e);
        }

        return null;
    }
    //endregion

    //region SERVICE FUNCTIONS

    //region BUILD QUERY FUNCTION
    @Override
    protected void buildSearchQuery() {

        this.searchPhrase = this.searchPhrase.replace(WHITESPACE,SEPARATOR);
        url = SERVICE_URL + "?s=" + this.searchPhrase;
    }
    //endregion


    //region PARSE DATA FUNCTION
    @Override
    protected void parseData()
            throws JSONException {


        Log.d("TEST",this.resultStr);
        JSONObject object = new JSONObject(this.resultStr);
        this.list = new ArrayList<Movie>();

        if (!object.has(ERROR_KEY_NAME)) {
            JSONArray arr = object.getJSONArray(JSON_ARRAY_NAME);

            int id;
            String subject;
            String body = "";
            String posterUrl;
            String year;
            int rate = Constants.DEFAULT_MOVIE_RATE;
            boolean watched = Constants.DEFAULT_WATCHED_VALUE;

            for (int i = 0; i < arr.length(); i++) {

                JSONObject item = arr.getJSONObject(i);
                id = UUID.randomUUID().hashCode();
                subject = item.getString(SUBJECT_KEY_NAME);
                posterUrl = item.getString(IMAGE_URL_KEY_NAME);
                year = item.getString(YEAR_KEY_NAME);
                Movie movie = new Movie(id, subject, body, posterUrl, watched, rate,year);
                list.add(movie);
            }

        }
    }
    //endregion
    //endregion


}
