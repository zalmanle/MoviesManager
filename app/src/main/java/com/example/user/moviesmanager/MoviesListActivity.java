package com.example.user.moviesmanager;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.user.moviesmanager.customadapter.MoviesAdapter;
import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.db.MoviesContract;
import com.example.user.moviesmanager.db.MoviesTableHandler;
import com.example.user.moviesmanager.info.InfoFactory;
import com.example.user.moviesmanager.info.MoviesUserInfo;
import com.example.user.moviesmanager.utilities.Constants;
import com.example.user.moviesmanager.utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

public class MoviesListActivity extends AppCompatActivity
        implements View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor> {


    //region Constants
    private static final int LOADER_ID = 0;
    private static final String MOVIES_LIST_KEY = "movies list";
    //endregion
    //region INSTANCE VARIABLES
    private FloatingActionButton showOptionsBtn;

    private FloatingActionButton manuallyAddBtn;

    private FloatingActionButton onlineAddBtn;

    private ListView moviesListView;

    private List<Movie> moviesList;

    private List<Movie> garbageMoviesList;

    private MoviesTableHandler handler;

    private MoviesAdapter adapter;

    private MenuItem restoreMenuItem;

    private MoviesUserInfo info;

    private ImageView shareImageContainer;

    private int selectedPosition;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);
        //set ui elements
        setUIElements();

        info = (MoviesUserInfo) InfoFactory.getInfo(InfoFactory.MOVIES_USER_INFO,this);
        //initialize garb age movie list
        garbageMoviesList = new ArrayList<Movie>();
        //initialize db handler
        handler = new MoviesTableHandler(this);
        //initMoviesList();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String result = intent.getStringExtra(Constants.MOVIE_LOADED_MESSAGE);
        if ((result != null) && (result.equals(Constants.MOVIE_LOADED_MESSAGE))) {
            //initMoviesList();
            getLoaderManager().getLoader(LOADER_ID).forceLoad();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(MOVIES_LIST_KEY, (ArrayList<? extends Parcelable>) moviesList);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        moviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST_KEY);
        initAdapter();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.show_options_button:
                swapButtons();
                break;
            case R.id.manually_add_button:
                disappearAddButtons();
                goToStoreMovieActivity();

                break;
            case R.id.online_add_button:
                disappearAddButtons();
                goToSearchMoviesActivity();
                break;

        }
    }

    private void goToSearchMoviesActivity() {
        Intent intent;
        intent = new Intent(this, SearchMoviesActivity.class);
        startActivityForResult(intent, Constants.SEARCH_MOVIE_REQUEST_CODE);
    }

    private void goToStoreMovieActivity() {
        Intent intent;
        intent = new Intent(this, StoreMovieActivity.class);
        startActivityForResult(intent, Constants.STORE_MOVIE_REQUEST_CODE);
    }

    private void disappearAddButtons() {
        manuallyAddBtn.setVisibility(View.GONE);
        onlineAddBtn.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movieslistmenu, menu);
        restoreMenuItem = menu.findItem(R.id.restore_movies_item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean selectedResult = super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.remove_all_item:
                if (moviesList != null) {
                    if (moviesList.size() > Constants.MOVIES_EMPTY_NUMBER) {

                        info.showDeleteAllItemsWarning(deleteAllMoviesListener);
                    } else {
                        info.displayInfoMessage(getString(R.string.empty_movies_storage_message));
                    }

                }
                break;
            case R.id.restore_movies_item:
                disappearRestoreMenuItem();
                restoreMovies();
                break;
            case R.id.settings_item:
                goToSettingsScreen();
                break;
            case R.id.db_search_item:
                goToDBSearchScreen();
                break;
            case R.id.exit_item:
                finish();
                break;
        }
        return selectedResult;
    }

    private void goToDBSearchScreen() {

        Intent intent = new Intent(this, DbSearchActivity.class);
        startActivityForResult(intent, Constants.DB_SEARCH_REQUEST_CODE);
    }

    private void goToSettingsScreen() {
        Intent intent;
        intent = new Intent(this, AppPrefsActivity.class);
        startActivityForResult(intent, Constants.PREFERENCES_REQUEST_CODE);
    }

    private void disappearRestoreMenuItem() {
        restoreMenuItem.setVisible(false);
        restoreMenuItem.setEnabled(false);
        restoreMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    private void restoreMovies() {
        if (garbageMoviesList != null) {

            moviesList.addAll(garbageMoviesList);
            handler.addMoviesList(garbageMoviesList);
            initMoviesList();
            info.displayInfoMessage(getString(R.string.movies_back_message));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Constants.PREFERENCES_REQUEST_CODE:
            case Constants.SEARCH_MOVIE_REQUEST_CODE:
            case Constants.STORE_MOVIE_REQUEST_CODE:
            case Constants.DB_SEARCH_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    //initMoviesList();
                    getLoaderManager().getLoader(LOADER_ID).forceLoad();
                }
                break;

        }
    }

    //region SET VIEW ELEMENTS
    private void setUIElements() {
        //set floating buttons
        setShowFloatingActionButtons();

        //set movies list view
        setMoviesListView();

    }

    //region SET FLOATING BUTTONS
    private void setShowFloatingActionButtons() {

        //set show options button
        showOptionsBtn = (FloatingActionButton) findViewById(R.id.show_options_button);
        showOptionsBtn.setOnClickListener(this);

        //set manually addition button
        manuallyAddBtn = (FloatingActionButton) findViewById(R.id.manually_add_button);
        manuallyAddBtn.setOnClickListener(this);
        manuallyAddBtn.setVisibility(View.GONE);

        //set manually addition button
        onlineAddBtn = (FloatingActionButton) findViewById(R.id.online_add_button);
        onlineAddBtn.setOnClickListener(this);
        onlineAddBtn.setVisibility(View.GONE);

    }
    //endregion

    //region SET MOVIES LIST VIEW
    private void setMoviesListView() {
        moviesListView = (ListView) findViewById(R.id.main_movies_list_view);
        moviesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie movie = moviesList.get(position);
                goToStoreMovieActivity(movie);

            }
        });

        moviesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                selectedPosition = position;
                if (view instanceof LinearLayout) {

                    shareImageContainer = (ImageView) view.findViewById(R.id.movies_list_item_image);
                }
                info.showMovieOptionDialog(optionsListener);
                return true;
            }
        });
    }

    private void goToStoreMovieActivity(Movie movie) {
        Intent intent = Utilities.Helper.getIntentFromMovie02(movie, MoviesListActivity.this);
        intent.putExtra(Constants.BASE_PAGE_CODE, Constants.MOVIES_LIST_PAGE);
        startActivityForResult(intent, Constants.STORE_MOVIE_REQUEST_CODE);
    }
    //endregion

    //endregion

    //region SERVICE FUNCTIONS


    //region SHOW ALERT DIALOG ON LIST ITEM CLICKED
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
                        Utilities.SystemHelper.shareImageFromImageView(MoviesListActivity.this,shareImageContainer);
                    } catch (Exception e) {
                        info.displayWarningMessage(getString(R.string.share_image_error_message));
                    }
                    break;
                case Constants.EDIT_MOVIE_CODE:
                    movie = moviesList.get(selectedPosition);
                    goToStoreMovieActivity(movie);
                    break;
                case Constants.DELETE_MOVIE_CODE:
                    info.showDeleteItemWarning(deleteMovieListener);
                    break;
            }
        }
    };
    //endregion

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
        }
    };

    private void deleteMovie(int position) {
        Movie movie = moviesList.get(position);
        handler.deleteMovie(movie);
        moviesList.remove(position);
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

    //region SHOW ALERT DIALOG TO REMOVE ALL ITEMS
    private void showDeleteAllItemsWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.warning_title_text);
        builder.setIcon(R.drawable.red_warning_icon);
        builder.setMessage(R.string.delete_all_movies_warning_message);
        builder.setPositiveButton(R.string.positive_button_text,deleteAllMoviesListener);
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

        }
    };

    private void deleteMovies() {
        garbageMoviesList.addAll(moviesList);
        moviesList.clear();
        handler.deleteAllMovies();
        adapter.notifyDataSetChanged();
        info.displayInfoMessage(getString(R.string.all_movies_deleted_message));
    }
    //endregion

    //region INIT MOVIES LIST
    private void initMoviesList() {


        moviesList = handler.getAllMovies();

        if ((moviesList != null)) {
            Movie.Helper.setMoviesOrder(moviesList, this);
            adapter = new MoviesAdapter(this, R.layout.movies_list_item, moviesList);
            moviesListView.setAdapter(adapter);
        }
    }
    //endregion

    //region SWAP BUTTONS FUNCTION
    private void swapButtons() {

        int onlineBtnVisibility = onlineAddBtn.getVisibility();
        int manuallyBtnVisibility = manuallyAddBtn.getVisibility();
        //swap online button visibility
        if (onlineBtnVisibility == View.VISIBLE) {
            onlineAddBtn.setVisibility(View.GONE);
        } else {
            onlineAddBtn.setVisibility(View.VISIBLE);
        }
        //swap manually add button
        if (manuallyBtnVisibility == View.VISIBLE) {
            manuallyAddBtn.setVisibility(View.GONE);
        } else {
            manuallyAddBtn.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri CONTENT_URI = MoviesContract.CONTENT_URI;
        return new CursorLoader(this, CONTENT_URI, null,null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        moviesList = MoviesTableHandler.getCursorData(data);


        if ((moviesList != null)) {
            initAdapter();
        }
    }

    private void initAdapter() {
        Movie.Helper.setMoviesOrder(moviesList, this);
        adapter = new MoviesAdapter(this, R.layout.movies_list_item, moviesList);
        moviesListView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    //endregion

    //endregion
}
