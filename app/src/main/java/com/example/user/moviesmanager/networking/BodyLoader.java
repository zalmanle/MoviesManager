package com.example.user.moviesmanager.networking;

import android.util.Log;

import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.utilities.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by User on 14/12/2015.
 */
public class BodyLoader extends Loader {

    //region CONSTANTS
    private static final String PLOT_KEY = "Plot";

    private static final String TAG = "->BODY LOADER CLASS";
    //endregion

    //region INSTANCE VARIABLES
    private Movie movie;
    //endregion

    //region CONSTRUCTOR
    /**
     * Constructor
     * @param movie
     */
    public BodyLoader(Movie movie){

        this.movie = movie;

    }
    //endregion

    //region MAIN FUNCTION
    /**
     * This function return movie object with it's plot
     * @return
     */
    public Movie getMovieWithDescription() {

        //check if search phrase not null or not empty string
        if(this.movie == null) {
            return null;
        }
        //build search query
        buildSearchQuery();
        try {

            this.resultStr = sendHTTPRequest();
            System.out.println(this.resultStr);
            parseData();
            return movie;

        }
        catch(IOException e) {
            Log.d(TAG,Constants.ERROR_MESSAGE, e);
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

        this.searchPhrase = this.movie.getSubject();
        this.searchPhrase = this.searchPhrase.replace(WHITESPACE,SEPARATOR);
        url = SERVICE_URL + "?t=" + this.searchPhrase;
    }
    //endregion

    //region PARSE DATA FUNCTION
    @Override
    protected void parseData()
            throws JSONException{

        JSONObject object = new JSONObject(this.resultStr);
        String body = object.getString(PLOT_KEY);
        this.movie.setBody(body);
    }
    //endregion

    //endregion
}
