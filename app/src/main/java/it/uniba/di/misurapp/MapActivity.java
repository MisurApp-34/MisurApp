package it.uniba.di.misurapp;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Mappa ottenuta da google
    public GoogleMap mMap;
    // flag di controllo activity
    static boolean status;
    // Timer stampa
    Timer timer;
    // Valori latitudine e longitudine
    static Double latitude,longitude;
    // Servizii posizione
    LocationManager locationManager;
    // Servizi posizione
    LocationService myService;
    // Inizio e fine bind
    static long startTime, endTime;
    // Flag per il LocationService per capire la richiesta effettuata
    static int p = 1;
    // Helper database
    DatabaseManager helper;
    // Valore latitudine e longitudine concatenati per la stampa
    String value1;
    // Bottoni aggiungi/rimmuovi preferito
    Button addpreferenceButton,removepreferenceButton;
    // Flag controllo preferito
    int favourite;
    //pulsante aggiunta dati database
    private Button buttonAdd;
    // stampa toast messaggio
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        p=0;

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.gps);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // oggetto helper database
        helper = new DatabaseManager(this);

        final TextView measure = (TextView) findViewById(R.id.measure);
        TextView details = (TextView) findViewById(R.id.details);
        details.setText(R.string.gps);

        // pulsante salva nei preferiti
        addpreferenceButton = (Button) findViewById(R.id.add_fav);
        removepreferenceButton = (Button) findViewById(R.id.remove_fav);

        // verifico l'entità dell'id nel database
        favourite = helper.getFavoriteTool(8);
        if (favourite == 1) {

            addpreferenceButton.setVisibility(View.GONE);
            removepreferenceButton.setVisibility(View.VISIBLE);
            removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

        } else {

            addpreferenceButton.setVisibility(View.VISIBLE);
            removepreferenceButton.setVisibility(View.GONE);
            addpreferenceButton.getBackground().setColorFilter(Color.parseColor("#80d10f"), PorterDuff.Mode.SRC_IN);

        }

        // Listener per aggiungere il tool all'insieme di tool preferiti
        addpreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.favoriteTool(8,1);
                addpreferenceButton.setVisibility(View.GONE);
                removepreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Listener per rimuovere dalla lista preferiti il tool
        removepreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.favoriteTool(8,0);
                addpreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.setVisibility(View.GONE);
                addpreferenceButton.getBackground().setColorFilter(Color.parseColor("#80d10f"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Storico misurazioni specifico dello strumento selezionato
        Button buttonHistory = (Button) findViewById(R.id.history);

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSave.id_tool=8;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

        buttonAdd = findViewById(R.id.add);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        // Controllo permessi dal Manifest (posizione)
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                },1000);
            }
        }

        // Reminder attivazione GPS
        if (!checkGPS()){
            measure.setText(R.string.alert_gps_off);
            Toast.makeText(this,R.string.alert_gps_off,Toast.LENGTH_SHORT).show();
        } else {
            measure.setText(R.string.getting_location);
        }

        // Thread principale di aggiornamento del marker sullla mappa
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        final Runnable changeLocationRunnable = (new Runnable() {
            @Override
            public void run() {
                changeLocation();
                mainHandler.postDelayed(this,5000);
                if (latitude!=null && longitude!=null) {
                    String append = "Latitudine: "+latitude+" Longitudine: "+longitude;
                    measure.setText(append);
                }
            }
        });

        mainHandler.postDelayed(changeLocationRunnable,5000);

        // Controllo a runtime della presenza o meno del segnale gps
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gpsloop();
            }
        },0,5000);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //salvo valore in variabile
                value1 = "Latitudine: "+latitude+" Longitudine: "+longitude;
                //dialog text acquisizione nome salvataggio
                final EditText input = new EditText(MapActivity.this);

                //apertura dialog inserimento nome salvataggio
                new AlertDialog.Builder(MapActivity.this)
                        .setTitle(getResources().getString(R.string.name_saving))
                        .setMessage(getResources().getString(R.string.insert_name))
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                //acquisisco nome
                                Editable nome = input.getText();

                                //imposto nome tool
                                // String name_tool ="Gps";
                                String name_tool = getResources().getString(R.string.gps);

                                //converto editable in stringa
                                String saving_name= nome.toString();

                                //aggiungo al db
                                if (value1.length() != 0) {

                                    boolean insertData = helper.addData( saving_name, name_tool, value1);

                                    if (insertData) {
                                        toastMessage(getResources().getString(R.string.uploaddata_message_ok));
                                    } else {
                                        toastMessage(getResources().getString(R.string.uploaddata_message_error));
                                    }
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Do nothing.
                            }
                        }).show();
            }
        });

    }

    /**
     * Permette l'utilizzo della mappa appena disponibile
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /**
     * Aggiunta di un nuovo marker in base alla propria posizione
     */
    public void changeLocation(){
        if (latitude!=null && longitude!=null) {
            newLocation(latitude, longitude);
            mMap.clear();
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(getResources().getString(R.string.currentlocation));
            mMap.addMarker(marker);
        }
    }

    /**
     * Controllo della presenza del gps e conseguente Bind servizio di posizione
     */
    public void gpsloop(){
        if (checkGPS()) {
            bindService();
        }
    }

    /**
     * Individuazione posizione attuale e spostamento del focus della camera sulla propria posizione
     * @param latitude valore della latitudine della posizione attuale
     * @param longitude valore della longitudine della posizione attuale
     */
    public void newLocation(double latitude, double longitude){
        LatLng a = new LatLng(latitude,longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(a,15.5f), 2000, null);
    }

    /**
     * Interfaccia con LocationService.java per la propria posizione
     * @param lat latitudine attuale ottenuta da LocationService.java
     * @param lon longitudine attuale ottenuta da LocationService.java
     */
    public static void getCoordinates(double lat, double lon){
        latitude = lat;
        longitude = lon;
    }

    /**
     * Boolean di controllo presenza del segnale GPS
     * @return true/false - se è attivo o meno il gps
     */
    private boolean checkGPS() {
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Connessione al servizio LocationService
     */
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder)iBinder;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            status = false;
        }
    };

    /**
     * Unbind servizio in "onDestroy" per evitare NullObject
     */
    @Override
    public void onDestroy(){
        if(status) unbindService();
        super.onDestroy();
    }

    /**
     * Scollegamento servizio LocationService
     */
    private void unbindService() {
        if(status) return;
        unbindService(sc);
        status = false;
    }

    /**
     * Bind di LocationService
     */
    private void bindService(){
        if(status) return;
        Intent i = new Intent(getApplicationContext(),LocationService.class);
        bindService(i,sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        status = false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        status = false;
        return super.onSupportNavigateUp();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }
}