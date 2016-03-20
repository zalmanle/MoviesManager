package com.example.user.moviesmanager.info;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.user.moviesmanager.R;

/**
 * Created by User on 15/03/2016.
 */
public class MoviesUserInfo extends UserInfo {

    MoviesUserInfo(Context context) {
        super(context);
    }

    //region SHOW ALERT DIALOG ON LIST ITEM CLICKED
    public void showMovieOptionDialog(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.options_dialog_title);
        builder.setItems(R.array.movie_options_items, listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion

    //region SHOW ALERT DIALOG TO REMOVE ITEM
    public void showDeleteItemWarning(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.warning_title_text);
        builder.setIcon(R.drawable.red_warning_icon);
        builder.setMessage(R.string.delete_one_movie_warning_message);
        builder.setPositiveButton(R.string.positive_button_text,listener);
                builder.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    //endregion
    //region SHOW ALERT DIALOG TO REMOVE ALL ITEMS
    public void showDeleteAllItemsWarning(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.warning_title_text);
        builder.setIcon(R.drawable.red_warning_icon);
        builder.setMessage(R.string.delete_all_movies_warning_message);
        builder.setPositiveButton(R.string.positive_button_text, listener);
        builder.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    //endregion
}
