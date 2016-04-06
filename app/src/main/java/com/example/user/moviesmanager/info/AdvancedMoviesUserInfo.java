package com.example.user.moviesmanager.info;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.user.moviesmanager.R;
import com.example.user.moviesmanager.data.DataConstants;
import com.example.user.moviesmanager.utilities.Constants;

/**
 * Created by User on 20/03/2016.
 */
public class AdvancedMoviesUserInfo extends MoviesUserInfo {


    //region Constants
    private static final int START_POSITION = 0;
    //endregion

    AdvancedMoviesUserInfo(Context context) {
        super(context);
    }



    //region SHOW ADVANCED SEARCH DIALOG
    public void showAdvancedSearchDialog(DialogInterface.OnClickListener onClickListener) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.advanced_search_dialog, null);
        final EditText advancedSearchET = (EditText) view.findViewById(R.id.advanced_search_edit);

        final Button clearBtn = (Button)view.findViewById(R.id.advanced_clear_button);
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = advancedSearchET.getText().toString();
                    text = text.substring(0,text.length() - 1);
                    advancedSearchET.setText(text);
                }
            });
            clearBtn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    advancedSearchET.setText(Constants.EMPTY_STRING);
                    return true;
                }
            });

            initColumnSpinner(view, advancedSearchET);

            initOrAndSpinner(view, advancedSearchET);

            initEqualsLikeSpinner(view, advancedSearchET);

            builder.setPositiveButton(android.R.string.ok, onClickListener);
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

    }



    private void initEqualsLikeSpinner(View view, final EditText advancedSearchET) {
        final Spinner equalsLikeSpinner = (Spinner) view.findViewById(R.id.equals_like_spinner);
        final String[]signs = context.getResources().getStringArray(R.array.equals_like);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, signs);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        equalsLikeSpinner.setAdapter(dataAdapter);
        equalsLikeSpinner.setSelection(START_POSITION, false);
        equalsLikeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != START_POSITION) {
                    advancedSearchET.append(" " + signs[position] + " ");
                    equalsLikeSpinner.setSelection(START_POSITION, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initOrAndSpinner(View view, final EditText advancedSearchET) {
        final Spinner orAndSpinner = (Spinner) view.findViewById(R.id.and_or_spinner);
        final String[]letters = context.getResources().getStringArray(R.array.or_and);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,letters);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        orAndSpinner.setAdapter(dataAdapter);
        orAndSpinner.setSelection(START_POSITION,false);
        orAndSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != START_POSITION){
                    advancedSearchET.append(" " + letters[position] + " ");
                    orAndSpinner.setSelection(START_POSITION, false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initColumnSpinner(View view, final TextView advancedSearchET) {
        final Spinner columnsSpinner = (Spinner) view.findViewById(R.id.columns_spinner);
        ArrayAdapter<String> columnsAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, DataConstants.COLUMNS);

        // Drop down layout style - list view with radio button
        columnsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        columnsSpinner.setAdapter(columnsAdapter);
        columnsSpinner.setSelection(START_POSITION,false);
        columnsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != START_POSITION) {
                    advancedSearchET.append(DataConstants.COLUMNS[position]);
                    columnsSpinner.setSelection(START_POSITION, false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //endregion
}
