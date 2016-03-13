package com.example.user.moviesmanager.customadapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.moviesmanager.DbSearchActivity;
import com.example.user.moviesmanager.MoviesListActivity;
import com.example.user.moviesmanager.R;
import com.example.user.moviesmanager.SearchMoviesActivity;
import com.example.user.moviesmanager.data.Movie;
import com.example.user.moviesmanager.utilities.Constants;
import com.example.user.moviesmanager.utilities.Utilities;
import com.github.siyamed.shapeimageview.BubbleImageView;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.siyamed.shapeimageview.HexagonImageView;
import com.github.siyamed.shapeimageview.OctogonImageView;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.github.siyamed.shapeimageview.shader.BubbleShader;

import java.util.List;

/**
 * Created by User on 15/12/2015.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    //region Constants
    private static final int IMAGE_VIEW_CODE = 0;

    private static final int ROUNDED_IMAGE_VIEW_CODE = 1;

    private static final int CIRCULAR_IMAGE_VIEW_CODE = 2;

    private static final int HEXAGON_IMAGE_VIEW_CODE = 3;

    private static final int OCTOGON_IMAGE_VIEW_CODE = 4;

    private static final int BUBBLE_IMAGE_VIEW_CODE = 5;

    private static final String DEFAULT_IMAGE_CODE_STRING = "0";
    //endregion
    //region INSTANCE VARIABLES
    private int resource;

    private TypedArray colorResources;

    private int color;

    private SharedPreferences prefs;

    private boolean isWatchedShow;

    private boolean isYearShow;

    private boolean isRateShow;

    private int imageTypeIndex;
    //endregion

    //region CONSTRUCTOR
    public MoviesAdapter(Context context, int resource, List<Movie> objects) {
        super(context, resource, objects);
        this.resource = resource;
        colorResources = context.getResources().obtainTypedArray(R.array.rate_colors_resources);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        setAdapterSettings(context);
    }

    //endregion

    //region GET VIEW
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(resource, null);
        }

        Movie movie = getItem(position);

        if (movie != null) {

            LinearLayout layout = (LinearLayout) view.findViewById(R.id.image_container);
            layout.addView(imageViewFactory(imageTypeIndex));
            ImageView imageView = (ImageView)view.findViewById(R.id.movies_list_item_image);
            TextView subjectTextView = (TextView) view.findViewById(R.id.subject);
            TextView yearTextView = (TextView)view.findViewById(R.id.year);
            CheckBox checkBox = (CheckBox)view.findViewById(R.id.watched);

            if (subjectTextView != null) {
                subjectTextView.setText(movie.getSubject());
            }

            if (yearTextView != null) {

                if(isYearShow){
                    yearTextView.setVisibility(View.VISIBLE);
                    yearTextView.setText(movie.getYear());
                }
                else {
                    yearTextView.setVisibility(View.GONE);
                }

            }

            if(imageView != null) {

                Utilities.UI.setImageToImageView(movie, imageView, getContext());
                imageView.setVisibility(View.VISIBLE);
            }


            if(checkBox != null){
                if(isWatchedShow){
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(movie.isWatched());
                }
                else {
                    checkBox.setVisibility(View.GONE);
                }

            }


            if(isRateShow){
                int colorResPos = movie.getRate() - Constants.OFFSET;
                color = getContext().getResources().getColor(colorResources.getResourceId(colorResPos,R.color.color_1));
                view.setBackgroundColor(color);
            }
            else {
                color = getContext().getResources().getColor(R.color.color_1);
                view.setBackgroundColor(color);
            }



        }

        return view;
    }
    //endregion
    //region SERVICE FUNCTIONS
    private void setAdapterSettings(Context context) {
        String key;
        String indexStr;
        if(context instanceof MoviesListActivity){

            key = context.getString(R.string.list_enable_watched_key);
            isWatchedShow = prefs.getBoolean(key, true);
            key = context.getString(R.string.list_enable_year_key);
            isYearShow = prefs.getBoolean(key, true);
            key = context.getString(R.string.list_enable_rate_key);
            isRateShow = prefs.getBoolean(key, true);
            key = context.getString(R.string.movies_list_image_shapes_list_key);
            indexStr = prefs.getString(key, DEFAULT_IMAGE_CODE_STRING);
            imageTypeIndex = Integer.parseInt(indexStr);



        }
        else if(context instanceof SearchMoviesActivity){

            key = context.getString(R.string.search_enable_watched_key);
            isWatchedShow = prefs.getBoolean(key, false);
            key = context.getString(R.string.search_enable_year_key);
            isYearShow = prefs.getBoolean(key, true);
            isRateShow = false;
            key = context.getString(R.string.web_search_image_shapes_list_key);
            indexStr = prefs.getString(key,DEFAULT_IMAGE_CODE_STRING);
            imageTypeIndex = Integer.parseInt(indexStr);

        }
        else if(context instanceof DbSearchActivity) {

            key = context.getString(R.string.db_search_enable_watched_key);
            isWatchedShow = prefs.getBoolean(key, true);
            key = context.getString(R.string.db_search_enable_year_key);
            isYearShow = prefs.getBoolean(key, true);
            key = context.getString(R.string.db_search_enable_rate_key);
            isRateShow = prefs.getBoolean(key, true);
            key = context.getString(R.string.db_search_image_shapes_list_key);
            indexStr = prefs.getString(key, DEFAULT_IMAGE_CODE_STRING);
            imageTypeIndex = Integer.parseInt(indexStr);
        }



    }
    //endregion

    //region IMAGE VIEW FACTORY
    //https://github.com/siyamed/android-shape-imageview  :-> all types of shapes
    private ImageView imageViewFactory(int index){
        ImageView imageView = null;
        switch(index){
            case IMAGE_VIEW_CODE:
                imageView = getImageView();
                break;
            case ROUNDED_IMAGE_VIEW_CODE:
                imageView = getRoundedImageView();
                break;
            case CIRCULAR_IMAGE_VIEW_CODE:
                imageView = getCircularImageView();
                break;
            case HEXAGON_IMAGE_VIEW_CODE:
                imageView = getHexagonImageView();
                break;
            case OCTOGON_IMAGE_VIEW_CODE:
                imageView = getOctogonImageView();
                break;
            case BUBBLE_IMAGE_VIEW_CODE:
                imageView = getBubbleImageView();
                break;
        }
        imageView.setId(R.id.movies_list_item_image);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return imageView;
    }


    private ImageView getBubbleImageView() {
        BubbleImageView imageView = new BubbleImageView(getContext());
        imageView.setBorderColor(Color.WHITE);
        imageView.setBorderWidth(2);
        imageView.setSquare(true);
        imageView.setArrowPosition(BubbleShader.ArrowPosition.RIGHT);
        return imageView;
    }

    private ImageView getOctogonImageView() {
        OctogonImageView imageView = new OctogonImageView(getContext());
        imageView.setBorderColor(Color.WHITE);
        imageView.setBorderWidth(2);
        return imageView;
    }

    private ImageView getHexagonImageView() {
        HexagonImageView imageView = new HexagonImageView(getContext());
        imageView.setBorderColor(Color.WHITE);
        imageView.setBorderWidth(2);
        return imageView;
    }

    private ImageView getCircularImageView() {
        CircularImageView imageView = new CircularImageView(getContext());
        imageView.setBorderColor(Color.WHITE);
        imageView.setBorderWidth(2);
        return imageView;
    }


    private ImageView getRoundedImageView() {

        RoundedImageView imageView = new RoundedImageView(getContext());
        imageView.setBorderColor(Color.WHITE);
        imageView.setBorderWidth(2);
        imageView.setSquare(true);
        imageView.setRadius(15);
        return imageView;
    }

    private ImageView getImageView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    //endregion
}
