package com.elshadsm.organic.bio.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.data.DatabaseContract;
import com.elshadsm.organic.bio.models.Product;
import com.elshadsm.organic.bio.models.Review;
import com.squareup.picasso.Picasso;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elshadsm.organic.bio.models.Constants.PRODUCT_EXTRA_NAME;

public class ProductDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProductDetailsActivity.class.getSimpleName();

    @BindView(R.id.product_details_scroll_view)
    ScrollView scrollView;
    @BindView(R.id.product_details_image)
    ImageView image;
    @BindView(R.id.product_details_add_favorites)
    ToggleButton addFavorites;
    @BindView(R.id.product_details_rating_bar)
    RatingBar ratingBar;
    @BindView(R.id.product_details_price)
    TextView price;
    @BindView(R.id.product_details_add_to_shopping_cart)
    ImageView addShoppingCart;
    @BindView(R.id.product_details_title)
    TextView title;
    @BindView(R.id.product_details_description)
    TextView description;
    @BindView(R.id.product_details_review_label)
    TextView reviewLabel;
    @BindView(R.id.product_details_review_container)
    LinearLayout reviewContainer;

    Product product;

    private static final String SCROLL_POSITION_KEY = "scroll_position_key";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(SCROLL_POSITION_KEY, new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (intent.hasExtra(PRODUCT_EXTRA_NAME)) {
            Bundle data = intent.getExtras();
            assert data != null;
            product = data.getParcelable(PRODUCT_EXTRA_NAME);
        }
        applyConfigurations();
        registerEventHandlers();
        restoreViewState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, ProductSearchActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_shopping_cart) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyConfigurations() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(product.getName());
        Picasso.get()
                .load(product.getImageSrc())
                .into(image);
        addFavorites.setChecked(isFavorite());
        ratingBar.setRating(product.getRating());
        price.setText(String.format("%s $", product.getPrice()));
        title.setText(product.getTitle());
        description.setText(product.getDescription());
        setReviews();
    }

    private void registerEventHandlers() {
        addFavorites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                if (isChecked) {
                    addProductToFavoriteList();
                    return;
                }
                removeProductFromFavoriteList();
            }
        });
        addShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToShoppingCart();
            }
        });
    }

    private boolean isFavorite() {
        try (Cursor cursor = getContentResolver().query(
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

    private void addProductToFavoriteList() {
        if (!isProductExistInDb()) {
            insertProductToDb();
        }
        ContentValues favoriteContentValues = new ContentValues();
        favoriteContentValues.put(DatabaseContract.FavoriteEntry.COLUMN_PRODUCT_ID, product.getId());
        getContentResolver().insert(DatabaseContract.FavoriteEntry.CONTENT_URI, favoriteContentValues);
    }

    private boolean isProductExistInDb() {
        String selection = DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        String[] projection = {DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID};
        try (Cursor cursor = getContentResolver().query(
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

    private void insertProductToDb() {
        ContentValues productContentValues = new ContentValues();
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_PRODUCT_ID, product.getId());
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_CATEGORY, product.getCategory());
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION, product.getDescription());
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_IMAGE_SRC, product.getImageSrc());
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_INSERTION_DATE, product.getInsertionDate());
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_NAME, product.getName());
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_PRICE, product.getPrice());
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_RATING, product.getRating());
        productContentValues.put(DatabaseContract.ProductEntry.COLUMN_TITLE, product.getTitle());
        getContentResolver().insert(DatabaseContract.ProductEntry.CONTENT_URI, productContentValues);
    }

    private void removeProductFromFavoriteList() {
        String selection = DatabaseContract.FavoriteEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        getContentResolver().delete(DatabaseContract.FavoriteEntry.CONTENT_URI, selection, selectionArgs);
    }

    private void addProductToShoppingCart() {
        if (!isProductExistInDb()) {
            insertProductToDb();
        }
        if (!isProductExistInShoppingCart()) {
            ContentValues shoppingCartContentValues = new ContentValues();
            shoppingCartContentValues.put(DatabaseContract.ShoppingCartEntry.COLUMN_PRODUCT_ID, product.getId());
            getContentResolver().insert(DatabaseContract.ShoppingCartEntry.CONTENT_URI, shoppingCartContentValues);
        }
    }

    private boolean isProductExistInShoppingCart() {
        String selection = DatabaseContract.ShoppingCartEntry.COLUMN_PRODUCT_ID + "=?";
        String[] selectionArgs = {String.valueOf(product.getId())};
        String[] projection = {DatabaseContract.ShoppingCartEntry.COLUMN_PRODUCT_ID};
        try (Cursor cursor = getContentResolver().query(
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

    private void setReviews() {
        if (product.getReviews() == null || product.getReviews().size() == 0) {
            reviewContainer.setVisibility(View.GONE);
            reviewLabel.setVisibility(View.GONE);
            Log.e(LOG_TAG, "review list is empty!");
        } else {
            for (Map.Entry<String, Review> entry : product.getReviews().entrySet()) {
                reviewContainer.addView(getReviewView(entry.getValue()));
            }
        }
    }

    private View getReviewView(final Review review) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.review_list_item, reviewContainer, false);
        TextView contentTextView = view.findViewById(R.id.review_content_text);
        TextView authorTextView = view.findViewById(R.id.review_author_text);
        contentTextView.setText(review.getReview());
        authorTextView.setText(review.getFullName());
        return view;
    }

    public void restoreViewState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        final int[] position = savedInstanceState.getIntArray(SCROLL_POSITION_KEY);
        if (position == null) {
            return;
        }
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.scrollTo(position[0], position[1]);
            }
        });
    }
}
