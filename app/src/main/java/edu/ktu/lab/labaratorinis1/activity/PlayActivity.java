package edu.ktu.lab.labaratorinis1.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ktu.lab.labaratorinis1.R;
import edu.ktu.lab.labaratorinis1.ReverseArrayAdapter;

public class PlayActivity extends Activity {

    private static final String SETTINGS_FILE_NAME = "game-settings";
    private static final String GAME_POINTS_KEY = "game-points";
    private static final String AGE_KEY = "age";
    private static final int GUESS_FROM = 1;
    private static final int GUESS_TO = 10;
    private static final int UNAMUSED_FACE_UNICODE = 0x1F612;
    private static final int HUSHED_FACE_UNICODE = 0x1F62F;
    private static final int PARTY_UNICODE = 0x1F389;
    private static final int ANGRY_UNICODE = 0x1F92C;
    private static final int SCREAMING_UNICODE = 0x1F631;
    private static final String DOT = ".";
    private static final int MAX_LIST_VIEW_ROW_LENGTH = 10;
    private static final int MAX_NUMBER_LENGTH_DIGITS = 7;
    private static final int PLUS_POINTS_FOR_CORRECT = 5;
    private static final int MINUS_POINTS_FOR_INCORRECT = 1;
    public static final int ADULT_AGE = 18;
    public static final String PAST_RESULTS_KEY = "past-results";

    private BigInteger numberToGuess;
    private List<String> pastResults;
    private ArrayAdapter<String> pastResultsAdapter;

    public PlayActivity() {
        super();
        pastResults = new ArrayList<>();
        numberToGuess = BigInteger.valueOf(RandomUtils.nextInt(GUESS_FROM, GUESS_TO));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        ListView listView = findViewById(R.id.pastResultsListView);
        pastResults = getPastResults();
        pastResultsAdapter = new ReverseArrayAdapter<>(this, R.layout.list_view_item, pastResults);

        listView.setAdapter(pastResultsAdapter);

        Integer userPoints = getSharedPreferences().getInt(GAME_POINTS_KEY, NumberUtils.INTEGER_ZERO);
        setUserPoints(userPoints, false);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private boolean isAdult() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences != null) {
            Integer age = sharedPreferences.getInt(AGE_KEY, getResources().getInteger(R.integer.defaultAge));
            return (age >= ADULT_AGE);
        }
        return false;
    }

    public void onGuessButtonPress(View view) {
        EditText valueEditText = findViewById(R.id.valueText);
        Object inputMethodServiceObj = getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodServiceObj instanceof InputMethodManager) {
            InputMethodManager inputMethodManager = (InputMethodManager) inputMethodServiceObj;
            inputMethodManager.showSoftInput(valueEditText, InputMethodManager.SHOW_IMPLICIT);
        }

        String valueText = valueEditText.getText().toString();
        if (!StringUtils.isNumeric(valueText)) {
            // TODO: Show error
            return;
        }
        BigInteger valueEntered = new BigInteger(valueEditText.getText().toString());
        Integer userPoints = getSharedPreferences().getInt(GAME_POINTS_KEY, NumberUtils.INTEGER_ZERO);
        if (valueEntered.compareTo(numberToGuess) == 0) {
            numberToGuess = BigInteger.valueOf(RandomUtils.nextInt(GUESS_FROM, GUESS_TO));
            userPoints += PLUS_POINTS_FOR_CORRECT;
            setUserPoints(userPoints, true);
            addResult(valueEntered, PARTY_UNICODE);

        } else {
            if (userPoints - MINUS_POINTS_FOR_INCORRECT >= 0) {
                userPoints -= MINUS_POINTS_FOR_INCORRECT;
            }
            setUserPoints(userPoints, false);
            boolean higher = (valueEntered.compareTo(numberToGuess) > 0);
            addResult(valueEntered, resolveEmoji(higher));
            pastResultsAdapter.notifyDataSetChanged();
        }
        valueEditText.getText().clear();
    }

    private List<String> getPastResults() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if (sharedPreferences != null) {
            Set<String> pastResultsString = sharedPreferences.getStringSet(PAST_RESULTS_KEY, new HashSet<String>());
            return new ArrayList<>(pastResultsString);
        }
        return new ArrayList<>();
    }

    private void addResult(BigInteger result, int emoji) {
        String resultTxt = result.toString();
        if (resultTxt.length() > MAX_LIST_VIEW_ROW_LENGTH) {
            resultTxt = resultTxt.substring(0, MAX_NUMBER_LENGTH_DIGITS)
                    .concat(DOT)
                    .concat(DOT)
                    .concat(DOT);
        }
        pastResults.add(resultTxt
                .concat(StringUtils.SPACE)
                .concat(getEmojiByUnicode(emoji)));

        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferencesEditor != null) {
            Set<String> results = new HashSet<>(pastResults);
            sharedPreferencesEditor.putStringSet(PAST_RESULTS_KEY, results);
            sharedPreferencesEditor.apply();
        }
        pastResultsAdapter.notifyDataSetChanged();
    }

    private void setUserPoints(Integer points, boolean withAnimation) {
        SharedPreferences.Editor preferencesEditor = getSharedPreferences().edit();
        preferencesEditor.putInt(GAME_POINTS_KEY, points);
        preferencesEditor.apply();

        TextView userPointsText = findViewById(R.id.userPointsText);
        userPointsText.setText(String.valueOf(points));

        if (withAnimation) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setRepeatCount(-1);
            userPointsText.clearAnimation();
            userPointsText.startAnimation(animation);
        }
    }

    private int resolveEmoji(boolean higher) {
        if (higher) {
            if (isAdult()) {
                return SCREAMING_UNICODE;
            } else {
                return HUSHED_FACE_UNICODE;
            }
        } else {
            if (isAdult()) {
                return ANGRY_UNICODE;
            } else {
                return UNAMUSED_FACE_UNICODE;
            }
        }
    }

    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
    }
}
