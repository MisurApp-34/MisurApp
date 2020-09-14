
package it.uniba.di.misurapp;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        //importo fragment
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

        //fragment preferenze
    public static class MyPreferenceFragment extends PreferenceFragment
    {

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            //importo file preferenze
            addPreferencesFromResource(R.xml.settings);

            //cerco preferenza
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


        //listner preferenza con commit di sistema
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