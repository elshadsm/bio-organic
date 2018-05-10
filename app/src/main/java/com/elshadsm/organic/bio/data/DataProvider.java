package com.elshadsm.organic.bio.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class DataProvider extends ContentProvider {

    private static final String LOG_TAG = DataProvider.class.getSimpleName();

    private final static int CODE_PRODUCTS = 100;
    private final static int CODE_PRODUCTS_WITH_ID = 101;
    private final static int CODE_REVIEWS = 200;
    private final static int CODE_FAVORITES = 300;
    private final static int CODE_SHOPPING_CART = 400;
    private final static int CODE_PRODUCTS_IN_SHOPPING_CART = 500;
    private final static int CODE_PRODUCTS_IN_FAVORITE_LIST = 600;
    private final static UriMatcher uriMatcher = buildUriMatcher();
    private DatabaseHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_PRODUCTS, CODE_PRODUCTS);
        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_PRODUCTS + "/#", CODE_PRODUCTS_WITH_ID);
        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_REVIEWS, CODE_REVIEWS);
        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_FAVORITES, CODE_FAVORITES);
        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_SHOPPING_CART, CODE_SHOPPING_CART);
        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_PRODUCTS_IN_SHOPPING_CART, CODE_PRODUCTS_IN_SHOPPING_CART);
        uriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_PRODUCTS_IN_FAVORITE_LIST, CODE_PRODUCTS_IN_FAVORITE_LIST);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case CODE_PRODUCTS:
                cursor = database.query(DatabaseContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_REVIEWS:
                cursor = database.query(DatabaseContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FAVORITES:
                cursor = database.query(DatabaseContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_SHOPPING_CART:
                cursor = database.query(DatabaseContract.ShoppingCartEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_PRODUCTS_IN_SHOPPING_CART:
                cursor = database.rawQuery(DatabaseContract.ProductsInShoppingCartEntry.RAW_SQL, null);
                break;
            case CODE_PRODUCTS_IN_FAVORITE_LIST:
                cursor = database.rawQuery(DatabaseContract.ProductsInFavoriteListEntry.RAW_SQL, null);
                break;
            default:
                Log.e(LOG_TAG, "Unknown query uri: " + uri);
                return null;
        }
        assert getContext() != null;
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        try (final SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            long id;
            switch (uriMatcher.match(uri)) {
                case CODE_PRODUCTS:
                    id = database.insert(DatabaseContract.ProductEntry.TABLE_NAME, null, values);
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(DatabaseContract.ProductEntry.CONTENT_URI, id);
                    } else {
                        Log.e(LOG_TAG, "Failed to insert row into: " + uri);
                        return null;
                    }
                    break;
                case CODE_REVIEWS:
                    id = database.insert(DatabaseContract.ReviewEntry.TABLE_NAME, null, values);
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(DatabaseContract.ReviewEntry.CONTENT_URI, id);
                    } else {
                        Log.e(LOG_TAG, "Failed to insert row into: " + uri);
                        return null;
                    }
                    break;
                case CODE_FAVORITES:
                    id = database.insert(DatabaseContract.FavoriteEntry.TABLE_NAME, null, values);
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(DatabaseContract.FavoriteEntry.CONTENT_URI, id);
                    } else {
                        Log.e(LOG_TAG, "Failed to insert row into: " + uri);
                        return null;
                    }
                    break;
                case CODE_SHOPPING_CART:
                    id = database.insert(DatabaseContract.ShoppingCartEntry.TABLE_NAME, null, values);
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(DatabaseContract.ShoppingCartEntry.CONTENT_URI, id);
                    } else {
                        Log.e(LOG_TAG, "Failed to insert row into: " + uri);
                        return null;
                    }
                    break;
                default:
                    Log.e(LOG_TAG, "Unknown insert uri: " + uri);
                    return null;
            }
        }
        assert getContext() != null;
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowDeleted;
        try (final SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            switch (uriMatcher.match(uri)) {
                case CODE_PRODUCTS:
                    rowDeleted = database.delete(DatabaseContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case CODE_REVIEWS:
                    rowDeleted = database.delete(DatabaseContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case CODE_FAVORITES:
                    rowDeleted = database.delete(DatabaseContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case CODE_SHOPPING_CART:
                    rowDeleted = database.delete(DatabaseContract.ShoppingCartEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    Log.e(LOG_TAG, "Unknown delete uri: " + uri);
                    return 0;
            }
        }
        if (rowDeleted != 0) {
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}