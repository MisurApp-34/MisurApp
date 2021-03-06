package it.uniba.di.misurapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static it.uniba.di.misurapp.Altimeter.updateValue;
import static it.uniba.di.misurapp.MapActivity.getCoordinates;
import static it.uniba.di.misurapp.Speed.getSpeed;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Intervallo di aggiornamento standard
    private static final long INTERVAL = 1000*2;
    // Intervallo di aggiornamento rapido
    private static final long FASTEST_INTERVAL = 1000;
    // Variabile richiesta posizione
    LocationRequest mLocationRequest;
    // Riferimento all'api google
    GoogleApiClient mGoogleApiClient;
    // valore altezza inizializzato a 0
    double currentHeight = 0;
    // valori ricevuti da google api su latitudine, longitudine e velocità
    double currentLat,currentLon,currentSpeed = 0;
    // Binder per "onBind()"
    private final IBinder mBinder = new LocalBinder();

    /**
     * Bind servizio con classe
     * @param intent relativo alla classe legata al service
     * @return Bind
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Richiesta posizione
        createLocationRequest();
        // API google per posizione
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        return mBinder;

    }

    /**
     * Setup richiesta per la posizione
     */
    @SuppressLint("RestrictedApi")
    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval((FASTEST_INTERVAL));
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Richiesta in connessione della posizione
     * @param bundle Stato
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);
        }
        catch(SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    /**
     * Eventuale cambiamento coordinate
     * @param location posizione per ricavare l'altitudine
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location.hasAltitude()) {
            currentHeight = location.getAltitude();
        }
        currentLat = location.getLatitude();
        currentLon = location.getLongitude();
        currentSpeed = location.getSpeed();

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        final Runnable locupdated = (new Runnable() {
            @Override
            public void run() {
                if (currentLat!=0.0 && currentLon!=0.0) {
                    updateUI();
                }
            }
        });
        mainHandler.postDelayed(locupdated,5000);
    }

    /**
     * Aggiornamento dati e relativa stampa nella textview di single_tool.xml
     */
    private void updateUI() {
        // Richiesta dall'altimetro della posizione
        if(Altimeter.p == 0){
            Altimeter.endTime = System.currentTimeMillis();
            updateValue(currentHeight);
        }
        // Richiesta da MapActivity della posizione
        if (MapActivity.p == 0){
            MapActivity.endTime = System.currentTimeMillis();
            getCoordinates(currentLat,currentLon);
        }
        // Richiesta da Speed della velocità
        if (Speed.p == 0){
            Speed.endTime = System.currentTimeMillis();
            getSpeed(currentSpeed);
        }
    }

    /**
     * Unbind servizio
     * @param intent classe di chiamata
     * @return unbind tramite intent
     */
    @Override
    public boolean onUnbind(Intent intent){
        stopLocationUpdates();
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        return super.onUnbind(intent);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public class LocalBinder extends Binder {
        public LocationService getService(){
            return LocationService.this;
        }
    }

}
