package edu.ktu.lab.labaratorinis1.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import edu.ktu.lab.labaratorinis1.R;

public class MainActivity extends Activity {

    private static final String SETTINGS_FILE_NAME = "game-settings";
    private static final String NOT_FIRST_TIME_KEY = "not-first-time";
    private static final String NICKNAME_KEY = "nickname";
    private static final String GAME_POINTS_KEY = "game-points";
    public static final String COMMA = ",";
    public static final int PLAY_ACTIVITY_REQUEST_CODE = 1;
    public static final int MAX_VISIBLE_USERNAME_LENGTH = 15;
    public static final String DOT = ".";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_linear);
        prepareGreeting();
        preparePoints();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLAY_ACTIVITY_REQUEST_CODE) {
            prepareGreeting();
            preparePoints();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareGreeting();
        preparePoints();
    }

    private void preparePoints() {
        TextView pointsTextView = findViewById(R.id.pointsTextView);
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (pointsTextView != null && sharedPreferences != null) {
            Integer userPoints = sharedPreferences.getInt(GAME_POINTS_KEY, NumberUtils.INTEGER_ZERO);
            String userPointsText = String.valueOf(userPoints);
            String pointsLabelText = getResources().getString(R.string.userPointsLabel);
            pointsLabelText = pointsLabelText.replace("${points}", userPointsText);
            pointsTextView.setText(pointsLabelText);
        }
    }

    public void openAbout(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openPlay(View view) {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivityForResult(intent, PLAY_ACTIVITY_REQUEST_CODE);
    }

    private void prepareGreeting() {
        SharedPreferences preferences = getSharedPreferences();
        Boolean notFirstTime = preferences.getBoolean(NOT_FIRST_TIME_KEY, false);
        if (!notFirstTime) {
            SharedPreferences.Editor sharedPreferencesEditor = preferences.edit();
            if (sharedPreferencesEditor != null) {
                sharedPreferencesEditor.putBoolean(NOT_FIRST_TIME_KEY, true);
                sharedPreferencesEditor.apply();
            }
        }

        String username = preferences.getString(NICKNAME_KEY, StringUtils.EMPTY);
        String greetingText;
        if (StringUtils.isNotBlank(username)) {
            if (username.length() > MAX_VISIBLE_USERNAME_LENGTH) {
                username = username.substring(0, 12)
                        .concat(DOT)
                        .concat(DOT)
                        .concat(DOT);
            }
            greetingText = getGreeting(username, notFirstTime);
        } else {
            greetingText = getGreeting(getResources().getString(R.string.unknownUser), notFirstTime);
        }

        if (StringUtils.isNotBlank(greetingText)) {
            TextView greetingTextView = findViewById(R.id.greetingTextView);
            greetingTextView.setText(greetingText);
        }
    }

    private String getGreeting(String username, boolean notFirstTime) {
        if (notFirstTime) {
            return getResources().getString(R.string.knownUserGreeting)
                    .concat(COMMA)
                    .concat(StringUtils.SPACE)
                    .concat(username);
        } else {
            return getResources().getString(R.string.newUserGreeting)
                    .concat(COMMA)
                    .concat(StringUtils.SPACE)
                    .concat(username);
        }
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
    }
}
