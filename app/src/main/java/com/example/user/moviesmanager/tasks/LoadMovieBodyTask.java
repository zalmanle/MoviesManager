package com.example.user.moviesmanager.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.networking.BodyLoader;
import com.example.user.moviesmanager.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 17/12/2015.
 */
public class LoadMovieBodyTask  extends AsyncTask<Movie,Integer,Movie>{

    //region CONSTANTS
    private static final String TAG = "LoadMovieBody";
    //endregion

    //region INSTANCE VARIABLES
    private ProgressBar progressBar;

    private int counter;

    private BodyLoader loader;

    private Movie movie;

    private OnDataReceivedListener listener;

    private List<Movie>list;
    //endregion

    //region CONSTRUCTOR
    LoadMovieBodyTask(ProgressBar progressBar,OnDataReceivedListener listener){
        this.progressBar = progressBar;
        this.listener = listener;
    }
    //endregion


    @Override
    protected Movie doInBackground(Movie... params) {

        //check if parameter exist
        if(params == null) {
            return null;
        }
        movie = params[0];
        try {

            loader = new BodyLoader(movie);
            publishProgress(counter++);
            movie = loader.getMovieWithDescription();
            return movie;
        }
        catch (Exception e){
            Log.d(TAG, Constants.LOADING_ERROR_MESSAGE, e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie movie) {

        //hide progress bar
        if(progressBar!= null){
            progressBar.setVisibility(View.GONE);
        }
        //set data
        if(listener != null){
            list = new ArrayList<Movie>();
            list.add(movie);
            listener.onDataReceived(list);
        }

    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        //set progress
        if(progressBar != null){
            progressBar.setProgress(values[0]);
        }
    }

    @Override
    protected void onPreExecute() {

        //check if progress bar exist
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(25);
        }
        //initialize counter
        counter = 0;
    }
}
