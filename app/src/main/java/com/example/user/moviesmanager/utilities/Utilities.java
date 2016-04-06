package com.example.user.moviesmanager.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.moviesmanager.DbSearchActivity;
import com.example.user.moviesmanager.MoviesListActivity;
import com.example.user.moviesmanager.R;
import com.example.user.moviesmanager.SearchMoviesActivity;
import com.example.user.moviesmanager.StoreMovieActivity;
import com.example.user.moviesmanager.data.DataConstants;
import com.example.user.moviesmanager.data.Movie;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by User on 09/12/2015.
 */
public class Utilities {

    //region Constants
    private static final String IMAGE_PATTERN =
            "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)";

    private static final String TAG = "Utilities";

    private static final String CAMERA_LOCATION_STRING = "DCIM/Camera";

    private static final int RADIUS = 70;

    private static final int MARGIN = 10;

    private static final String QUERY_PATTERN = "(((\\s)(\\()(\\s))?(subject|body|image_url|watched|rate|date)(\\s)(=|like)(((\\s)(\\w+))+((\\s)(\\))(\\s))?)(\\s?))?((or|and|OR|AND)(\\s)((\\s)(\\()(\\s))?(subject|body|image_url|watched|rate|date)(\\s)(=|like)(((\\s)(\\w+))+)((\\s)(\\))(\\s))?(\\s?))*";

    //check params without brackets
    //private static final String QUERY_PATTERN = "((subject|body|image_url|watched|rate|date)(\\s)(=|like)(((\\s)(\\w+))+)(\\s?))?((or|and|OR|AND)(\\s)(subject|body|image_url|watched|rate|date)(\\s)(=|like)(((\\s)(\\w+))+)(\\s?))*";
    //endregion



    //region class Helper
    public static class Helper {

        /**
         * This function validate image name
         */
        public static boolean isValidImageName(String imageName) {

            Pattern pattern = Pattern.compile(IMAGE_PATTERN);
            Matcher matcher = pattern.matcher(imageName);
            return matcher.matches();
        }
        //endregion

        /**
         * This function validate image name
         */
        public static boolean isValidSearchQuery(String queryStr) {

            //check if query is empty
            if(TextUtils.isEmpty(queryStr)){
                return false;
            }

            Pattern pattern = Pattern.compile(QUERY_PATTERN);
            Matcher matcher = pattern.matcher(queryStr);
            return matcher.matches();
        }
        //endregion

        //region image url validator

        /**
         * This function check if image url is valid
         */
        public static boolean isValidURL(CharSequence input) {
            if (TextUtils.isEmpty(input)) {
                return false;
            }
            Pattern URL_PATTERN = Patterns.WEB_URL;
            boolean isURL = URL_PATTERN.matcher(input).matches();
            if (!isURL) {
                String urlString = input + "";
                if (URLUtil.isNetworkUrl(urlString)) {
                    try {
                        new URL(urlString);
                        isURL = true;
                    } catch (Exception e) {
                    }
                }
            }
            return isURL;
        }
        //endregion


        //region GET FILE NAME FROM URL
        public static String getFileNameFromURL(String url) {
            String fileNameWithExtension = null;
            if (URLUtil.isValidUrl(url)) {
                fileNameWithExtension = URLUtil.guessFileName(url, null, null);
            }
            return fileNameWithExtension;
        }
        //endregion

        //region RETURN INTENT TO EDIT
        public static Intent getIntentFromMovie01(Movie movie,Context context){
            //create intent
            Intent intent = new Intent(context,StoreMovieActivity.class);

            //set data
            intent.putExtra(DataConstants.ID,movie.getId());
            intent.putExtra(DataConstants.SUBJECT,movie.getSubject());
            intent.putExtra(DataConstants.BODY,movie.getBody());
            intent.putExtra(DataConstants.IMAGE_URL,movie.getImageUrl());
            intent.putExtra(DataConstants.RATE,movie.getRate());
            intent.putExtra(DataConstants.WATCHED, movie.isWatched());
            intent.putExtra(DataConstants.YEAR, movie.getYear());

            //return intent
            return intent;
        }
        public static Intent getIntentFromMovie02(Movie movie,Context context){
            //create intent
            Intent intent = new Intent(context,StoreMovieActivity.class);

            //set data
            intent.putExtra(Movie.NAME,movie);

            //return intent
            return intent;
        }
        //endregion

    }
    //endregion

