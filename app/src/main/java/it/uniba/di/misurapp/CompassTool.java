package it.uniba.di.misurapp;

import android.app.Activity;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CompassTool extends AppCompatActivity implements SensorEventListener {

//definisco l'immagine
private ImageView image;


    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;

    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    //variabile in cui memorizzo le  tre  misure  dei tre assi dell'accelerometro
    private float[] mLastAccelerometer = new float[3];
    //variabile in cui memorizzo le tre  misure  dei tre assi dell'magnetometro
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    //matrice di rotazione
    private float[] mR = new float[9];
    //variabile in cui memorizzo le misure  dell'orientamento
    private float[] mOrientation = new float[3];
    // registro l'angolo di inclinazione dell'immagine a 0 gradi

    private float mCurrentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_tool_nochart);

        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            getSupportActionBar().setTitle(R.string.compass_details);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        TextView details = findViewById(R.id.details);
        Resources res = getResources();

        //TextView tipo strumento
        details.setText(R.string.compass_instrument);

        image = (ImageView) findViewById(R.id.imageView);

        // textview dove viene mostrata l'angolazione
        tvHeading = (TextView) findViewById(R.id.measure);

        // inizializzo sensori
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //  registro Listner sensori
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stoppo Listner sensori per risparmiare batteria
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated


        if (event.sensor == mAccelerometer) {
            //memorizzo misure accelerometro
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            //memorizzo misure magnetometro

            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            //genero matrice di rotazione
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            //prendo orientamento
            SensorManager.getOrientation(mR, mOrientation);
            //radianti
            float azimuthInRadians = mOrientation[0];
            //gradi
            int azimuthInDegress = (int) (Math.toDegrees(azimuthInRadians) + 360) % 360;

            Resources res = getResources();
            //stampo gradi
            tvHeading.setText(Integer.toString(azimuthInDegress) + " "+ String.format(res.getString(R.string.degrees)));


            //creo oggetto di rotazione
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            //inizio rotazione immagine
            image.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;



        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}