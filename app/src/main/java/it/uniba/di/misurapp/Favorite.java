package it.uniba.di.misurapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import java.util.Objects;

public class Favorite extends AppCompatActivity {

    LinearLayout compass,magneticfield,photometer,soundintensity,pressure,temperature,altitude,gpscoords,spiritlevel,proximity,gravity,speed,acceleration,mainlayout;
    TextView alertnofav;
    CardView ambientcard,locationcard,motioncard;
    int ambient = 0,location = 0,motion = 0;
    int flag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.favorite);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // LinearLayout delle varie caselle
        compass = findViewById(R.id.compass);
        magneticfield = findViewById(R.id.magneticfield);
        photometer = findViewById(R.id.lightintensity);
        soundintensity = findViewById(R.id.soundintensity);
        pressure = findViewById(R.id.pressure);
        temperature = findViewById(R.id.devicetemperature);
        altitude = findViewById(R.id.altitude);
        gpscoords = findViewById(R.id.gpscoords);
        spiritlevel = findViewById(R.id.spiritlevel);
        proximity = findViewById(R.id.proximity);
        gravity = findViewById(R.id.gravity);
        speed = findViewById(R.id.speed);
        acceleration = findViewById(R.id.deviceacceleration);

        // LinearLayout generale interno alla ScrollView
        mainlayout = findViewById(R.id.mainlayout);

        // TextView da mostrare in caso non vi siano preferiti
        alertnofav = (TextView) findViewById(R.id.alertnofav);

        // CardView di ogni gruppo di strumenti
        ambientcard = (CardView) findViewById(R.id.ambientcard);
        locationcard = (CardView) findViewById(R.id.locationcard);
        motioncard = (CardView) findViewById(R.id.motioncard);

        // Setup del layout
        checkFavorite();

        // Insieme di Listener per lanciare i Tool direttamente dai preferiti
        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),CompassTool.class);
                startActivity(intent);
            }
        });

        magneticfield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),MagneticField.class);
                startActivity(intent);
            }
        });

        photometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),LightTool.class);
                startActivity(intent);
            }
        });

        soundintensity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),SoundIntensity.class);
                startActivity(intent);
            }
        });

        pressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),PressureTool.class);
                startActivity(intent);
            }
        });

        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),TemperatureTool.class);
                startActivity(intent);
            }
        });

        altitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),Altimeter.class);
                startActivity(intent);
            }
        });

        gpscoords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),MapActivity.class);
                startActivity(intent);
            }
        });

        spiritlevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),SpiritLevel.class);
                startActivity(intent);
            }
        });

        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),ProximityTool.class);
                startActivity(intent);
            }
        });

        gravity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),GravityTool.class);
                startActivity(intent);
            }
        });

        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),Speed.class);
                startActivity(intent);
            }
        });

        acceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),AccelerometerTool.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Metodo per controllare le preferenze sul database e mostrare quelle eventualmente già selezionate
     */
    public void checkFavorite(){

        // Inizializzazione
        flag = 0;
        ambient = 0;
        location = 0;
        motion = 0;

        DatabaseManager helper;
        helper = new DatabaseManager(this);

        // Ciclo for per controllare quali strumenti sono i preferiti dell'utente
        for (int i=1; i<=13; i++){
            if (helper.getFavoriteTool(i) == 0){
                flag++;
                populateFavorite(i);
            }
        }

        // Metodo che si occupa della visualizzazione corretta
        updateview();
    }

    /**
     * Metodo di supporto a checkFavorite per sistemare i preferiti stampati; gli incrementi delle variabili globali int servono per gestire la visualizzazione
     * @param id id sul db del tool scelto
     */
    public void populateFavorite(int id){
        switch (id){
            case 1  : compass.setVisibility(LinearLayout.GONE); ambient++; break;
            case 2  : magneticfield.setVisibility(LinearLayout.GONE); ambient++; break;
            case 3  : photometer.setVisibility(LinearLayout.GONE); ambient++; break;
            case 4  : soundintensity.setVisibility(LinearLayout.GONE);ambient++; break;
            case 5  : pressure.setVisibility(LinearLayout.GONE); ambient++; break;
            case 6  : temperature.setVisibility(LinearLayout.GONE); ambient++; break;
            case 7  : altitude.setVisibility(LinearLayout.GONE); location++; break;
            case 8  : gpscoords.setVisibility(LinearLayout.GONE); location++; break;
            case 9  : spiritlevel.setVisibility(LinearLayout.GONE); location++; break;
            case 10 : proximity.setVisibility(LinearLayout.GONE); location++; break;
            case 11 : gravity.setVisibility(LinearLayout.GONE); motion++; break;
            case 12 : speed.setVisibility(LinearLayout.GONE); motion++; break;
            case 13 : acceleration.setVisibility(LinearLayout.GONE); motion++; break;
        }
    }

    /**
     * Metodo utile per la visualizzazione dei preferiti.
     * Il primo controllo effettuato è sulla presenza o meno dei preferiti che in caso di esistenza verranno mostrari nelle cardview rispettive.
     * Il secondo set di controlli (ambient, location e motion) permette una stampa più ordinata in base alla presenza o meno di almeno un tool
     * appartenente a un determinato gruppo di strumenti per evitare placeholder vuoti.
     */
    public void updateview(){

        if (flag == 13){
            mainlayout.setVisibility(LinearLayout.GONE);
            alertnofav.setVisibility(LinearLayout.VISIBLE);
        } else {
            mainlayout.setVisibility(LinearLayout.VISIBLE);
            alertnofav.setVisibility(TextView.GONE);

            // Se per 6 è stata aggiornata la variabile ambient allora nel database non vi saranno preferiti della categoria ambiental_tools
            if (ambient == 6){
                ambientcard.setVisibility(CardView.GONE);
            }

            // Se per 4 è stata aggiornata la variabile location allora nel database non vi saranno preferiti della categoria location_tools
            if (location == 4){
                locationcard.setVisibility(CardView.GONE);
            }

            // Se per 3 è stata aggiornata la variabile motion allora nel database non vi saranno preferiti della categoria movement_tools
            if (motion == 3){
                motioncard.setVisibility(CardView.GONE);
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        // Chiamata nell'onResume per garantire l'update della Lista di preferiti dopo la navigazione ad un Tool e la rimozione di esso dai preferiti
        checkFavorite();
    }
}
