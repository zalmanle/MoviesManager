package com.example.user.moviesmanager;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.user.moviesmanager.data.DataConstants;
import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.db.MoviesTableHandler;
import com.example.user.moviesmanager.info.AdvancedOptionsUserInfo;
import com.example.user.moviesmanager.tasks.LoadMovieBodyTask;
import com.example.user.moviesmanager.tasks.OnDataReceivedListener;
import com.example.user.moviesmanager.utilities.Constants;
import com.example.user.moviesmanager.utilities.Utilities;

import java.util.List;

public class StoreMovieActivity extends AppCompatActivity
                                implements OnDataReceivedListener,
                                           TextWatcher {

    //region CONSTANTS
    private static final int ID_NO_EXIST_CODE = -1;

    private static final int MOVIE_POSITION = 0;

    private static final String TAG = "TEST";

    private static final int WATCHED_POSITION = 0;

    private static final int ADD_RATE_POSITION = 1;

    private static int RESULT_LOAD_IMAGE = 251;
    //endregion

    //region INSTANCE VARIABLES
    private EditText subjectEditText;

    private EditText bodyEditText;

    private EditText urlEditText;

    private ImageView posterImageView;

    private EditText yearEditText;

    private Movie movie;

    private ActionBar bar;

    private ProgressBar loadBodyPb;

    private String sender;

    private MoviesTableHandler handler;

    private boolean isNewAddition = true;

    private String subject;

    private String body;

    private String url;

    private String year;

    private boolean watched = false;

    private int movieRate = Constants.DEFAULT_MOVIE_RATE;

    private AdvancedOptionsUserInfo info;

    String[]rates;

    private boolean isStored;

    private boolean isChanged = false;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_movie);
        handler = new MoviesTableHandler(this);
        //initialize ui elements
        initUIElements();
        info = new AdvancedOptionsUserInfo(this);
        rates = getResources().getStringArray(R.array.rates);


    }

    @Override
    protected void onStart() {
        super.onStart();
        //initialize screen values
        initScreenValues();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.store_image_menu_item:
                //store image
                storeImage();
                break;
            case R.id.store_image_to_gallery_menu_item:
                if((movie!= null)&&(posterImageView.getDrawable() != null)){
                    Utilities.SystemHelper.storeImageToGallery(this,posterImageView,movie);
                }
                break;
            case R.id.upload_image_from_gallery_menu_item:
                goToGalleryApp();
                break;
            case R.id.share_image_menu_item:
                try {
                    Utilities.SystemHelper.shareImageFromImageView(StoreMovieActivity.this,posterImageView);
                }
                catch (Exception e){
                    info.displayWarningMessage(getString(R.string.share_image_error_message));
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void goToGalleryApp() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_storeItem:
                storeMovie();
                break;
            case R.id.advanced_store_option:
                info.showAdvancedOptionsDialog(optionsListener);
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void storeMovie() {

        if (prepareMovieFromFields()) {

            if (isNewAddition) {
                if (!handler.isMovieExist(movie)) {
                    storeMovie(true);
                } else {
                    info.displayInfoMessage(getString(R.string.movie_exist_message));
                }

            } else {
                if(isChanged) {
                    storeMovie(false);
                }
                else {
                    info.displayInfoMessage(getString(R.string.no_change_detected_message));
                    finish();
                }

            }
            goBackWithResult();
        }
    }

    private void storeMovie(boolean isNewAddition){
        isStored = false;
        if(isNewAddition){
            isStored = handler.addMovie(movie);
            if(isStored){
               info.displayInfoMessage(getString(R.string.add_movie_success_message));
            }
            else {
               info.displayInfoMessage(getString(R.string.add_movie_failed_message));
            }
        }
        else {
            isStored = handler.editMovie(movie);
            if(isStored){
               info.displayInfoMessage(getString(R.string.edit_movie_success_message));
            }
            else {
               info.displayInfoMessage(getString(R.string.edit_movie_failed_message));
            }
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.action_storeItem);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item = menu.findItem(R.id.advanced_store_option);
        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();

            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... params) {

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    posterImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    urlEditText.setText(picturePath);
                }
            }.execute();

        }
    }
    //region SERVICE FUNCTIONS
    //region INIT UI ELEMENTS
    private void initUIElements(){

        //initialize subject edit text
        subjectEditText = (EditText) findViewById(R.id.subject_edit_text);
        subjectEditText.addTextChangedListener(this);
        //initialize body edit text
        bodyEditText = (EditText) findViewById(R.id.body_edit_text);
        bodyEditText.addTextChangedListener(this);
        //initialize url edit text
        urlEditText = (EditText) findViewById(R.id.url_edit_text);
        urlEditText.addTextChangedListener(this);
        //initialize year edit text
        yearEditText = (EditText)findViewById(R.id.year_edit_text);
        yearEditText.addTextChangedListener(this);
        //initialize image view
        posterImageView = (ImageView)findViewById(R.id.movie_poster_image_view);
        registerForContextMenu(posterImageView);
        //initialize load body progress bar
        loadBodyPb = (ProgressBar)findViewById(R.id.load_body_pb);

        //set back button
        ActionBar bar = getSupportActionBar();
        bar.setLogo(R.drawable.back_icon);
        bar.setDisplayHomeAsUpEnabled(true);

    }
    //endregion

    //region INIT SCREEN VALUES
    private void initScreenValues(){

        Intent intent = getIntent();
        bar = getSupportActionBar();
        if(intent != null){
            sender = intent.getStringExtra(Constants.BASE_PAGE_CODE);
            if(sender != null) {

                isNewAddition = false;
                //prepareMovieFromIntent(intent);
                movie = intent.getParcelableExtra(Movie.NAME);

                if(sender.equals(Constants.SEARCH_MOVIES_PAGE)){

                      isNewAddition = true;
                      new LoadMovieBodyTask(loadBodyPb,this).execute(movie);
                }
                if(sender.equals((Constants.MOVIES_LIST_PAGE))||(sender.equals(Constants.SEARCH_MOVIES_PAGE))||(sender.equals(Constants.DB_SEARCH_PAGE))){
                    initEditTextFields();
                    Utilities.UI.setImageToImageView(movie,posterImageView, this);
                    if(bar != null)getSupportActionBar().setTitle(movie.getSubject());
                }
                else {
                    if(bar != null)getSupportActionBar().setTitle(R.string.default_store_movie_title);
                }

            }
            else {

                if(bar != null)getSupportActionBar().setTitle(R.string.default_store_movie_title);
            }
        }
        else {
            isNewAddition = true;
            if(bar != null)getSupportActionBar().setTitle(R.string.default_store_movie_title);
        }

    }
    //endregion

    //region INIT EDIT TEXT FIELDS
    private void initEditTextFields() {
        if(movie != null) {
            subjectEditText.setText(movie.getSubject());
            bodyEditText.setText(movie.getBody());
            urlEditText.setText(movie.getImageUrl());
            yearEditText.setText(movie.getYear());
            isChanged = false;
        }
    }
    //endregion

    //region PREPARE MOVIE TO INSERT TO FIELDS
    private void prepareMovieFromIntent(Intent intent){

        //get data from intent
        int id = intent.getIntExtra(DataConstants.ID,ID_NO_EXIST_CODE);
        String subject = intent.getStringExtra(DataConstants.SUBJECT);
        String body = intent.getStringExtra(DataConstants.BODY);
        String url = intent.getStringExtra(DataConstants.IMAGE_URL);
        int rate = intent.getIntExtra(DataConstants.RATE, Constants.DEFAULT_MOVIE_RATE);
        boolean watched = intent.getBooleanExtra(DataConstants.WATCHED, Constants.DEFAULT_WATCHED_VALUE);
        String year = intent.getStringExtra(DataConstants.YEAR);

        if(id != ID_NO_EXIST_CODE){
            movie = new Movie(id,subject,body,url,watched,rate,year);
        }
    }

    //endregion

    //region STORE IMAGE FUNCTION
    private void storeImage(){


        String data = urlEditText.getText().toString();
        if(!data.equals(Constants.EMPTY_STRING)){

            if(Utilities.Helper.isValidURL(data)){

                String fileName = Utilities.Helper.getFileNameFromURL(data);
                storeImageByFileName(fileName);

            }
            else if(Utilities.Helper.isValidImageName(data)){
                storeImageByFileName(data);
                isChanged = true;
            }
            else {

                info.displayWarningMessage(getString(R.string.invalid_url_message));

            }
        }
        else {
            info.displayWarningMessage(getString(R.string.empty_url_image_message));
        }

    }
    //endregion

    //region PREPARE MOVIE TO STORE
    private boolean prepareMovieFromFields(){


        //get data from edit texts
        subject = subjectEditText.getText().toString();
        body = bodyEditText.getText().toString();
        url = urlEditText.getText().toString();
        year = yearEditText.getText().toString();

        if(isNewAddition){

            return prepareNewItem();

        }else{

            return prepareExistingItem();

        }
    }
    //endregion

    //region PREPARE NEW ITEM
    private boolean prepareNewItem(){

        //check if subject is valid
        if(subject.equals(Constants.EMPTY_STRING)){

            info.displayWarningMessage(getString(R.string.empty_subject_message));
            return false;
        }

        //check if url is valid
        if(!(Utilities.Helper.isValidURL(url)||Utilities.Helper.isValidImageName(url))){
            info.displayWarningMessage(getString(R.string.empty_image_message));
            return false;
        }

        if(!Movie.Helper.isValidYear(year.trim())){
            info.displayWarningMessage(getString(R.string.invalid_year_message));
            return false;
        }


        movie = new Movie(subject,body,url,watched,movieRate,year);
        return true;

    }
    //endregion

    //region PREPARE EXISTING ITEM
    private boolean prepareExistingItem(){

        //check if subject is valid

        if(!subject.equals(Constants.EMPTY_STRING)){
            movie.setSubject(subject);
        }

        //check if url is valid
        if((Utilities.Helper.isValidURL(url)||Utilities.Helper.isValidImageName(url))){
            movie.setImageUrl(url);
        }

        if(Movie.Helper.isValidYear(year)){
            movie.setYear(year);
        }
        movie.setBody(body);

        if((watched == true)&&(!movie.isWatched())){
            movie.setWatched(watched);
        }

        movie.setRate(movieRate);

        return true;
    }
    //endregion

    //region BACK FUNCTION
    private void goBackWithResult(){
        Intent intent;
        if(isStored){
            if((sender == null)&&(isNewAddition)){

                setResult(RESULT_OK);
                finish();
            }
            else {
                if(sender.equals(Constants.MOVIES_LIST_PAGE)){

                    setResult(RESULT_OK);
                    finish();
                }
                else if((sender.equals(Constants.SEARCH_MOVIES_PAGE))||(sender.equals(Constants.DB_SEARCH_PAGE))){
                    intent = new Intent();
                    intent.putExtra(Constants.MOVIE_LOADED_MESSAGE,Constants.MOVIE_LOADED_MESSAGE);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        }
    }
    //endregion

    //region STORE IMAGE BY FILE NAME
    private void storeImageByFileName(String fileName){

        //check if image not store already
        if((fileName != null)&&(posterImageView.getDrawable()!= null)) {
            Bitmap bitmap = ((BitmapDrawable) posterImageView.getDrawable()).getBitmap();
            //check if image exist in image view
            if (bitmap == null) {
                info.displayWarningMessage(getString(R.string.empty_image_message));
                return;
            }
            //store image and his name in preferences
            if(Utilities.SystemHelper.isFilePresent(fileName,this)){

                info.displayInfoMessage(getString(R.string.already_stored_image_message));
                return;
            }
            Utilities.SystemHelper.storeImage(bitmap, fileName, this);
            urlEditText.setText(fileName);
            info.displayWarningMessage(getString(R.string.warning_to_store_data_text));
        }
        else if(posterImageView.getDrawable() == null){
            info.displayWarningMessage(getString(R.string.empty_image_message));
        }
        else if(fileName == null){
            info.displayWarningMessage(getString(R.string.empty_file_name_message));
        }

    }
    //endregion
    //region SHOW ADVANCED OPTIONS DIALOG
    private void showAdvancedOptionsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.advanced_options_items,optionsListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private DialogInterface.OnClickListener optionsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case WATCHED_POSITION:
                    watched = true;
                    isChanged = true;
                    break;
                case ADD_RATE_POSITION:
                    info.showRatesDialog(ratesListener);
                    break;
            }
        }
    };
    //endregion
    //region SHOW RATES DIALOG
    private void showRatesDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.rates,ratesListener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private DialogInterface.OnClickListener ratesListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            movieRate = Integer.parseInt(rates[which]);
            isChanged = true;
        }
    };
    //endregion

    public void onShowImageClick(View view) {

        String url = urlEditText.getText().toString();

        if(Utilities.Helper.isValidURL(url)){

            String fileName = Utilities.Helper.getFileNameFromURL(url);

            if((fileName != null)&&(Utilities.Helper.isValidImageName(fileName))){
                //set image to image view
                Utilities.UI.setImageToImageView(url,posterImageView,this);
                return;
            }
            else {
                info.displayWarningMessage(getString(R.string.invalid_file_name_message));
                return;
            }
        }
        else  if(Utilities.Helper.isValidImageName(url)) {

                Utilities.UI.setImageToImageView(url,posterImageView,this);
                return;

        }
        else {
            info.displayWarningMessage(getString(R.string.invalid_url_message));
            return;
        }

    }

    @Override
    public void onDataReceived(List<Movie> movies) {
        if(movies != null){
            movie = movies.get(MOVIE_POSITION);
            bodyEditText.setText(movie.getBody());
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        isChanged = true;
    }
}
