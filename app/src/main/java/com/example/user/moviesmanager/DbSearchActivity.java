package com.example.user.moviesmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.moviesmanager.customadapter.MoviesAdapter;
import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.db.MoviesTableHandler;
import com.example.user.moviesmanager.info.InfoFactory;
import com.example.user.moviesmanager.info.MoviesUserInfo;
import com.example.user.moviesmanager.utilities.Constants;
import com.example.user.moviesmanager.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

public class DbSearchActivity extends AppCompatActivity {

    //region Constants
    private static final int SUBJECT_SEARCH_OPTION = 0;

    private static final int YEAR_SEARCH_OPTION = 1;

    private static final int SUBJECT_FRAGMENT_SEARCH_OPTION = 2;

    private static final String DB_SEARCH_STRING_KEY = "db_search_string_key";

    private static final String MOVIES_LIST_KEY = "movies list";
    //endregion

    //region Instance Variables
    private Spinner dbOptionsSpinner;

    private ListView dbResultList;

    private EditText dbSearchEditText;

    private InputMethodManager imm;

    private String searchString;

    private int currentSearchOption;

    private List<Movie>results;

    private List<Movie>garbageMoviesList;

    private MoviesAdapter adapter;

    private Movie selectedMovie;

    private MoviesTableHandler handler;

    private MenuItem restoreMenuItem;

    private MoviesUserInfo info;

    private ImageView shareImageContainer;

