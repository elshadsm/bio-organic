package com.elshadsm.organic.bio.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

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

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        applyConfiguration();
    }

    private void applyConfiguration() {
        sharedPreferences = getApplicationContext().getSharedPreferences(USER_PREF_NAME, 0);
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
        }
    }
}
