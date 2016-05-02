package com.example.user.moviesmanager.db.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.user.moviesmanager.db.MoviesContract;
import com.example.user.moviesmanager.db.MoviesDBHelper;

import java.util.HashMap;

/**
 * Created by User on 18/02/2016.
 */
public class MoviesProvider extends ContentProvider {

    private MoviesDBHelper helper;

    private static final UriMatcher sUriMatcher;
    private static final int MOVIES_TYPE_LIST = 1;
    private static final int MOVIES_TYPE_ONE = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MoviesContract.AUTHORITY, "movies", MOVIES_TYPE_LIST);
        sUriMatcher.addURI(MoviesContract.AUTHORITY, "movies/#",MOVIES_TYPE_ONE);
    }

    private static HashMap<String, String> moviesProjectionMap;

    static {
        moviesProjectionMap = new HashMap<String, String>();
        moviesProjectionMap.put(MoviesContract.Table.ID, MoviesContract.Table.ID);
        moviesProjectionMap.put(MoviesContract.Table.SUBJECT, MoviesContract.Table.SUBJECT);
        moviesProjectionMap.put(MoviesContract.Table.BODY, MoviesContract.Table.BODY);
        moviesProjectionMap.put(MoviesContract.Table.IMAGE_URL, MoviesContract.Table.IMAGE_URL);
        moviesProjectionMap.put(MoviesContract.Table.WATCHED, MoviesContract.Table.WATCHED);
        moviesProjectionMap.put(MoviesContract.Table.RATE, MoviesContract.Table.RATE);
        moviesProjectionMap.put(MoviesContract.Table.DATE, MoviesContract.Table.DATE);
    }
    @Override
    public boolean onCreate() {

        //on emulator of android studio not work
        android.os.Process.getThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        helper = new MoviesDBHelper(getContext());
        return (helper != null);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch(sUriMatcher.match(uri)) {
            case MOVIES_TYPE_LIST:
                builder.setTables(MoviesContract.Table.TABLE_NAME);
                builder.setProjectionMap(moviesProjectionMap);
                break;

            case MOVIES_TYPE_ONE:
                builder.setTables(MoviesContract.Table.TABLE_NAME);
                builder.setProjectionMap(moviesProjectionMap);
                builder.appendWhere(MoviesContract.Table.ID + " = " +
                        uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor queryCursor = builder.query(db, projection, selection, selectionArgs, null, null, null);
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return queryCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case MOVIES_TYPE_LIST:
                return MoviesContract.CONTENT_TYPE_MOVIES_LIST;

            case MOVIES_TYPE_ONE:
                return MoviesContract.CONTENT_TYPE_MOVIES_ONE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(sUriMatcher.match(uri) != MOVIES_TYPE_LIST) {
            throw new IllegalArgumentException("[Insert](01)Unknown URI: " + uri);
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        long rowId = db.insert(MoviesContract.Table.TABLE_NAME, null, values);
        if(rowId > 0) {
            Uri articleUri = ContentUris.withAppendedId(MoviesContract.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(articleUri, null);
            return articleUri;
        }
        throw new IllegalArgumentException("[Insert](02)Unknown URI: " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = 0;
        switch(sUriMatcher.match(uri)) {
            case MOVIES_TYPE_LIST:
                count = db.delete(MoviesContract.Table.TABLE_NAME,selection,selectionArgs);
                break;

            case MOVIES_TYPE_ONE:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(MoviesContract.Table.TABLE_NAME,
                        MoviesContract.Table.ID + " = " + rowId +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int count = 0;
        switch(sUriMatcher.match(uri)) {
            case MOVIES_TYPE_LIST:
                count = db.update(MoviesContract.Table.TABLE_NAME, values,selection,selectionArgs);
                break;

            case MOVIES_TYPE_ONE:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(MoviesContract.Table.TABLE_NAME, values,
                        MoviesContract.Table.ID + " = " + rowId +
                                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
                        selectionArgs);

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }



}
