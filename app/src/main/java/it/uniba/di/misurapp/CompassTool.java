package it.uniba.di.misurapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CompassTool extends AppCompatActivity implements SensorEventListener {

//definisco l'immagine
private ImageView image;
private String cardinale;
    // device sensor manager
    private SensorManager mSensorManager;

    TextView tvHeading;
    String value1;

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
        setContentView(R.layout.single_tool_nochart);
        //oggetto helper database
        helper = new DatabaseManager(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        //set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Storico misurazioni specifico dello strumento selezionato
        Button buttonHistory = (Button) findViewById(R.id.history);
        ToolSave.flag = 1;

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSave.id_tool=1;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

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
        buttonAdd = findViewById(R.id.add);

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

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Math.abs(mCurrentDegree)>=0 && Math.abs(mCurrentDegree)<45) {
                    cardinale = "N";
                }
                if(Math.abs(mCurrentDegree)>=45 && Math.abs(mCurrentDegree)<90)
                {
                    cardinale="NE";
                }
                if(Math.abs(mCurrentDegree)>=90 && Math.abs(mCurrentDegree)<135)
                {
                    cardinale="E";
                }
                if(Math.abs(mCurrentDegree)>=135 && Math.abs(mCurrentDegree)<180)
                {
                    cardinale="SE";
                }
                if(Math.abs(mCurrentDegree)>=180 && Math.abs(mCurrentDegree)<225)
                {
                    cardinale="S";
                }
                if(Math.abs(mCurrentDegree)>=225 && Math.abs(mCurrentDegree)<270)
                {
                    cardinale="SW";
                }
                if(Math.abs(mCurrentDegree)>=270 && Math.abs(mCurrentDegree)<315)
                {
                    cardinale="W";
                }
                if(Math.abs(mCurrentDegree)>=315 && Math.abs(mCurrentDegree)<360)
                {
                    cardinale="NW";
                }
                if(Math.abs(mCurrentDegree)==360)
                {
                    cardinale="N";
                }
                //salvo valore in variabile
                 value1 = Math.abs(mCurrentDegree) + "° "+ cardinale;
                //dialog text acquisizione nome salvataggio
                final EditText input = new EditText(CompassTool.this);

                //apertura dialog inserimento nome salvataggio
                new AlertDialog.Builder(CompassTool.this)
                        .setTitle(getResources().getString(R.string.name_saving))
                        .setMessage(getResources().getString(R.string.insert_name))
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                //acquisisco nome
                                Editable nome = input.getText();




                                //imposto nome tool
                                String name_tool ="Bussola";

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