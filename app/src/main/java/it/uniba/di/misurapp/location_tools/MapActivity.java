package it.uniba.di.misurapp.location_tools;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import it.uniba.di.misurapp.DatabaseManager;
import it.uniba.di.misurapp.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    static boolean status;
    Timer timer;
    static Double latitude,longitude;
    LocationManager locationManager;
    LocationService myService;
    static long startTime, endTime;
    static int p = 1;
    DatabaseManager helper;
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

        //oggetto helper database
        helper = new DatabaseManager(this);
        SQLiteDatabase db = helper.getReadableDatabase();



        buttonAdd = findViewById(R.id.add);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.gps_coordinates);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


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
            // TODO: Rendere il controllo del gps e la conseguente allerta all'utente dinamico (se l'utente spegne il servizio)
            Toast.makeText(this,R.string.alert_gps_off,Toast.LENGTH_SHORT).show();
        }

        // Thread principale di aggiornamento del marker sullla mappa
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        final Runnable changeLocationRunnable = (new Runnable() {
            @Override
            public void run() {
                // TODO: Permettere l'interruzione del runnable in caso si sia spostato il focus dalla propria posizione, riprenderlo con un bottone o simili
                changeLocation();
                mainHandler.postDelayed(this,5000);
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


                                //salvo valore in variabile
                                String value1 = "Latitudine: "+latitude+" Longitudine: "+longitude;

                                //imposto nome tool
                                String name_tool ="Gps";

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
                                }                               }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
            // TODO: Se viene spento il GPS ma è stata individuata una posizione in precedenza essa verrà segnata come posizione attuale,
            //  gestire la situazione o trovare un'alternativa
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
}