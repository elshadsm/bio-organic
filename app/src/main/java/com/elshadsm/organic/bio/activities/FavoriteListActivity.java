package com.elshadsm.organic.bio.activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import com.elshadsm.organic.bio.data.ProductsDao;
import com.elshadsm.organic.bio.models.Product;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteListActivity extends AppCompatActivity {

    @BindView(R.id.favorite_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.favorite_list_empty_view)
    TextView emptyView;

    private static final String SAVED_LAYOUT_MANAGER_KEY = "saved_layout_manager";

    private ProductsDao productsDao;
    private FavoriteListAdapter favoriteListAdapter;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_LAYOUT_MANAGER_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);
        ButterKnife.bind(this);
        applyConfiguration();
        restoreViewState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Product> productList = productsDao.getFavoriteProducts();
        favoriteListAdapter.setData(productList);
        if (productList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search: {
                Intent intent = new Intent(this, ProductSearchActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_shopping_cart: {
                Intent intent = new Intent(this, ShoppingCartActivity.class);
                startActivity(intent);
                return true;
            }
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyConfiguration() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productsDao = new ProductsDao(getContentResolver());
        favoriteListAdapter = new FavoriteListAdapter();
        recyclerView.setAdapter(favoriteListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void restoreViewState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER_KEY);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }
}
