package com.example.user.moviesmanager.data;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.example.user.moviesmanager.DbSearchActivity;
import com.example.user.moviesmanager.MoviesListActivity;
import com.example.user.moviesmanager.R;
import com.example.user.moviesmanager.SearchMoviesActivity;
import com.example.user.moviesmanager.utilities.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * This class content movie object
 *
 * Created by User on 09/12/2015.
 */
public class Movie implements Parcelable{


    //region Constants
    static final int MIN_MOVIE_RATE = 1;

    static final int MAX_MOVIE_RATE = 10;

    public static final int DEFAULT_MOVIE_RATE = 5;

    private static final int FIELDS_NUMBER = 7;

    private static final int TRUE_VALUE = -1;

    private static final int FALSE_VALUE = 0;

    public static final String NAME = Movie.class.getSimpleName();
    //endregion

    //region Instance variables
    private int id;

    private String subject;

    private String body;

    private String imageUrl;

    private boolean isWatched;

    private int rate;

    private String year;
    //endregion

    //region Constructors
    /**
     * Constructor
     *
     * @param subject title of movie (String)
     * @param body description of movie (String
     * @param url - url to movie poster in internet
     * @param isWatched
     * @param rate  1-10 in another case set DEFAULT_MOVIE_RATE = 5
     * @param year  - movie creation year
     * @throws MovieFormatException
     */
    public Movie(String subject,String body,String url,boolean isWatched,int rate,String year)
            throws MovieFormatException{

        this.id = Helper.generateUniqueID();
        setSubject(subject);
        setBody(body);
        setImageUrl(url);
        setWatched(isWatched);
        setRate(rate);
        setYear(year);
    }

    /**
     * Constructor
     *
     * @param id  identifier of movie
     * @param subject title of movie (String)
     * @param body description of movie (String
     * @param url - url to movie poster in internet or path to local storage image
     * @param isWatched
     * @param rate  1-10 in another case set DEFAULT_MOVIE_RATE = 5
     * @param year - movie creation year
     * @throws MovieFormatException
     */
    public Movie(int id,String subject,String body,String url,boolean isWatched,int rate,String year)
            throws MovieFormatException{

        setSubject(subject);
        setBody(body);
        setImageUrl(url);
        setWatched(isWatched);
        setRate(rate);
        setId(id);
        setYear(year);

    }

    public Movie(Parcel in) {
        readFromParcel(in);
    }

    //endregion

    //region Getters&Setters

    //region id getter&setter
    /**
     * This function return id of movie object
     * @return integer
     */
    public int getId(){
        return this.id;
    }

    /**
     * This function set movie object id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * This function get title of movie
     * @return
     */
    //endregion

    //region subject getter&setter
    /**
     * This function get subject of movie
     * @return
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * This function set movie subject
     * @param subject
     */
    public void setSubject(String subject)
            throws MovieFormatException{

        if(subject.equals(Constants.EMPTY_STRING)) {

            throw new MovieFormatException(DataConstants.EMPTY_SUBJECT_MESSAGE);
        }
        this.subject = subject;

    }
    //endregion

    //region body getter&setter
    /**
     * This function return description of movie
     * @return
     */
    public String getBody() {
        return this.body;
    }

    /**
     * This function set movie description
     * @param body
     */
    public void setBody(String body){
        if(body != null){
            this.body = body;
        }
    }
    //endregion

    //region imageUrl getter&setter
    /**
     * This function return url of movie
     * @return
     */
    public String getImageUrl() {
        return this.imageUrl;
    }

    /**
     * Set poster url to movie object
     * @param url
     */
    public void setImageUrl(String url){
        this.imageUrl = url;
    }
    //endregion

    //region isWatched getter&setter
    /**
     * This function return if movie is watched
     * @return
     */
    public boolean isWatched() {
        return this.isWatched;
    }

    /**
     * This function set if movie is watched
     * @param isWatched
     */
    public void setWatched(boolean isWatched){
        this.isWatched = isWatched;
    }
    //endregion

    //region rate getter&setter
    /**
     * This function return movies rate
     * @return
     */
    public int getRate() {
        return this.rate;
    }

    /**
     * This function set movies rate
     * from one to ten
     * @param rate
     */
    public void setRate(int rate) {

        if(rate < MIN_MOVIE_RATE || rate > MAX_MOVIE_RATE) {
            this.rate = DEFAULT_MOVIE_RATE;
            return;
        }
        this.rate = rate;
    }
    //endregion
    //region year getters&setters
    /**
     * This function get year of movie
     * @return String
     */
    public String getYear(){
        return year;
    }

    /**
     * This function set year of movie
     * @param year
     */
    public void setYear(String year){
        this.year = year;
    }
    //endregion

    //region OVERRIDE FUNCTION
    @Override
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("{ subject : ");
        buffer.append(this.getSubject());
        buffer.append(",\n");
        buffer.append("     body : ");
        buffer.append(this.getBody());
        buffer.append(",\n");
        buffer.append("     year : ");
        buffer.append(this.getYear());
        buffer.append(" }");
        buffer.append(",\n");
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Movie)){
            return false;
        }
        Movie movie = (Movie)o;
        return this.toString().equals(movie.toString());
    }
    //endregion
    //region Parcelable part
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return FIELDS_NUMBER;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.subject);
        dest.writeString(this.body);
        dest.writeString(this.imageUrl);
        dest.writeInt(isWatched ? TRUE_VALUE : FALSE_VALUE);
        dest.writeInt(this.rate);
        dest.writeString(this.year);
    }

    private void readFromParcel(Parcel in){

        id = in.readInt();
        subject = in.readString();
        body = in.readString();
        imageUrl = in.readString();
        int isWatchedInt = in.readInt();
        isWatched = isWatchedInt == TRUE_VALUE ? true : false;
        rate = in.readInt();
        year = in.readString();
    }
    //endregion
    //region Helper
    public static class Helper {
        //region unique int id generator

        /**
         * This function generate unique identifier
         */
        private static int generateUniqueID() {

            String idStr = UUID.randomUUID().toString();
            int id = idStr.hashCode();
            id = Math.abs(id);
            return id;
        }
        //endregion

        //region VALIDATE YEAR
        public static boolean isValidYear(String year){

            boolean isValidYear = true;
            String date = year + "-03-10";
            isValidYear = isValidDate(date);
            return isValidYear;
        }
        private static boolean isValidDate(String inDate) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(inDate.trim());
            } catch (ParseException pe) {
                return false;
            }
            return true;
        }
        //endregion

    }
    //endregion
    //endregion


}
