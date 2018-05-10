package com.elshadsm.organic.bio.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {

    static final String AUTHORITY = "com.elshadsm.organic.bio";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static final String PATH_PRODUCTS = "products";
    static final String PATH_REVIEWS = "reviews";
    static final String PATH_FAVORITES = "favorites";
    static final String PATH_SHOPPING_CART = "shopping_cart";
    static final String PATH_PRODUCTS_IN_SHOPPING_CART = "products_in_shopping_cart";

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCTS).build();

        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_ID = "id";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IMAGE_SRC = "imageSrc";
        public static final String COLUMN_INSERTION_DATE = "insertionDate";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TITLE = "title";
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_REVIEW_ID = "id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_FULL_NAME = "full_name";
        public static final String COLUMN_PRODUCT_ID = "product_id";
    }

    public static final class FavoriteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_PRODUCT_ID = "product_id";
    }

    public static final class ShoppingCartEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOPPING_CART).build();

        public static final String TABLE_NAME = "shopping_cart";
        public static final String COLUMN_PRODUCT_ID = "product_id";
    }

    public static final class ProductsInShoppingCartEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCTS_IN_SHOPPING_CART).build();

        public static final String RAW_SQL = "SELECT " +
                DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + ", " +
                DatabaseContract.ProductEntry.COLUMN_CATEGORY + ", " +
                DatabaseContract.ProductEntry.COLUMN_DESCRIPTION + ", " +
                DatabaseContract.ProductEntry.COLUMN_IMAGE_SRC + ", " +
                DatabaseContract.ProductEntry.COLUMN_INSERTION_DATE + ", " +
                DatabaseContract.ProductEntry.COLUMN_NAME + ", " +
                DatabaseContract.ProductEntry.COLUMN_PRICE + ", " +
                DatabaseContract.ProductEntry.COLUMN_QUANTITY + ", " +
                DatabaseContract.ProductEntry.COLUMN_RATING + ", " +
                DatabaseContract.ProductEntry.COLUMN_STATUS + ", " +
                DatabaseContract.ProductEntry.COLUMN_TITLE + " " +
                " FROM " + ProductEntry.TABLE_NAME + ", " + ShoppingCartEntry.TABLE_NAME+
                " WHERE " + DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + "="+ShoppingCartEntry.COLUMN_PRODUCT_ID;
    }

}
