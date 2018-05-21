package com.elshadsm.organic.bio.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.adapters.ProductSearchAdapter;
import com.elshadsm.organic.bio.models.Product;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elshadsm.organic.bio.models.Constants.PRODUCT_EXTRA_NAME;

public class ProductSearchActivity extends AppCompatActivity implements ProductSearchAdapter.ProductSearchAdapterListener {

    @BindView(R.id.product_search_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.product_search_toolbar)
    Toolbar toolbar;

    private static final String SEARCH_VIEW_QUERY_KEY = "query_key";

    ProductSearchAdapter productSearchAdapter;
    SearchView searchView;
    private CharSequence savedQuery;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putCharSequence(SEARCH_VIEW_QUERY_KEY, searchView.getQuery());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        ButterKnife.bind(this);
        applyConfiguration(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setIconified(false);
        registerSearchViewEventHandlers();
        if (!TextUtils.isEmpty(savedQuery)) {
            searchView.setQuery(savedQuery, false);
            productSearchAdapter.setInitialQuery(savedQuery.toString());
            savedQuery = "";
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProductSelected(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(PRODUCT_EXTRA_NAME, product);
        startActivity(intent);
        finish();
    }

    private void applyConfiguration(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productSearchAdapter = new ProductSearchAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(productSearchAdapter);
        if (savedInstanceState != null) {
            savedQuery = savedInstanceState.getCharSequence(SEARCH_VIEW_QUERY_KEY, "");
        }
    }

    private void registerSearchViewEventHandlers() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productSearchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                productSearchAdapter.getFilter().filter(query);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
    }

}
