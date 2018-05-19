package com.elshadsm.organic.bio.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.elshadsm.organic.bio.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elshadsm.organic.bio.models.Constants.*;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.account_full_name)
    TextView fullName;
    @BindView(R.id.account_email)
    TextView email;
    @BindView(R.id.account_profile_image)
    ImageView profileImage;
    @BindView(R.id.account_country_city)
    TextView countryCity;
    @BindView(R.id.account_state)
    TextView state;
    @BindView(R.id.account_postal_code)
    TextView postalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        applyConfiguration();
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

    private void applyConfiguration() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(USER_PREF_NAME, 0);
        String name = sharedPreferences.getString(USER_NAME_PREF_NAME, "");
        String surname = sharedPreferences.getString(USER_SURNAME_PREF_NAME, "");
        fullName.setText(String.format("%s %s", name, surname));
        email.setText(sharedPreferences.getString(USER_EMAIL_PREF_NAME, ""));
        String imagePath = sharedPreferences.getString(USER_IMAGE_PATH_PREF_NAME, "");
        if (!TextUtils.isEmpty(imagePath)) {
            loadImageFromStorage(imagePath, profileImage);
        } else {
            profileImage.setImageResource(R.mipmap.ic_launcher_round);
        }
        String country = sharedPreferences.getString(USER_COUNTRY_PREF_NAME, "");
        String city = sharedPreferences.getString(USER_CITY_PREF_NAME, "");
        countryCity.setText(String.format("%s, %s", country, city));
        state.setText(sharedPreferences.getString(USER_STATE_PREF_NAME, ""));
        postalCode.setText(sharedPreferences.getString(USER_POSTAL_CODE_PREF_NAME, ""));
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
}
