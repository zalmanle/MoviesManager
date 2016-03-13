package com.example.user.moviesmanager.tasks;

import com.example.user.moviesmanager.data.Movie;

import java.util.List;

/**
 * Created by User on 20/12/2015.
 */
public interface OnDataReceivedListener {

    void onDataReceived(List<Movie> movies);
}