    //region class System
    public static class SystemHelper {

        //region SHARE BITMAP IMAGE
        public static void shareImageFromImageView(Activity activity,ImageView imageView)
                throws Exception {


            if(imageView != null){
                // Get access to the URI for the bitmap
                Uri bmpUri = getLocalBitmapUri(imageView);
                if (bmpUri != null) {
                    // Construct a ShareIntent with link to image
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");
                    // Launch sharing dialog for image
                    activity.startActivity(Intent.createChooser(shareIntent, "Share Image"));
                } else {

                    throw new Exception();

                }
            }

        }

        // Returns the URI path to the Bitmap displayed in specified ImageView
        private static Uri getLocalBitmapUri(ImageView imageView) {
            // Extract Bitmap from ImageView drawable
            Drawable drawable = imageView.getDrawable();
            Bitmap bmp = null;
            if (drawable instanceof BitmapDrawable){
                bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else {
                return null;
            }
            // Store image to default external storage directory
            Uri bmpUri = null;
            try {
                File file =  new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
                file.getParentFile().mkdirs();
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }
        //endregion


        //region Gallery Storage Function
        public static void storeImageToGallery(Context context,ImageView imageView,Movie movie) {

            fixMediaDir();
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,movie.getSubject(),movie.getBody());
        }

        public static void fixMediaDir() {
            File sdcard = Environment.getExternalStorageDirectory();
            if (sdcard != null) {
                File mediaDir = new File(sdcard, CAMERA_LOCATION_STRING);
                if (!mediaDir.exists()) {
                    mediaDir.mkdirs();
                }
            }
        }

        //endregion
        //region CHECK IF IMAGE FILE ALREADY EXIST
        public static boolean isFilePresent(String fileName,Context context) {
            String filePath = Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + context.getApplicationContext().getPackageName()
                    + "/Images/" + fileName;
            File file = new File(filePath);
            return file.exists();
        }
        //endregion

        //region STORE IMAGE FUNCTIONS

        /**
         * This function store image in internal storage
         * @param image - file to store
         * @param fileName - name of file
         * @param context - context of activity
         */
        public static void storeImage(Bitmap image,String fileName,Context context) {
            File pictureFile = getOutputMediaFile(fileName,context);
            if (pictureFile == null) {
                Log.d(TAG,
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }catch (OutOfMemoryError e) {
                Log.d(TAG, "Image is too large. choose other " + e.getMessage());

            }
        }

        private static File getOutputMediaFile(String fileName,Context context){
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                    + "/Android/data/"
                    + context.getApplicationContext().getPackageName()
                    + "/Images");

            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()){
                if (! mediaStorageDir.mkdirs()){
                    return null;
                }
            }
            // Create a media file name
            File mediaFile;

            mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
            return mediaFile;
        }
        //endregion

    }
    //endregion
    //region class UI
    public static class UI {

        //region SET IMAGE TO IMAGE VIEW WITH MOVIE OBJECT
        public static void setImageToImageView(Movie movie, ImageView imageView, Context context) {


            //check if movie not equals null
            if (movie != null) {

                String data = movie.getImageUrl();

                //check if image url field is url
                if (Helper.isValidURL(data)) {

                    //load image from url from web
                    Picasso.with(context)
                            .load(data)
                                    //.transform(new RoundedTransformation(RADIUS,MARGIN))//radius,margin
                            .into(imageView);

                } //image loaded and load it from application
                else if (Helper.isValidImageName(data)) {

                    File imgFile = new File(movie.getImageUrl().trim());
                    if (imgFile.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                        imageView.setImageBitmap(myBitmap);

                    } else {
                        //internal path to load
                        String filePath = Environment.getExternalStorageDirectory()
                                + "/Android/data/"
                                + context.getApplicationContext().getPackageName()
                                + "/Images/" + data;
                        Bitmap bmp = BitmapFactory.decodeFile(filePath);
                        //set image to image view
                        imageView.setImageBitmap(bmp);
                    }


                }
            }
        }
        //endregion
        //region SET IMAGE TO IMAGE VIEW WITH URL
        public static boolean setImageToImageView(String url, ImageView imageView, Context context) {


            //check if movie not equals null
            if (url != null) {

                //check if image url field is url
                if (Helper.isValidURL(url)) {

                    //load image from url from web
                    Picasso.with(context)
                            .load(url)
                                    //.transform(new RoundedTransformation(RADIUS, MARGIN))//radius,margin
                            .into(imageView);
                    return true;
                }
                else {
                    return false;
                }

            } //image loaded and load it from application
            else {
                return false;
            }


        }

