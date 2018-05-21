package com.elshadsm.organic.bio.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.adapters.ProductListAdapter;
import com.elshadsm.organic.bio.data.ProductsDao;
import com.elshadsm.organic.bio.models.CategoryView;
import com.elshadsm.organic.bio.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elshadsm.organic.bio.models.Constants.CATEGORY_EXTRA_NAME;
import static com.elshadsm.organic.bio.models.Constants.FIREBASE_PRODUCTS_CATEGORY_COLUMN;

public class ProductListActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProductListActivity.class.getSimpleName();

    @BindView(R.id.activity_product_list_toolbar)
    Toolbar toolbar;
    @BindView(R.id.sort_action_layout)
    LinearLayout sortActionLayout;
    @BindView(R.id.filter_action_layout)
    LinearLayout filterActionLayout;
    @BindView(R.id.orientation_action_layout)
    LinearLayout orientationActionLayout;
    @BindView(R.id.sort_action_view)
    ConstraintLayout sortActionView;
    @BindView(R.id.filter_action_view)
    ConstraintLayout filterActionView;
    @BindView(R.id.product_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.product_list_empty_view)
    TextView emptyView;
    @BindView(R.id.lowest_price_check_box)
    CheckBox lowestPriceCheckBox;
    @BindView(R.id.highest_price_check_box)
    CheckBox highestPriceCheckBox;
    @BindView(R.id.newest_check_box)
    CheckBox newestCheckBox;
    @BindView(R.id.sort_action_close_button)
    Button sortActionCloseButton;
    @BindView(R.id.filter_action_close_button)
    Button filterActionCloseButton;
    @BindView(R.id.filter_action_done_button)
    Button filterActionDoneButton;
    @BindView(R.id.filter_from_amount)
    EditText fromAmountEditText;
    @BindView(R.id.filter_to_amount)
    EditText toAmountEditText;
    @BindView(R.id.orientation_action_icon)
    ImageView orientationActionIcon;

    DatabaseReference databaseReference;
    private ProductListAdapter productListAdapter;
    private CategoryView category;
    private ProductsDao productsDao;
    private boolean isSortActionDown = false;
    private boolean isFilterActionDown = false;
    private boolean isViewGridLayout = true;

    private final String SORT_TYPE_LOWEST_PRICE = "lowest-price";
    private final String SORT_TYPE_HIGHEST_PRICE = "highest-price";
    private final String SORT_TYPE_NEWEST = "newest";

    private final String ORIENTATION_TYPE_GRID = "grid";
    private final String ORIENTATION_TYPE_LINEAR = "linear";

    private final String SLIDE_ACTION_TYPE_SORT = "sort";
    private final String SLIDE_ACTION_TYPE_FILTER = "filter";

    private static final String SAVED_LAYOUT_MANAGER_KEY = "saved_layout_manager";
    private static final String SORT_ACTION_VISIBLE_KEY = "sort_action_visible";
    private static final String FILTER_ACTION_VISIBLE_KEY = "filter_action_visible";
    private static final String ORIENTATION_ACTION_VISIBLE_KEY = "orientation_action_visible";

    List<Product> productList;
    private String sortType;
    private Bundle savedInstanceState;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_LAYOUT_MANAGER_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putBoolean(SORT_ACTION_VISIBLE_KEY, isSortActionDown);
        outState.putBoolean(FILTER_ACTION_VISIBLE_KEY, isFilterActionDown);
        outState.putBoolean(ORIENTATION_ACTION_VISIBLE_KEY, isViewGridLayout);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        ButterKnife.bind(this);
        this.savedInstanceState = savedInstanceState;
        setSupportActionBar(toolbar);
        applyConfiguration();
        registerActionEventHandlers();
        registerSortActionEventHandlers();
        registerFilterActionEventHandlers();
        initData();
        applyActionBarConfiguration();
        applySavedInstanceState();
    }

    private void applySavedInstanceState() {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(SORT_ACTION_VISIBLE_KEY, false)) {
                sortActionView.setVisibility(View.VISIBLE);
                enableSortActionView(true);
                isSortActionDown = true;
            } else if (savedInstanceState.getBoolean(FILTER_ACTION_VISIBLE_KEY, false)) {
                filterActionView.setVisibility(View.VISIBLE);
                enableFilterActionView(true);
                isFilterActionDown = true;
            }
            if (!savedInstanceState.getBoolean(ORIENTATION_ACTION_VISIBLE_KEY, true)) {
                setLayoutManager(ORIENTATION_TYPE_LINEAR);
                orientationActionIcon.setImageResource(R.drawable.ic_grid_view);
                isViewGridLayout = !isViewGridLayout;
            }
        }
    }

    private void applyConfiguration() {
        Intent intent = getIntent();
        if (intent.hasExtra(CATEGORY_EXTRA_NAME)) {
            Bundle data = intent.getExtras();
            assert data != null;
            category = data.getParcelable(CATEGORY_EXTRA_NAME);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("products");
        productListAdapter = new ProductListAdapter();
        setLayoutManager(ORIENTATION_TYPE_GRID);
        recyclerView.setAdapter(productListAdapter);
        productsDao = new ProductsDao(getContentResolver());
        enableSortActionView(false);
        enableFilterActionView(false);
    }

    private void registerActionEventHandlers() {
        sortActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFilterActionDown) {
                    closeFilterActionView();
                }
                if (isSortActionDown) {
                    closeSortActionView();
                    return;
                }
                openSortActionView();
            }
        });
        filterActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSortActionDown) {
                    closeSortActionView();
                }
                if (isFilterActionDown) {
                    closeFilterActionView();
                    return;
                }
                openFilterActionView();
            }
        });
        orientationActionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isViewGridLayout) {
                    setLayoutManager(ORIENTATION_TYPE_LINEAR);
                    orientationActionIcon.setImageResource(R.drawable.ic_grid_view);
                } else {
                    setLayoutManager(ORIENTATION_TYPE_GRID);
                    orientationActionIcon.setImageResource(R.drawable.ic_linear_view);
                }
                isViewGridLayout = !isViewGridLayout;
            }
        });
    }

    private void registerSortActionEventHandlers() {
        sortActionCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSortActionView();
            }
        });
        lowestPriceCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lowestPriceCheckBox.isChecked()) {
                    highestPriceCheckBox.setChecked(false);
                    newestCheckBox.setChecked(false);
                    sortProductListView(SORT_TYPE_LOWEST_PRICE);
                    closeSortActionView();
                    return;
                }
                resetSortActions();
            }
        });
        highestPriceCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (highestPriceCheckBox.isChecked()) {
                    lowestPriceCheckBox.setChecked(false);
                    newestCheckBox.setChecked(false);
                    sortProductListView(SORT_TYPE_HIGHEST_PRICE);
                    closeSortActionView();
                    return;
                }
                resetSortActions();
            }
        });
        newestCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newestCheckBox.isChecked()) {
                    highestPriceCheckBox.setChecked(false);
                    lowestPriceCheckBox.setChecked(false);
                    sortProductListView(SORT_TYPE_NEWEST);
                    closeSortActionView();
                    return;
                }
                resetSortActions();
            }
        });
    }

    private void registerFilterActionEventHandlers() {
        filterActionCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilterActionView();
            }
        });

        filterActionDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query query = databaseReference.orderByChild(FIREBASE_PRODUCTS_CATEGORY_COLUMN);
                fetchData(query);
                closeFilterActionView();
            }
        });
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
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void slideActionViewUp(View view, String type, int delta) {
        view.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                -view.getHeight() - delta);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        setSlideActionViewUpListeners(animate, type);
        view.startAnimation(animate);
    }

    public void slideActionViewDown(View view, String type) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -view.getHeight(),                 // fromYDelta
                0); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        setSlideActionViewDownListeners(animate, type);
        view.startAnimation(animate);
    }

    private void setSlideActionViewUpListeners(TranslateAnimation animate, final String type) {
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (type.equals(SLIDE_ACTION_TYPE_SORT)) {
                    enableSortActionView(false);
                    return;
                }
                enableFilterActionView(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void setSlideActionViewDownListeners(TranslateAnimation animate, final String type) {
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (type.equals("sort")) {
                    enableSortActionView(true);
                    return;
                }
                enableFilterActionView(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void openSortActionView() {
        slideActionViewDown(sortActionView, SLIDE_ACTION_TYPE_SORT);
        isSortActionDown = !isSortActionDown;
    }

    private void closeSortActionView() {
        slideActionViewUp(sortActionView, SLIDE_ACTION_TYPE_SORT, 500);
        isSortActionDown = !isSortActionDown;
    }

    private void openFilterActionView() {
        slideActionViewDown(filterActionView, SLIDE_ACTION_TYPE_FILTER);
        isFilterActionDown = !isFilterActionDown;
    }

    private void closeFilterActionView() {
        int delta = (fromAmountEditText.isFocused() || toAmountEditText.isFocused()) ? 900 : 500;
        removeFocusEditTexts();
        slideActionViewUp(filterActionView, SLIDE_ACTION_TYPE_FILTER, delta);
        isFilterActionDown = !isFilterActionDown;
    }

    private void removeFocusEditTexts() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initData() {
        if (productList != null) {
            return;
        }
        productList = productsDao.getProductList(category.getCategory());
        renderProductList();
        Query query = databaseReference.orderByChild(FIREBASE_PRODUCTS_CATEGORY_COLUMN);
        fetchData(query);
    }

    private void applyActionBarConfiguration() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(category.getTitle());
    }

    private void fetchData(Query query) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    storeProductList(dataSnapshot);
                    renderProductList();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void storeProductList(DataSnapshot dataSnapshot) {
        productList = new ArrayList<>();
        for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
            Product product = productSnapshot.getValue(Product.class);
            assert product != null;
            if (product.getCategory().equals(category.getCategory())) {
                productList.add(product);
            }
        }
        productsDao.insertProductList(productList);
    }

    private void renderProductList() {
        List<Product> processedProductList = processProductList();
        productListAdapter.setData(processedProductList);
        if (processedProductList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        restoreViewState();
    }

    private List<Product> processProductList() {
        if (productList == null || productList.isEmpty()) {
            return new ArrayList<>();
        }
        List<Product> processedProductList = new ArrayList<>();
        for (Product product : productList) {
            if (validateFilters(product)) {
                processedProductList.add(product);
            }
        }
        sortProductList(processedProductList);
        return processedProductList;
    }

    private void sortProductListView(String type) {
        sortType = type;
        renderProductList();
    }

    private void sortProductList(List<Product> processedProductList) {
        if (sortType == null) {
            return;
        }
        @SuppressLint("SimpleDateFormat")
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Collections.sort(processedProductList, new Comparator<Product>() {
            public int compare(Product one, Product other) {
                return compareProducts(one, other, format);
            }
        });
        sortType = null;
    }

    private int compareProducts(Product one, Product other, SimpleDateFormat format) {
        switch (sortType) {
            case SORT_TYPE_LOWEST_PRICE:
                return Float.compare(one.getPrice(), other.getPrice());
            case SORT_TYPE_HIGHEST_PRICE:
                return Float.compare(other.getPrice(), one.getPrice());
            default:
                try {
                    return format.parse(other.getInsertionDate()).compareTo(format.parse(one.getInsertionDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
        }
    }

    private void resetSortActions() {
        boolean someIsChecked = lowestPriceCheckBox.isChecked() ||
                highestPriceCheckBox.isChecked() ||
                newestCheckBox.isChecked();
        if (!someIsChecked) {
            Query query = databaseReference.orderByChild(FIREBASE_PRODUCTS_CATEGORY_COLUMN);
            fetchData(query);
        }
    }

    private boolean validateFilters(Product product) {
        if (!product.getCategory().equals(category.getCategory())) {
            return false;
        }
        if (!TextUtils.isEmpty(fromAmountEditText.getText()) &&
                Float.compare(product.getPrice(), Float.valueOf(fromAmountEditText.getText().toString())) < 0) {
            return false;
        }
        if (!TextUtils.isEmpty(toAmountEditText.getText()) &&
                Float.compare(product.getPrice(), Float.valueOf(toAmountEditText.getText().toString())) > 0) {
            return false;
        }
        return true;
    }

    private void enableSortActionView(boolean enable) {
        for (int i = 0; i < sortActionView.getChildCount(); i++) {
            View child = sortActionView.getChildAt(i);
            child.setEnabled(enable);
            child.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        }
        sortActionView.setEnabled(enable);
        sortActionView.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }


    private void enableFilterActionView(boolean enable) {
        for (int i = 0; i < filterActionView.getChildCount(); i++) {
            View child = filterActionView.getChildAt(i);
            child.setEnabled(enable);
            child.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
        }
        filterActionView.setEnabled(enable);
        filterActionView.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    private void setLayoutManager(String type) {
        if (type.equals(ORIENTATION_TYPE_GRID)) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getGridLayoutManagerSpan());
            recyclerView.setLayoutManager(gridLayoutManager);
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private int getGridLayoutManagerSpan() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return getResources().getInteger(R.integer.grid_view_landscape_column_number);
        }
        return getResources().getInteger(R.integer.grid_view_portrait_column_number);
    }

    public void restoreViewState() {
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER_KEY);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

}
