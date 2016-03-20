package com.example.user.moviesmanager.tasks;

import android.os.AsyncTask;
import android.widget.ProgressBar;

/**
 * Created by User on 20/03/2016.
 */
public class TasksFactory {

    //region Constants
    public static final String SUBJECT_TASK = "subject";

    public static final String MOVIES_TASK = "movies";
    //endregion

    public static AsyncTask getTask(String flag,ProgressBar bar,OnDataReceivedListener listener){
        AsyncTask asyncTask = null;
        if(flag.equals(SUBJECT_TASK)){
            asyncTask = new LoadMovieBodyTask(bar,listener);
        }
        else if(flag.equals(MOVIES_TASK)){
            asyncTask = new LoadMoviesTask(bar,listener);
        }
        return asyncTask;

    }

}
