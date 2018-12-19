package edu.ktu.lab.labaratorinis1.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import edu.ktu.lab.labaratorinis1.R;

public class SettingsActivity extends AppCompatActivity {

    private static final String SETTINGS_FILE_NAME = "game-settings";
    public static final String NICKNAME_KEY = "nickname";
    public static final String AGE_KEY = "age";
    public static final String SOUND_ENABLED_KEY = "soundEnabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setUpActionBar();
        loadSettingsFromPreferences();

        EditText nicknameText = findViewById(R.id.nicknameInput);
        nicknameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence usernameChars, int start, int before, int count) {
                TextInputLayout usernameTextLayout = findViewById(R.id.nicknameWrapper);
                String username = String.valueOf(usernameChars);
                if (isUsernameShorterThanMin(username)) {
                    disableSaveButton();
                    usernameTextLayout.setError(getResources().getString(R.string.tooShortUsernameMsg));
                    return;
                }

                if (isUsernameHigherThanMax(username)) {
                    disableSaveButton();
                    usernameTextLayout.setError(getResources().getString(R.string.tooLongUsernameMsg));
                    return;
                }

                enableSaveButton();
                usernameTextLayout.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText ageText = findViewById(R.id.ageInput);
        ageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence ageChars, int i, int i1, int i2) {
                TextInputLayout ageTextLayout = findViewById(R.id.ageWrapper);
                String ageText = String.valueOf(ageChars);
                if (!StringUtils.isNumeric(ageText)) {
                    disableSaveButton();
                    ageTextLayout.setError(getResources().getString(R.string.incorrectFormatAgeMsg));
                    return;
                }
                Integer age = Integer.valueOf(ageText);
                if (isAgeLowerThanMin(age)) {
                    disableSaveButton();
                    ageTextLayout.setError(getResources().getString(R.string.tooLowAgeMsg));
                    return;
                }
                if (isAgeHigherThanMax(age)) {
                    disableSaveButton();
                    ageTextLayout.setError(getResources().getString(R.string.tooHighAgeMsg));
                    return;
                }

                enableSaveButton();
                ageTextLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button saveButton = findViewById(R.id.settingsSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClick(view);
            }
        });
    }

    private boolean isUsernameHigherThanMax(String username) {
        return (username.length() > getResources().getInteger(R.integer.usernameMaxLength));
    }

    private boolean isUsernameShorterThanMin(String username) {
        return (username.length() < getResources().getInteger(R.integer.usernameMinLength));
    }

    private boolean isAgeHigherThanMax(Integer age) {
        return (age > getResources().getInteger(R.integer.ageMax));
    }

    private boolean isAgeLowerThanMin(Integer age) {
        return (age < getResources().getInteger(R.integer.ageMin));
    }

    private void disableSaveButton() {
        Button saveButton = findViewById(R.id.settingsSaveButton);
        saveButton.setClickable(false);
        saveButton.setEnabled(false);
        saveButton.setAlpha(.5f);
    }

    private void enableSaveButton() {
        Button saveButton = findViewById(R.id.settingsSaveButton);
        saveButton.setClickable(true);
        saveButton.setEnabled(true);
        saveButton.setAlpha(1f);
    }

    public void showSuccessfulSaveToast() {
        Toast toast = Toast.makeText(
                getApplicationContext(),
                getResources().getText(R.string.onSuccessSettingsSave),
                Toast.LENGTH_SHORT
        );
        toast.show();
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    public void onSaveButtonClick(View view) {
        if (view.getId() == R.id.settingsSaveButton) {
            saveSettings();
            showSuccessfulSaveToast();
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            String settingsBarTitle = getResources().getString(R.string.settingsBarTitle);
            actionBar.setTitle(settingsBarTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadSettingsFromPreferences() {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            EditText nicknameText = findViewById(R.id.nicknameInput);
            if (nicknameText != null) {
                String nickname = preferences.getString(NICKNAME_KEY, StringUtils.EMPTY);
                nicknameText.setText(nickname);
                TextInputLayout usernameTextLayout = findViewById(R.id.nicknameWrapper);
                if (isUsernameHigherThanMax(nickname)) {
                    disableSaveButton();
                    usernameTextLayout.setError(getResources().getString(R.string.tooLongUsernameMsg));
                } else if (isUsernameShorterThanMin(nickname)) {
                    disableSaveButton();
                    usernameTextLayout.setError(getResources().getString(R.string.tooShortUsernameMsg));
                } else {
                    enableSaveButton();
                    usernameTextLayout.setError(null);
                }
            }

            EditText ageText = findViewById(R.id.ageInput);
            if (ageText != null) {
                Integer age = preferences.getInt(AGE_KEY, getResources().getInteger(R.integer.defaultAge));
                ageText.setText(String.format("%s", age));
                TextInputLayout ageTextLayout = findViewById(R.id.ageWrapper);
                if (!StringUtils.isNumeric(String.valueOf(age))) {
                    disableSaveButton();
                    ageTextLayout.setError(getResources().getString(R.string.incorrectFormatAgeMsg));
                } else if (isAgeLowerThanMin(age)) {
                    disableSaveButton();
                    ageTextLayout.setError(getResources().getString(R.string.tooLowAgeMsg));
                } else if (isAgeHigherThanMax(age)) {
                    disableSaveButton();
                    ageTextLayout.setError(getResources().getString(R.string.tooHighAgeMsg));
                } else {
                    enableSaveButton();
                    ageTextLayout.setError(null);
                }
            }

            CheckBox soundCheckBox = findViewById(R.id.soundCheckBox);
            if (soundCheckBox != null) {
                boolean soundEnabled = preferences.getBoolean(
                        SOUND_ENABLED_KEY,
                        getResources().getBoolean(R.bool.defaultSoundEnabled)
                );
                soundCheckBox.setChecked(soundEnabled);
            }
        }
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
    }


    private void saveSettings() {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor preferencesEditor = preferences.edit();

        EditText nicknameText = findViewById(R.id.nicknameInput);
        if (nicknameText != null) {
            String nickname = nicknameText.getText().toString();
            preferencesEditor.putString(NICKNAME_KEY, nickname);
        }


        EditText ageText = findViewById(R.id.ageInput);
        if (ageText != null) {
            Integer age = Integer.valueOf(ageText.getText().toString());
            preferencesEditor.putInt(AGE_KEY, age);
        }

        CheckBox soundCheckBox = findViewById(R.id.soundCheckBox);
        if (soundCheckBox != null) {
            boolean soundEnabled = soundCheckBox.isChecked();
            preferencesEditor.putBoolean(SOUND_ENABLED_KEY, soundEnabled);
        }

        preferencesEditor.apply();
        finish();
    }
}
