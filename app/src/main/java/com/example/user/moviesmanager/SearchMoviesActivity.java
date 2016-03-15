package com.example.user.moviesmanager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user.moviesmanager.customadapter.MoviesAdapter;
import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.info.UserInfo;
import com.example.user.moviesmanager.tasks.LoadMoviesTask;
import com.example.user.moviesmanager.tasks.OnDataReceivedListener;
import com.example.user.moviesmanager.utilities.Constants;
import com.example.user.moviesmanager.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

public class SearchMoviesActivity extends AppCompatActivity
        implements View.OnClickListener,OnDataReceivedListener{

    private static final int OFFSET = 1;

    private static final String SEARCH_STRING_KEY = "search_string";

    private static final String MOVIES_LIST_KEY = "movies list";
    //region INSTANCE VARIABLES
    private EditText searchEditText;

    private ListView resultsListView;

    private ProgressBar loadMoviesBar;

    private MoviesAdapter adapter;

    private List<Movie>resultsList;

    private String searchString;

    private Movie selectedMovie;

    private  InputMethodManager imm;

    private UserInfo info;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_movies);
        //set ui elements
        setUIElements();
        info = new UserInfo(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(MOVIES_LIST_KEY, (ArrayList<? extends Parcelable>)resultsList);
        savedInstanceState.putString(SEARCH_STRING_KEY, searchString);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<Movie>list = savedInstanceState.getParcelableArrayList(MOVIES_LIST_KEY);
        updateResultsList(list);
        searchString = savedInstanceState.getString(SEARCH_STRING_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result =  super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_movies_activity_menu,menu);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.search_settings_item:
                goToPreferencesScreen();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToPreferencesScreen() {
        Intent intent = new Intent(this,AppPrefsActivity.class);
        startActivityForResult(intent,Constants.PREFERENCES_REQUEST_CODE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.search_settings_item);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public void onDataReceived(List<Movie> movies) {

        if((resultsList != null)&&(movies != null)){
            if(movies.size() > Constants.MOVIES_EMPTY_NUMBER){

                updateResultsList(movies);

            }
            else {
                info.displayInfoMessage(getString(R.string.not_movie_found_message));
            }
        }
        else {
             info.displayInfoMessage(getString(R.string.not_movie_found_message));

        }
    }

    private void updateResultsList(List<Movie> movies) {
        resultsList.clear();
        resultsList.addAll(movies);
        Movie.Helper.setMoviesOrder(resultsList, this);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case Constants.PREFERENCES_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    setResultsListView();
                    if(!searchString.equals(Constants.EMPTY_STRING)) {
                        new LoadMoviesTask(loadMoviesBar, SearchMoviesActivity.this).execute(searchString.trim());
                    }
                }
                break;
            case Constants.STORE_MOVIE_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    setResult(RESULT_OK);
                    //finish();
                }
                else {
                    //finish();
                }
                break;
        }
    }

    //region SET UI ELEMENTS
    private void setUIElements(){

        //set load movies progress bar
        loadMoviesBar = (ProgressBar) findViewById(R.id.load_movies_progress_bar);
        //set search text edit text
        setSearchEditText();
        //set search results list
        setResultsListView();

        //set back button
        ActionBar bar = getSupportActionBar();
        bar.setLogo(R.drawable.back_icon);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    //region SET RESULTS LIST VIEW
    private void setResultsListView(){
        resultsListView = (ListView)findViewById(R.id.search_results_list);
        resultsList = new ArrayList<Movie>();
        adapter = new MoviesAdapter(this,R.layout.movies_list_item,resultsList);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMovie = resultsList.get(position);
                goToStoreMovieActivity();
            }
        });
    }

    private void goToStoreMovieActivity() {
        Intent intent = Utilities.Helper.getIntentFromMovie02(selectedMovie, SearchMoviesActivity.this);
        intent.putExtra(Constants.BASE_PAGE_CODE, Constants.SEARCH_MOVIES_PAGE);
        startActivityForResult(intent, Constants.STORE_MOVIE_REQUEST_CODE);
    }

    //endregion
    //region SET SEARCH STRING EDIT TEXT
    private void setSearchEditText(){
        searchEditText = (EditText) findViewById(R.id.search_movies_edit_text);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
                }
                else {

                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }
            }
        });

        searchEditText.setOnClickListener(this);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchString = searchEditText.getText().toString();
                    if (searchString.equals(Constants.EMPTY_STRING)) {
                        info.displayWarningMessage(getString(R.string.empty_search_string_message));
                        return true;
                    }
                    Utilities.UI.hide_keyboard(SearchMoviesActivity.this);
                    new LoadMoviesTask(loadMoviesBar, SearchMoviesActivity.this).execute(searchString);


                    return true;
                }
                return false;
            }
        });
    }
    //endregion


    //endregion
}
