package com.elshadsm.organic.bio.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.elshadsm.organic.bio.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.elshadsm.organic.bio.models.Constants.*;

public class AccountRegistrationActivity extends AppCompatActivity {

    @BindView(R.id.name_input)
    EditText nameInput;
    @BindView(R.id.name_message)
    TextView nameMessage;
    @BindView(R.id.surname_input)
    EditText surnameInput;
    @BindView(R.id.surname_message)
    TextView surnameMessage;
    @BindView(R.id.email_input)
    EditText emailInput;
    @BindView(R.id.email_message)
    TextView emailMessage;
    @BindView(R.id.country_input)
    EditText countryInput;
    @BindView(R.id.country_message)
    TextView countryMessage;
    @BindView(R.id.city_input)
    EditText cityInput;
    @BindView(R.id.city_message)
    TextView cityMessage;
    @BindView(R.id.state_input)
    EditText stateInput;
    @BindView(R.id.state_message)
    TextView stateMessage;
    @BindView(R.id.postal_code_input)
    EditText postalCodeInput;
    @BindView(R.id.postal_code_message)
    TextView postalCodeMessage;
    @BindView(R.id.image_icon)
    ImageView icon;
    @BindView(R.id.start_button)
    Button button;

    private static int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2;

    private Bitmap bitmapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_registration);
        ButterKnife.bind(this);
        registerEventHandlers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bitmapImage = BitmapFactory.decodeFile(picturePath);
            icon.setImageBitmap(bitmapImage);
        }
    }

    private void registerEventHandlers() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveUserData();
                }
            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showPhoneStatePermission()) {
                    pickImage();
                }
            }
        });
    }

    private boolean showPhoneStatePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showExplanation(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toast.makeText(AccountRegistrationActivity.this,
                            getResources().getString(R.string.permission_denied_message),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showExplanation(final String permission, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.permission_needed_message))
                .setMessage(getResources().getString(R.string.rationale_message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    private void pickImage() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private boolean validateInputs() {
        return validateName() &
                validateSurname() &
                validateEmail() &
                validateCountry() &
                validateCity() &
                validateState() &
                validatePostalCode();
    }

    private boolean validateName() {
        if (TextUtils.isEmpty(nameInput.getText())) {
            nameMessage.setText(R.string.account_reg_form_name_message);
            nameMessage.setVisibility(View.VISIBLE);
            return false;
        }
        nameMessage.setVisibility(View.GONE);
        return true;
    }

    private boolean validateSurname() {
        if (TextUtils.isEmpty(surnameInput.getText())) {
            surnameMessage.setText(R.string.account_reg_form_surname_message);
            surnameMessage.setVisibility(View.VISIBLE);
            return false;
        }
        surnameMessage.setVisibility(View.GONE);
        return true;
    }

    private boolean validateEmail() {
        if (TextUtils.isEmpty(emailInput.getText())) {
            emailMessage.setText(R.string.account_reg_form_email_message);
            emailMessage.setVisibility(View.VISIBLE);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText()).matches()) {
            emailMessage.setText(R.string.account_reg_form_email_message_correct);
            emailMessage.setVisibility(View.VISIBLE);
            return false;
        }
        emailMessage.setVisibility(View.GONE);
        return true;
    }

    private boolean validateCountry() {
        if (TextUtils.isEmpty(countryInput.getText())) {
            countryMessage.setText(R.string.account_reg_form_country_message);
            countryMessage.setVisibility(View.VISIBLE);
            return false;
        }
        countryMessage.setVisibility(View.GONE);
        return true;
    }

    private boolean validateCity() {
        if (TextUtils.isEmpty(cityInput.getText())) {
            cityMessage.setText(R.string.account_reg_form_city_message);
            cityMessage.setVisibility(View.VISIBLE);
            return false;
        }
        cityMessage.setVisibility(View.GONE);
        return true;
    }

    private boolean validateState() {
        if (TextUtils.isEmpty(stateInput.getText())) {
            stateMessage.setText(R.string.account_reg_form_state_message);
            stateMessage.setVisibility(View.VISIBLE);
            return false;
        }
        stateMessage.setVisibility(View.GONE);
        return true;
    }

    private boolean validatePostalCode() {
        if (TextUtils.isEmpty(postalCodeInput.getText())) {
            postalCodeMessage.setText(R.string.account_reg_form_postal_code_message);
            postalCodeMessage.setVisibility(View.VISIBLE);
            return false;
        }
        postalCodeMessage.setVisibility(View.GONE);
        return true;
    }

    private void saveUserData() {
        ProgressDialog dialog = showProgressDialog();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(USER_PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME_PREF_NAME, nameInput.getText().toString());
        editor.putString(USER_SURNAME_PREF_NAME, surnameInput.getText().toString());
        editor.putString(USER_EMAIL_PREF_NAME, emailInput.getText().toString());
        editor.putString(USER_COUNTRY_PREF_NAME, countryInput.getText().toString());
        editor.putString(USER_CITY_PREF_NAME, cityInput.getText().toString());
        editor.putString(USER_STATE_PREF_NAME, stateInput.getText().toString());
        editor.putString(USER_POSTAL_CODE_PREF_NAME, postalCodeInput.getText().toString());
        if (bitmapImage != null) {
            editor.putString(USER_IMAGE_PATH_PREF_NAME, saveToInternalStorage());
        }
        editor.putBoolean(USER_SIGNED_UP_PREF_NAME, true);
        editor.apply();
        dialog.dismiss();
        finish();
    }

    private String saveToInternalStorage() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, USER_PROFILE_IMAGE_FILE);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
        return directory.getAbsolutePath();
    }

    private ProgressDialog showProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getResources().getString(R.string.please_wait_message));
        dialog.show();
        return dialog;
    }

}
