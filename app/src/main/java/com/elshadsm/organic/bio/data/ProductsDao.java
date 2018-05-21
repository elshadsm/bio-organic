package com.elshadsm.organic.bio.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.elshadsm.organic.bio.models.Product;
import com.elshadsm.organic.bio.models.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsDao {

    public ProductsDao(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    private ContentResolver contentResolver;

    public boolean isProductFavorite(Product product) {
        try (Cursor cursor = contentResolver.query(
                DatabaseContract.FavoriteEntry.CONTENT_URI, null, null, null, null)) {
            if (cursor == null) {
                return false;
            }
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.FavoriteEntry.COLUMN_PRODUCT_ID));
                if (productId == product.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addProductToFavoriteList(Product product) {
        if (!isProductExist(product)) {
            insertProduct(product);
        }
        ContentValues favoriteContentValues = new ContentValues();
        favoriteContentValues.put(DatabaseContract.FavoriteEntry.COLUMN_PRODUCT_ID, product.getId());
        contentResolver.insert(DatabaseContract.FavoriteEntry.CONTENT_URI, favoriteContentValues);
    }

    public List<Product> getProductList(String category) {
        List<Product> productList = new ArrayList<>();
        String selection = DatabaseContract.ProductEntry.COLUMN_CATEGORY + "=?";
        String[] selectionArgs = {category};
        try (Cursor cursor = contentResolver.query(
                DatabaseContract.ProductEntry.CONTENT_URI, getProductProjection(), selection, selectionArgs, null)) {
            if (cursor == null || cursor.getCount() == 0) {
                return productList;
            }
            cursor.moveToFirst();
            Product product;
            do {
                product = new Product();
                product.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID)));
                product.setCategory(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_CATEGORY)));
                product.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION)));
                product.setImageSrc(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_IMAGE_SRC)));
                product.setInsertionDate(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_INSERTION_DATE)));
                product.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_NAME)));
                product.setPrice(cursor.getFloat(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_PRICE)));
                product.setQuantity(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_QUANTITY)));
                product.setRating(cursor.getFloat(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_RATING)));
                product.setStatus(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_STATUS)));
                product.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_TITLE)));
                product.setReviews(getReviewMap(product.getId()));
                productList.add(product);
            } while (cursor.moveToNext());
        }
        return productList;
    }

    private Map<String, Review> getReviewMap(long productId) {
        Map<String, Review> reviewsMap = new HashMap<>();
        String selection = DatabaseContract.ReviewEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(productId)};
        try (Cursor cursor = contentResolver.query(
                DatabaseContract.ReviewEntry.CONTENT_URI, getReviewProjection(), selection, selectionArgs, null)) {
            if (cursor == null || cursor.getCount() == 0) {
                return reviewsMap;
            }
            cursor.moveToFirst();
            Review review;
            do {
                review = new Review();
                review.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ReviewEntry.COLUMN_REVIEW_ID)));
                review.setDate(cursor.getString(cursor.getColumnIndex(DatabaseContract.ReviewEntry.COLUMN_DATE)));
                review.setReview(cursor.getString(cursor.getColumnIndex(DatabaseContract.ReviewEntry.COLUMN_REVIEW)));
                review.setFullName(cursor.getString(cursor.getColumnIndex(DatabaseContract.ReviewEntry.COLUMN_FULL_NAME)));
                reviewsMap.put(String.valueOf(review.getId()), review);
            } while (cursor.moveToNext());
        }
        return reviewsMap;
    }

    public void insertProductList(List<Product> productList) {
        for (Product product : productList) {
            if (isProductExist(product)) {
                removeReviews(product);
                removeProduct(product);
            }
            insertProduct(product);
        }
    }

    public void insertProduct(Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID, product.getId());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_CATEGORY, product.getCategory());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION, product.getDescription());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_IMAGE_SRC, product.getImageSrc());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_INSERTION_DATE, product.getInsertionDate());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_NAME, product.getName());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_PRICE, product.getPrice());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_QUANTITY, product.getQuantity());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_RATING, product.getRating());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_STATUS, product.getStatus());
        contentValues.put(DatabaseContract.ProductEntry.COLUMN_TITLE, product.getTitle());
        for (Map.Entry<String, Review> entry : product.getReviews().entrySet()) {
            insertReview(entry.getValue(), product);
        }
        contentResolver.insert(DatabaseContract.ProductEntry.CONTENT_URI, contentValues);
    }

    private void insertReview(Review review, Product product) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
        contentValues.put(DatabaseContract.ReviewEntry.COLUMN_DATE, review.getDate());
        contentValues.put(DatabaseContract.ReviewEntry.COLUMN_REVIEW, review.getReview());
        contentValues.put(DatabaseContract.ReviewEntry.COLUMN_FULL_NAME, review.getFullName());
        contentValues.put(DatabaseContract.ReviewEntry.COLUMN_PRODUCT_ID, product.getId());
        contentResolver.insert(DatabaseContract.ReviewEntry.CONTENT_URI, contentValues);
    }

    public void removeProductFromFavoriteList(Product product) {
        String selection = DatabaseContract.FavoriteEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        contentResolver.delete(DatabaseContract.FavoriteEntry.CONTENT_URI, selection, selectionArgs);
    }

    public void addProductToShoppingCart(Product product) {
        if (!isProductExist(product)) {
            insertProduct(product);
        }
        if (!isProductExistInShoppingCart(product)) {
            ContentValues shoppingCartContentValues = new ContentValues();
            shoppingCartContentValues.put(DatabaseContract.ShoppingCartEntry.COLUMN_PRODUCT_ID, product.getId());
            contentResolver.insert(DatabaseContract.ShoppingCartEntry.CONTENT_URI, shoppingCartContentValues);
        }
    }

    public void removeProductFromShoppingCart(Product product) {
        String selection = DatabaseContract.ShoppingCartEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        contentResolver.delete(DatabaseContract.ShoppingCartEntry.CONTENT_URI, selection, selectionArgs);
    }

    public boolean isProductExistInShoppingCart(Product product) {
        String selection = DatabaseContract.ShoppingCartEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        String[] projection = {DatabaseContract.ShoppingCartEntry.COLUMN_PRODUCT_ID};
        try (Cursor cursor = contentResolver.query(
                DatabaseContract.ShoppingCartEntry.CONTENT_URI, projection, selection, selectionArgs, null)) {
            if (cursor == null) {
                return false;
            }
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(0);
                if (productId == product.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isProductExist(Product product) {
        String selection = DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        String[] projection = {DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID};
        try (Cursor cursor = contentResolver.query(
                DatabaseContract.ProductEntry.CONTENT_URI, projection, selection, selectionArgs, null)) {
            if (cursor == null) {
                return false;
            }
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(0);
                if (productId == product.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void removeProduct(Product product) {
        String selection = DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        contentResolver.delete(DatabaseContract.ProductEntry.CONTENT_URI, selection, selectionArgs);
    }

    private void removeReviews(Product product) {
        String selection = DatabaseContract.ReviewEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        contentResolver.delete(DatabaseContract.ReviewEntry.CONTENT_URI, selection, selectionArgs);
    }

    private String[] getProductProjection() {
        return new String[]{
                DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID,
                DatabaseContract.ProductEntry.COLUMN_CATEGORY,
                DatabaseContract.ProductEntry.COLUMN_DESCRIPTION,
                DatabaseContract.ProductEntry.COLUMN_IMAGE_SRC,
                DatabaseContract.ProductEntry.COLUMN_INSERTION_DATE,
                DatabaseContract.ProductEntry.COLUMN_NAME,
                DatabaseContract.ProductEntry.COLUMN_PRICE,
                DatabaseContract.ProductEntry.COLUMN_QUANTITY,
                DatabaseContract.ProductEntry.COLUMN_RATING,
                DatabaseContract.ProductEntry.COLUMN_STATUS,
                DatabaseContract.ProductEntry.COLUMN_TITLE
        };
    }

    private String[] getReviewProjection() {
        return new String[]{
                DatabaseContract.ReviewEntry.COLUMN_REVIEW_ID,
                DatabaseContract.ReviewEntry.COLUMN_DATE,
                DatabaseContract.ReviewEntry.COLUMN_REVIEW,
                DatabaseContract.ReviewEntry.COLUMN_FULL_NAME,
                DatabaseContract.ReviewEntry.COLUMN_PRODUCT_ID
        };
    }

}
