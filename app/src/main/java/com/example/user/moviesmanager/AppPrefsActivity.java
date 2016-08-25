package com.example.user.moviesmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by User on 19/01/2016.
 */
public class AppPrefsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    private boolean isChanged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        isChanged = false;
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        isChanged = true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        int resultCode = isChanged == true ? RESULT_OK : RESULT_CANCELED;
        setResult(resultCode,intent);
        super.onBackPressed();
    }
}
