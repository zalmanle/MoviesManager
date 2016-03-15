package com.example.user.moviesmanager.info;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.user.moviesmanager.R;

/**
 * Created by User on 15/03/2016.
 */
public class AdvancedOptionsUserInfo extends UserInfo {

    public AdvancedOptionsUserInfo(Context context) {
        super(context);
    }

    //region SHOW ADVANCED OPTIONS DIALOG
    public void showAdvancedOptionsDialog(DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.advanced_options_items,listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion

    //region SHOW RATES DIALOG
    public void showRatesDialog(DialogInterface.OnClickListener listener){
        final String[]rates = context.getResources().getStringArray(R.array.rates);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.rates,listener);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //endregion

}
