package com.satyambyte.quizgpt;

import static androidx.core.app.ActivityCompat.recreate;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import javax.annotation.Nullable;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String PREF_LANGUAGE_KEY = "pref_language_key";
    private boolean isRecreating = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocalFromPreferences(this);

    }
    protected void changeLanguage(String languageCode) {
        if (!isRecreating) {
            isRecreating = true;
            setLocal(this, languageCode);
            saveLanguageToPreferences(languageCode);
            isRecreating = false;
        }
    }

    private void setLocal(Context context, String langCode) {
        // Implement your setLocal logic here
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
    private void saveLanguageToPreferences(String languageCode) {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_LANGUAGE_KEY, languageCode);
        editor.apply();
    }

    private void setLocalFromPreferences(Context context) {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String languageCode = preferences.getString(PREF_LANGUAGE_KEY, "en"); // Default to English if not found
        setLocal(context, languageCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
