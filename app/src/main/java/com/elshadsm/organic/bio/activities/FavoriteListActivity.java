package com.elshadsm.organic.bio.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.adapters.FavoriteListAdapter;
import com.elshadsm.organic.bio.data.DatabaseContract;
import com.elshadsm.organic.bio.models.Product;
import com.elshadsm.organic.bio.models.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteListActivity extends AppCompatActivity {

    @BindView(R.id.favorite_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.favorite_list_empty_view)
    TextView emptyView;

    private FavoriteListAdapter favoriteListAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        ButterKnife.bind(this);
        applyConfiguration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProducts();
        if (productList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        favoriteListAdapter.setData(productList);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shopping_cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.shopping_cart_action_search) {
            Intent intent = new Intent(this, ProductSearchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyConfiguration() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        favoriteListAdapter = new FavoriteListAdapter();
        recyclerView.setAdapter(favoriteListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void fetchProducts() {
        productList = new ArrayList<>();
        try (Cursor cursor = getContentResolver().query(
                DatabaseContract.ProductsInFavoriteListEntry.CONTENT_URI,
                null, null, null, null)) {
            if (cursor == null || cursor.getCount() == 0) {
                return;
            }
            cursor.moveToFirst();
            Product product;
            do {
                product = new Product();
                product.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID)));
                product.setCategory(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_CATEGORY)));
                product.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION)));
                product.setImageSrc(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_IMAGE_SRC)));
                product.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_NAME)));
                product.setPrice(cursor.getFloat(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_PRICE)));
                product.setQuantity(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_QUANTITY)));
                product.setRating(cursor.getFloat(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_RATING)));
                product.setStatus(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_STATUS)));
                product.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseContract.ProductEntry.COLUMN_TITLE)));
                product.setReviews(fetchReviews(product.getId()));
                productList.add(product);
            } while (cursor.moveToNext());
        }
    }

    private Map<String, Review> fetchReviews(long productId) {
        Map<String, Review> reviewsMap = new HashMap<>();
        String selection = DatabaseContract.ReviewEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(productId)};
        try (Cursor cursor = getContentResolver().query(
                DatabaseContract.ReviewEntry.CONTENT_URI, getReviewsProjection(), selection, selectionArgs, null)) {
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

    private String[] getReviewsProjection() {
        return new String[]{
                DatabaseContract.ReviewEntry.COLUMN_REVIEW_ID,
                DatabaseContract.ReviewEntry.COLUMN_DATE,
                DatabaseContract.ReviewEntry.COLUMN_REVIEW,
                DatabaseContract.ReviewEntry.COLUMN_FULL_NAME,
                DatabaseContract.ReviewEntry.COLUMN_PRODUCT_ID
        };
    }
}
