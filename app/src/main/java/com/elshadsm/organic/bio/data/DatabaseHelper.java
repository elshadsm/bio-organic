package com.elshadsm.organic.bio.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 3;

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createProductsTable(database);
        createReviewsTable(database);
        createFavoritesTable(database);
        createShoppingCartTable(database);
    }

    private void createProductsTable(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " +
                        DatabaseContract.ProductEntry.TABLE_NAME + "(" +
                        DatabaseContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," +
                        DatabaseContract.ProductEntry.COLUMN_CATEGORY + " TEXT NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_IMAGE_SRC + " TEXT NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_INSERTION_DATE + " TEXT NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_PRICE + " REAL NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_QUANTITY + " TEXT NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_RATING + " REAL NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_STATUS + " TEXT NOT NULL," +
                        DatabaseContract.ProductEntry.COLUMN_TITLE + " TEXT NOT NULL" +
                        ");"
        );
    }

    private void createReviewsTable(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " +
                        DatabaseContract.ReviewEntry.TABLE_NAME + "(" +
                        DatabaseContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DatabaseContract.ReviewEntry.COLUMN_REVIEW_ID + " INTEGER NOT NULL UNIQUE," +
                        DatabaseContract.ReviewEntry.COLUMN_DATE + " TEXT NOT NULL," +
                        DatabaseContract.ReviewEntry.COLUMN_REVIEW + " TEXT NOT NULL," +
                        DatabaseContract.ReviewEntry.COLUMN_FULL_NAME + " TEXT NOT NULL," +
                        DatabaseContract.ReviewEntry.COLUMN_PRODUCT_ID + " INTEGER NOT NULL," +
                        " FOREIGN KEY (" + DatabaseContract.ReviewEntry.COLUMN_PRODUCT_ID +
                        ") REFERENCES " + DatabaseContract.ProductEntry.TABLE_NAME +
                        "(" + DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + ")" +
                        ");"
        );
    }

    private void createFavoritesTable(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " +
                        DatabaseContract.FavoriteEntry.TABLE_NAME + "(" +
                        DatabaseContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DatabaseContract.FavoriteEntry.COLUMN_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," +
                        " FOREIGN KEY (" + DatabaseContract.FavoriteEntry.COLUMN_PRODUCT_ID +
                        ") REFERENCES " + DatabaseContract.ProductEntry.TABLE_NAME +
                        "(" + DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + ")" +
                        ");"
        );
    }

    private void createShoppingCartTable(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " +
                        DatabaseContract.ShoppingCartEntry.TABLE_NAME + "(" +
                        DatabaseContract.ShoppingCartEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DatabaseContract.ShoppingCartEntry.COLUMN_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," +
                        " FOREIGN KEY (" + DatabaseContract.FavoriteEntry.COLUMN_PRODUCT_ID +
                        ") REFERENCES " + DatabaseContract.ProductEntry.TABLE_NAME +
                        "(" + DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + ")" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ProductEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.FavoriteEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ShoppingCartEntry.TABLE_NAME);
        onCreate(database);
    }
}
