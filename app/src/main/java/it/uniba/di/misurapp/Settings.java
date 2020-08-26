package it.uniba.di.misurapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.widget.Toast;

import java.util.Locale;

public class Settings extends PreferenceActivity {

    Locale myLocale;
    public String pressure_unit;
    public String temperature_unit;
   public  String speed_unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference langPreference = getPreferenceScreen().findPreference(
            "languages");
        langPreference.setOnPreferenceChangeListener(languageChangeListener);


        Preference tempPreference = getPreferenceScreen().findPreference(
                "temperature");
        tempPreference.setOnPreferenceChangeListener(temperatureChangeListener);

        Preference pressPreference = getPreferenceScreen().findPreference(
                "pressure");
        pressPreference.setOnPreferenceChangeListener(pressureChangeListener);

        Preference speedPreference = getPreferenceScreen().findPreference(
                "speed");
       speedPreference.setOnPreferenceChangeListener(speedChangeListener);

}

    Preference.OnPreferenceChangeListener languageChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            switch (newValue.toString()) {
                case "en":
                    setLocale("en");
                    Toast.makeText(getApplicationContext(), "English!", Toast.LENGTH_LONG).show();
                    break;

                case "it":
                    setLocale("it");
                    Toast.makeText(getApplicationContext(), "Italian", Toast.LENGTH_LONG).show();
                    break;
            }
            return false;
        }
    };

    Preference.OnPreferenceChangeListener temperatureChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            SharedPreferences settings = getSharedPreferences("settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("temperature",newValue.toString());
            editor.commit();


            return false;
        }
    };

    Preference.OnPreferenceChangeListener pressureChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            SharedPreferences settings = getSharedPreferences("settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("pressure",newValue.toString());
            editor.commit();


            return false;
        }
    };



    Preference.OnPreferenceChangeListener speedChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            SharedPreferences settings = getSharedPreferences("settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("speed",newValue.toString());
            editor.commit();



            return false;
        }
    };


    //* manually changing current locale/
    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, Settings.class);
        finish();
        startActivity(refresh);
    }

}