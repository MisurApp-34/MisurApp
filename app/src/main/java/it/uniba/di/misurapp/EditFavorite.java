package it.uniba.di.misurapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class EditFavorite extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch CompassSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch SoundIntensitySwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch PressureSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch PhotometerSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch TemperatureSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch MagneticFieldSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch AltitudeSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch GpsCoordsSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch SpiritLevelSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch ProximitySwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch GravitySwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch AccelerationSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch SpeedSwitch;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editfavorites);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.edit_favorites);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Collegamento elementi vista
        CompassSwitch = findViewById(R.id.compass);
        MagneticFieldSwitch = findViewById(R.id.magneticfield);
        PhotometerSwitch = findViewById(R.id.lightintensity);
        SoundIntensitySwitch = findViewById(R.id.soundintensity);
        PressureSwitch = findViewById(R.id.pressure);
        TemperatureSwitch = findViewById(R.id.devicetemperature);
        AltitudeSwitch = findViewById(R.id.altitude);
        GpsCoordsSwitch = findViewById(R.id.gpscoords);
        SpiritLevelSwitch = findViewById(R.id.spiritlevel);
        ProximitySwitch = findViewById(R.id.proximity);
        GravitySwitch = findViewById(R.id.gravity);
        SpeedSwitch = findViewById(R.id.speed);
        AccelerationSwitch = findViewById(R.id.deviceacceleration);

        checkFavorite();

        final DatabaseManager helper;
        helper = new DatabaseManager(this);

        /*
         * Serie di setOnCheckedChangeListener ereditati dagli attributi di CompoundButton per gestire la preferenza inserita.
         * Ogni listener controlla se il toggle è stato messo in posizione true o false e eventualmente lo inserisce sul database o lo rimuove da esso
         */

        CompassSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (CompassSwitch.isChecked()) helper.favoriteTool(1,1);
                else helper.favoriteTool(1,0);
            }
        });

        MagneticFieldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (MagneticFieldSwitch.isChecked()) helper.favoriteTool(2,1);
                else helper.favoriteTool(2,0);
            }
        });

        PhotometerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (PhotometerSwitch.isChecked()) helper.favoriteTool(3,1);
                else helper.favoriteTool(3,0);
            }
        });

        SoundIntensitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SoundIntensitySwitch.isChecked()) helper.favoriteTool(4,1);
                else helper.favoriteTool(4,0);
            }
        });

        PressureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (CompassSwitch.isChecked()) helper.favoriteTool(5,1);
                else helper.favoriteTool(5,0);
            }
        });

        TemperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (TemperatureSwitch.isChecked()) helper.favoriteTool(6,1);
                else helper.favoriteTool(6,0);
            }
        });

        AltitudeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (AltitudeSwitch.isChecked()) helper.favoriteTool(7,1);
                else helper.favoriteTool(7,0);
            }
        });

        GpsCoordsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (GpsCoordsSwitch.isChecked()) helper.favoriteTool(8,1);
                else helper.favoriteTool(8,0);
            }
        });

        SpiritLevelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SpiritLevelSwitch.isChecked()) helper.favoriteTool(9,1);
                else helper.favoriteTool(9,0);
            }
        });

        ProximitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ProximitySwitch.isChecked()) helper.favoriteTool(10,1);
                else helper.favoriteTool(10,0);
            }
        });

        GravitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (GravitySwitch.isChecked()) helper.favoriteTool(11,1);
                else helper.favoriteTool(11,0);
            }
        });

        SpeedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SpeedSwitch.isChecked()) helper.favoriteTool(12,1);
                else helper.favoriteTool(12,0);
            }
        });

        AccelerationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (AccelerationSwitch.isChecked()) helper.favoriteTool(13,1);
                else helper.favoriteTool(13,0);
            }
        });
    }

    /**
     * Metodo per controllare le preferenze sul database e spuntare quelle eventualmente già selezionate
     */
    public void checkFavorite(){

        DatabaseManager helper;
        helper = new DatabaseManager(this);

        for (int i=1; i<=13; i++){
            if (helper.getFavoriteTool(i) == 1){
                populateFavorite(i);
            }
        }
    }

    /**
     * Metodo di supporto a checkFavorite per sistemare i toggle
     * @param id id sul db del tool scelto
     */
    public void populateFavorite(int id){
        switch (id){
            case 1  : CompassSwitch.setChecked(true); break;
            case 2  : MagneticFieldSwitch.setChecked(true); break;
            case 3  : PhotometerSwitch.setChecked(true); break;
            case 4  : SoundIntensitySwitch.setChecked(true); break;
            case 5  : PressureSwitch.setChecked(true); break;
            case 6  : TemperatureSwitch.setChecked(true); break;
            case 7  : AltitudeSwitch.setChecked(true); break;
            case 8  : GpsCoordsSwitch.setChecked(true); break;
            case 9  : SpiritLevelSwitch.setChecked(true); break;
            case 10 : ProximitySwitch.setChecked(true); break;
            case 11 : GravitySwitch.setChecked(true); break;
            case 12 : SpeedSwitch.setChecked(true); break;
            case 13 : AccelerationSwitch.setChecked(true); break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
