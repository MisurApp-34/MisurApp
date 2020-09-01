
package it.uniba.di.misurapp;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class Settings extends  AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        //importo toolbar

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            getSupportActionBar().setTitle(R.string.settings);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        FragmentTransaction transaction =
                getFragmentManager().beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.frameLayout, new MyPreferenceFragment())
                .commitAllowingStateLoss();



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public static class MyPreferenceFragment extends PreferenceFragment
    {

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
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
                        break;

                    case "it":
                        setLocale("it");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + newValue.toString());
                }
                return true;
            }
        };
        //* manually changing current locale/
        public void setLocale(String lang) {
            Locale myLocale = new Locale(lang);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(getActivity(),Settings.class);
            getActivity().finish();
            startActivity(refresh);

        }
        Preference.OnPreferenceChangeListener temperatureChangeListener = new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                SharedPreferences settings = getActivity().getSharedPreferences("settings",0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("temperature",newValue.toString());
                editor.commit();


                return true;
            }
        };

        Preference.OnPreferenceChangeListener pressureChangeListener = new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                SharedPreferences settings = getActivity().getSharedPreferences("settings",0);

                SharedPreferences.Editor editor = settings.edit();
                editor.putString("pressure",newValue.toString());
                editor.commit();


                return true;
            }
        };



        Preference.OnPreferenceChangeListener speedChangeListener = new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                SharedPreferences settings = getActivity().getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("speed",newValue.toString());
                editor.commit();



                return true;
            }
        };
    }


}