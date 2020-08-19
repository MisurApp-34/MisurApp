package it.uniba.di.misurapp.location_tools;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import it.uniba.di.misurapp.R;

import static it.uniba.di.misurapp.location_tools.Altimeter.*;
import static it.uniba.di.misurapp.location_tools.MapActivity.*;
import static it.uniba.di.misurapp.location_tools.Speed.*;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long INTERVAL = 1000*2;
    private static final long FASTEST_INTERVAL=1000;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    double currentHeight =0;
    double currentLat,currentLon,currentSpeed = 0;
    private final IBinder mBinder = new LocalBinder();
    Timer timer;

    /**
     * Bind servizio con classe
     * @param intent relativo alla classe legata al service
     * @return Bind
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
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

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (currentLat!=0.0 && currentLon!=0.0) {
                    updateUI();
                }
            }
        },0,5000);
    }

    /**
     * Aggiornamento dati e relativa stampa nella textview di single_tool.xml
     */
    private void updateUI() {
        if(Altimeter.p ==0){
            Altimeter.endTime = System.currentTimeMillis();
            updateValue(currentHeight);
            //Altimeter.measure.setText(new DecimalFormat("#.#").format((currentHeight*3.28))+" F");
        }
        if (MapActivity.p == 0){
            MapActivity.endTime = System.currentTimeMillis();
            getCoordinates(currentLat,currentLon);
        }
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