        //endregion
        //region CREATE TOAST WITH IMAGE
        public static Toast makeImageToast(Context context, int imageResId,int textResId, int length) {
            Toast toast = Toast.makeText(context,textResId, length);

            View rootView = toast.getView();
            LinearLayout linearLayout = null;
            View messageTextView = null;

            // check (expected) toast layout
            if (rootView instanceof LinearLayout) {
                linearLayout = (LinearLayout) rootView;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    linearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                }

                if (linearLayout.getChildCount() == 1) {
                    View child = linearLayout.getChildAt(0);

                    if (child instanceof TextView) {
                        messageTextView = child;
                    }
                }
            }

            // cancel modification because toast layout is not what we expected
            if (linearLayout == null || messageTextView == null) {
                return toast;
            }

            ViewGroup.LayoutParams textParams = messageTextView.getLayoutParams();
            ((LinearLayout.LayoutParams) textParams).gravity = Gravity.CENTER_VERTICAL;

            // convert dip dimension
            float density = context.getResources().getDisplayMetrics().density;
            int imageSize = (int) (density * 25 + 0.5f);
            int imageMargin = (int) (density * 15 + 0.5f);

            // setup image view layout parameters
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
            imageParams.setMargins(0, 0, imageMargin, 0);
            imageParams.gravity = Gravity.CENTER_VERTICAL;

            // setup image view
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageResId);
            imageView.setLayoutParams(imageParams);

            // modify root layout
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.addView(imageView, 0);

            return toast;
        }
        //endregion
        //region HIDE VIRTUAL KEYBOARD METHOD
        public static void hide_keyboard(Activity activity) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if(view == null) {
                view = new View(activity);
            }
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        //endregion
    }
    //endregion
    //region CLASS DATA HELPER
    public static class MovieHelper {
        //region SORTING FUNCTIONS
        public static void setMoviesOrder(List<Movie> list,Context context){
            int orderValue = getOrderValue(context);
            switch(orderValue){
                case Constants.EMPTY_ORDER_CODE:
                case Constants.BY_INSERT_ORDER_CODE:
                    break;
                case Constants.SUBJECT_ORDER_CODE:
                    sortMoviesListBySubject(list);
                    break;
                case Constants.ASCENDING_YEAR_CODE:
                    sortMoviesListByYear(list,Constants.ASC_YEAR_ORDER);
                    break;
                case Constants.DESCENDING_YEAR_CODE:
                    sortMoviesListByYear(list,Constants.DESC_YEAR_ORDER);
                    break;
            }

        }
        private static int getOrderValue(Context context){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String key = null;
            String valueStr;
            int value;
            //get preferences key
            if(context instanceof MoviesListActivity){
                key = context.getString(R.string.movies_list_order_list_key);
            }
            else if(context instanceof SearchMoviesActivity){
                key = context.getString(R.string.web_search_order_list_key);
            }
            else if(context instanceof DbSearchActivity){
                key = context.getString(R.string.db_search_order_list_key);
            }
            //return value of order preferences list
            if(key.equals(Constants.EMPTY_STRING)){
                return Constants.EMPTY_ORDER_CODE;
            }
            else {
                valueStr = prefs.getString(key, Constants.BY_INSERT_ORDER_CODE_STRING);
                value = Integer.parseInt(valueStr);
                return value;
            }

        }
        private static void sortMoviesListBySubject(List<Movie>list){

            Comparator<Movie> comparator = new Comparator<Movie>() {
                @Override
                public int compare(Movie lhs, Movie rhs) {
                    return lhs.getSubject().compareToIgnoreCase(rhs.getSubject());
                }
            };
            Collections.sort(list, comparator);
        }

        private static void sortMoviesListByYear(List<Movie>list, final String yearOrder){
            Comparator<Movie>comparator = new Comparator<Movie>() {
                @Override
                public int compare(Movie lhs, Movie rhs) {
                    int lYear = Integer.parseInt(lhs.getYear());
                    int rYear = Integer.parseInt(rhs.getYear());
                    return yearOrder.equals(Constants.DESC_YEAR_ORDER)? rYear - lYear:lYear - rYear;
                }
            };
            Collections.sort(list, comparator);
        }
        //endregion
    }
    //endregion
    //endregion

}
