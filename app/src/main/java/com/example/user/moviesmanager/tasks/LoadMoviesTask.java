package com.example.user.moviesmanager.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.networking.MoviesLoader;
import com.example.user.moviesmanager.utilities.Constants;

import java.util.List;

/**
 * Created by User on 17/12/2015.
 */
public class LoadMoviesTask extends AsyncTask<String,Integer,List<Movie>> {


    //region CONSTANTS
    private static final String TAG = "LoadMovies";
    //endregion

    //region INSTANCE VARIABLES
    private ProgressBar progressBar;

    private int counter;

    private MoviesLoader loader;

    private List<Movie>list;

    private OnDataReceivedListener listener;
    //endregion

    //region CONSTRUCTOR
    LoadMoviesTask(ProgressBar progressBar,OnDataReceivedListener listener){
        this.progressBar = progressBar;
        this.listener = listener;
    }
    //endregion


    @Override
    protected List<Movie> doInBackground(String... params) {

        //check if parameter exist
        if(params == null) {
            return null;
        }
        String searchStr = params[0];
        try {
            loader = new MoviesLoader(searchStr);
            publishProgress(counter++);
            list = loader.getMoviesList();
        }
        catch (Exception e){
            Log.d(TAG, Constants.LOADING_ERROR_MESSAGE,e);
        }

        return list;
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {

        //hide progress bar
        if(progressBar!= null){
            progressBar.setVisibility(View.GONE);
        }
        //set data
        if(listener != null){
            listener.onDataReceived(movies);
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
