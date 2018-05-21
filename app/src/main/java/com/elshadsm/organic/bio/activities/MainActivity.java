package com.elshadsm.organic.bio.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.elshadsm.organic.bio.R;
import com.elshadsm.organic.bio.adapters.CategoryViewAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elshadsm.organic.bio.models.Constants.FIREBASE_PRODUCTS_REFERENCE;
import static com.elshadsm.organic.bio.models.Constants.USER_EMAIL_PREF_NAME;
import static com.elshadsm.organic.bio.models.Constants.USER_IMAGE_PATH_PREF_NAME;
import static com.elshadsm.organic.bio.models.Constants.USER_NAME_PREF_NAME;
import static com.elshadsm.organic.bio.models.Constants.USER_PREF_NAME;
import static com.elshadsm.organic.bio.models.Constants.USER_PROFILE_IMAGE_FILE;
import static com.elshadsm.organic.bio.models.Constants.USER_SIGNED_UP_PREF_NAME;
import static com.elshadsm.organic.bio.models.Constants.USER_SURNAME_PREF_NAME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.category_views_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    MenuItem signMenuItem;

    private static final String SAVED_LAYOUT_MANAGER_KEY = "saved_layout_manager";

    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private boolean isLoggedIn = false;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SAVED_LAYOUT_MANAGER_KEY, recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        applyConfiguration();
        registerEventHandlers();
        restoreViewState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyNavigationConfiguration();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_shopping_cart) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_my_account:
                if (isLoggedIn) {
                    intent = new Intent(this, AccountActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(this, AccountRegistrationActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.nav_shopping_cart:
                intent = new Intent(this, ShoppingCartActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_my_favorites:
                intent = new Intent(this, FavoriteListActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_sign:
                if (isLoggedIn) {
                    logOut();
                } else {
                    intent = new Intent(this, AccountRegistrationActivity.class);
                    startActivity(intent);
                }
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void applyConfiguration() {
        sharedPreferences = getApplicationContext().getSharedPreferences(USER_PREF_NAME, 0);
        databaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_PRODUCTS_REFERENCE);
        setSupportActionBar(toolbar);
        recyclerView.setAdapter(new CategoryViewAdapter(this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        signMenuItem = menu.findItem(R.id.nav_sign);
    }

    private void registerEventHandlers() {
        final Context context = this;
        findViewById(R.id.search_input).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    Intent intent = new Intent(context, ProductSearchActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void applyNavigationConfiguration() {
        isLoggedIn = sharedPreferences.getBoolean(USER_SIGNED_UP_PREF_NAME, false);
        if (isLoggedIn) {
            applyLoggedInConfiguration();
            return;
        }
        applySignUpConfiguration();
    }

    private void applyLoggedInConfiguration() {
        signMenuItem.setTitle(R.string.navigation_drawer_logout);
        signMenuItem.setIcon(R.drawable.ic_menu_logout);
        View header = navigationView.getHeaderView(0);
        ImageView profileImage = header.findViewById(R.id.navigation_profile_image);
        TextView fullName = header.findViewById(R.id.navigation_profile_full_name);
        TextView email = header.findViewById(R.id.navigation_profile_email);
        String name = sharedPreferences.getString(USER_NAME_PREF_NAME,
                getResources().getString(R.string.drawer_menu_name_default_value));
        String surname = sharedPreferences.getString(USER_SURNAME_PREF_NAME,
                getResources().getString(R.string.drawer_menu_surname_default_value));
        fullName.setText(String.format("%s %s", name, surname));
        email.setText(sharedPreferences.getString(USER_EMAIL_PREF_NAME,
                getResources().getString(R.string.drawer_menu_email_default_value)));
        String imagePath = sharedPreferences.getString(USER_IMAGE_PATH_PREF_NAME, "");
        if (!TextUtils.isEmpty(imagePath)) {
            loadImageFromStorage(imagePath, profileImage);
        } else {
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    private void applySignUpConfiguration() {
        signMenuItem.setTitle(R.string.navigation_drawer_sign_up);
        signMenuItem.setIcon(R.drawable.ic_menu_sign_up);
        View header = navigationView.getHeaderView(0);
        ImageView profileImage = header.findViewById(R.id.navigation_profile_image);
        TextView fullName = header.findViewById(R.id.navigation_profile_full_name);
        TextView email = header.findViewById(R.id.navigation_profile_email);
        fullName.setText(getResources().getString(R.string.drawer_menu_full_name_default_value));
        email.setText(getResources().getString(R.string.drawer_menu_email_default_value));
        profileImage.setImageResource(R.mipmap.ic_launcher_round);
    }

    private void logOut() {
        editor = sharedPreferences.edit();
        String imagePath = sharedPreferences.getString(USER_IMAGE_PATH_PREF_NAME, "");
        if (!TextUtils.isEmpty(imagePath)) {
            deleteImageInStorage(imagePath);
        }
        editor.clear();
        editor.apply();
        applyNavigationConfiguration();
    }

    private void loadImageFromStorage(String path, ImageView image) {
        try {
            File file = new File(path, USER_PROFILE_IMAGE_FILE);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            image.setImageBitmap(bitmap);
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            Crashlytics.logException(exception);
        }
    }

    private void deleteImageInStorage(String path) {
        try {
            File file = new File(path, USER_PROFILE_IMAGE_FILE);
            if (file.exists()) {
                if (!file.delete()) {
                    Log.e(LOG_TAG, getResources().getString(R.string.image_delete_error_message));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            Crashlytics.logException(exception);
        }
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_share_subject));
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_share_text));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void restoreViewState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(SAVED_LAYOUT_MANAGER_KEY);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

}