    private int selectedPosition;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_search);
        handler = new MoviesTableHandler(this);
        info = (MoviesUserInfo) InfoFactory.getInfo(InfoFactory.MOVIES_USER_INFO,this);
        //initialize garb age movie list
        garbageMoviesList = new ArrayList<Movie>();
        initUIElements();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList(MOVIES_LIST_KEY, (ArrayList<? extends Parcelable>) results);
        savedInstanceState.putString(DB_SEARCH_STRING_KEY, searchString);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<Movie>list = savedInstanceState.getParcelableArrayList(MOVIES_LIST_KEY);
        updateResultsList(list);
        searchString = savedInstanceState.getString(DB_SEARCH_STRING_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result =  super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.db_search_movies_activity_menu, menu);
        restoreMenuItem = menu.findItem(R.id.db_restore_movies_item);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.db_search_settings_item:
                goToPreferencesScreen();
                break;
            case R.id.db_search_remove_all_movies_item:
                info.showDeleteAllItemsWarning(deleteAllMoviesListener);
                break;
            case R.id.db_restore_movies_item:
                disappearRestoreMenuItem();
                restoreMovies();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToPreferencesScreen() {
        Intent intent = new Intent(this,AppPrefsActivity.class);
        startActivityForResult(intent, Constants.PREFERENCES_REQUEST_CODE);
    }

    //region RESTORE FUNCTIONS
    private void disappearRestoreMenuItem() {
        restoreMenuItem.setVisible(false);
        restoreMenuItem.setEnabled(false);
        restoreMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    private void restoreMovies() {
        if(garbageMoviesList != null){
            results.addAll(garbageMoviesList);
            handler.addMoviesList(garbageMoviesList);
            Movie.Helper.setMoviesOrder(results, this);
            adapter.notifyDataSetChanged();
            setResult(RESULT_CANCELED);
            info.displayInfoMessage(getString(R.string.movies_back_message));
        }
    }
    //endregion


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.db_search_settings_item);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item= menu.findItem(R.id.db_search_remove_all_movies_item);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case Constants.PREFERENCES_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    updateListVisibility();
                    if(!TextUtils.isEmpty(searchString)) {
                        getDBData();
                    }
                }
                break;
            case Constants.STORE_MOVIE_REQUEST_CODE:

                if(resultCode == RESULT_OK) {

                    getDBData();
                    setResult(RESULT_OK);
                    //finish();
                }
                break;
        }
    }

    private void updateListVisibility() {
        adapter = new MoviesAdapter(this,R.layout.movies_list_item,results);
        dbResultList.setAdapter(adapter);
    }

    //region INIT UI ELEMENTS
    private void initUIElements() {
        initDBOptionsSpinner();
        initDBSearchEditText();
        initDBResultList();

        //set back button
        ActionBar bar = getSupportActionBar();
        bar.setLogo(R.drawable.back_icon);
        bar.setDisplayHomeAsUpEnabled(true);

    }

    private void initDBResultList() {
        dbResultList = (ListView)findViewById(R.id.db_search_results_list);
        results = new ArrayList<Movie>();
        adapter = new MoviesAdapter(this,R.layout.movies_list_item,results);
        dbResultList.setAdapter(adapter);
        dbResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMovie = results.get(position);
                goToEditMovieScreen();

            }
        });

        dbResultList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String customText = getString(R.string.custom_layout_tag_text);
                selectedPosition = position;
                if (view.getTag().equals(customText)) {

                    shareImageContainer = (ImageView) view.findViewById(R.id.movies_list_item_image);
                }
                info.showMovieOptionDialog(optionsListener);
                return true;
            }
        });
    }


    private void goToEditMovieScreen() {
        Intent intent = Utilities.Helper.getIntentFromMovie02(selectedMovie, DbSearchActivity.this);
        intent.putExtra(Constants.BASE_PAGE_CODE, Constants.DB_SEARCH_PAGE);
        startActivityForResult(intent, Constants.STORE_MOVIE_REQUEST_CODE);
    }

    private void initDBOptionsSpinner() {

        dbOptionsSpinner = (Spinner)findViewById(R.id.db_search_options_spinner);
        // Creating adapter for spinner
        final String[]options = getResources().getStringArray(R.array.db_search_options);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,options);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        dbOptionsSpinner.setAdapter(dataAdapter);
        dbOptionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case SUBJECT_SEARCH_OPTION:
                        currentSearchOption = SUBJECT_SEARCH_OPTION;
                        break;
                    case YEAR_SEARCH_OPTION:
                        currentSearchOption = YEAR_SEARCH_OPTION;
                        break;
                    case SUBJECT_FRAGMENT_SEARCH_OPTION:
                        currentSearchOption = SUBJECT_FRAGMENT_SEARCH_OPTION;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initDBSearchEditText() {
        dbSearchEditText = (EditText)findViewById(R.id.db_search_edit_text);
        currentSearchOption = SUBJECT_SEARCH_OPTION;
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        dbSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    imm.showSoftInput(dbSearchEditText, InputMethodManager.SHOW_IMPLICIT);
                } else {

                    imm.hideSoftInputFromWindow(dbSearchEditText.getWindowToken(), 0);
                }
            }
        });

        dbSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return getDBData();

                }
                return false;
            }
        });
    }
    //endregion

    //region GET DATA BASE DATA FUNCTION
    private boolean getDBData() {
        List<Movie>movies;
        searchString = dbSearchEditText.getText().toString();
        searchString = searchString.trim();
        if (searchString.equals(Constants.EMPTY_STRING)) {
            info.displayWarningMessage(getString( R.string.empty_search_string_message));
            return true;
        }
        Utilities.UI.hide_keyboard(DbSearchActivity.this);
        if (currentSearchOption == SUBJECT_SEARCH_OPTION) {
            movies = handler.getMovieBySubject(searchString);
            if ((movies == null) ||(movies.size() == Constants.MOVIES_EMPTY_NUMBER)) {
                info.displayInfoMessage(getString(R.string.not_movie_found_message));
                return true;
            }

            updateResultsList(movies);

        } else if (currentSearchOption == YEAR_SEARCH_OPTION) {
            if (Movie.Helper.isValidYear(searchString)) {
                movies = handler.getMovieByYear(searchString);
                if ((movies == null) ||(movies.size() == Constants.MOVIES_EMPTY_NUMBER)) {
                    info.displayInfoMessage(getString(R.string.not_movie_found_message));
                    return true;
                }

                updateResultsList(movies);

            } else {
                info.displayWarningMessage(getString(R.string.invalid_year_message));
            }

        } else if(currentSearchOption == SUBJECT_FRAGMENT_SEARCH_OPTION){
            movies = handler.getMovieBySubjectFragment(searchString);
            if ((movies == null) ||(movies.size() == Constants.MOVIES_EMPTY_NUMBER)) {
                info.displayWarningMessage(getString(R.string.not_movie_found_message));
                return true;
            }
            updateResultsList(movies);


        }
        return true;
    }

    /*
     * This function update results list
     */
    private void updateResultsList(List<Movie>movies) {
        results.clear();
        results.addAll(movies);
        Movie.Helper.setMoviesOrder(results, this);
        adapter.notifyDataSetChanged();
    }
    //endregion

    //region SHOW ALERT DIALOG TO REMOVE ALL ITEMS
    private void showDeleteAllItemsWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title_text);
        builder.setIcon(R.drawable.red_warning_icon);
        builder.setMessage(R.string.delete_all_movies_warning_message);
        builder.setPositiveButton(R.string.positive_button_text, deleteAllMoviesListener);
                builder.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    DialogInterface.OnClickListener deleteAllMoviesListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            deleteMovies();
            appearRestoreMenuItem();
            setResult(RESULT_OK);
        }
    };

    private void deleteMovies() {
        garbageMoviesList.addAll(results);
        handler.addMoviesList(results);
        results.clear();
        adapter.notifyDataSetChanged();
        info.displayInfoMessage(getString(R.string.movie_list_deleted_message));
    }
    //endregion

    //region SHOW ALERT DIALOG TO REMOVE ITEM
    private void showDeleteItemWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title_text);
        builder.setIcon(R.drawable.red_warning_icon);
        builder.setMessage(R.string.delete_one_movie_warning_message);
        builder.setPositiveButton(R.string.positive_button_text, deleteMovieListener);
                builder.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    DialogInterface.OnClickListener deleteMovieListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            deleteMovie(selectedPosition);
            appearRestoreMenuItem();
            setResult(RESULT_OK);
        }
    };

    private void deleteMovie(int position) {
        Movie movie = results.get(position);
        handler.deleteMovie(movie);
        results.remove(position);
        garbageMoviesList.add(movie);
        adapter.notifyDataSetChanged();
        info.displayInfoMessage(getString(R.string.movie_deleted_message));
    }

    private void appearRestoreMenuItem() {
        restoreMenuItem.setVisible(true);
        restoreMenuItem.setEnabled(true);
        restoreMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
    //endregion

    //region SHOW ALERT DIALOG ON LIST ITEM  LONG CLICKED
    private void showMovieOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.options_dialog_title);
        builder.setItems(R.array.movie_options_items,optionsListener);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    DialogInterface.OnClickListener optionsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            Movie movie;
            switch (which) {
                case Constants.SHARE_IMAGE_CODE:
                    try {
                        Utilities.SystemHelper.shareImageFromImageView(DbSearchActivity.this,shareImageContainer);
                    } catch (Exception e) {
                        info.displayWarningMessage(getString(R.string.share_image_error_message));
                    }
                    break;
                case Constants.EDIT_MOVIE_CODE:
                    selectedMovie = results.get(selectedPosition);
                    goToEditMovieScreen();
                    break;
                case Constants.DELETE_MOVIE_CODE:
                    info.showDeleteItemWarning(deleteMovieListener);
                    break;
            }
        }
    };
    //endregion

}
