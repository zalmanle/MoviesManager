package com.example.user.moviesmanager.data;

/**
 * This exception check if movie data is valid
 * Created by User on 09/12/2015.
 */
public class MovieFormatException extends IllegalArgumentException {

    /**
     * Constructor
     * @param message - message with problem
     */
    public MovieFormatException(String message){
        super(message);
    }
}
