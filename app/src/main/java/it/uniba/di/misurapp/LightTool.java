package it.uniba.di.misurapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;

public class LightTool extends AppCompatActivity {

    private int  first =1;
    Handler mHandler = new Handler();
    Runnable run;
    String value1;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private float maxValue;
    private TextView val;
    float value;
    int newValue;
    private LineChart mChart;
    View root;
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
        //oggetto helper database
        helper = new DatabaseManager(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        setContentView(R.layout.single_tool);

        buttonAdd = findViewById(R.id.add);

        // Storico misurazioni specifico dello strumento selezionato
        Button buttonHistory = (Button) findViewById(R.id.history);
        ToolSave.flag = 1;

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSave.id_tool=3;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

        val = (TextView) findViewById(R.id.measure);

        root = findViewById(R.id.chart1);

        //disabilitare il testo al centro della CardView
        mChart = findViewById(R.id.chart1);
        mChart.setNoDataText("");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //importo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            getSupportActionBar().setTitle(R.string.photometer);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (lightSensor == null) {
            Toast.makeText(this, "The device has no light sensor !", Toast.LENGTH_SHORT).show();
            finish();
        }

        //massimo valore del sensore di luminosità
        maxValue = lightSensor.getMaximumRange();

        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                TextView details = findViewById(R.id.details);
                details.setText(R.string.light);
                value = sensorEvent.values[0];
                val.setText(value + " lx");


                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //salvo valore in variabile
                         value1 = String.valueOf(value);

                        //dialog text acquisizione nome salvataggio
                        final EditText input = new EditText(LightTool.this);

                        //apertura dialog inserimento nome salvataggio
                        new AlertDialog.Builder(LightTool.this)
                                .setTitle(getResources().getString(R.string.name_saving))
                                .setMessage(getResources().getString(R.string.insert_name))
                                .setView(input)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        //acquisisco nome
                                        Editable nome = input.getText();


                                        //imposto nome tool
                                        String name_tool ="Intensità Luminosa";

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
                //otteniamo il nuovo valore per la luminosità in lx.
                //mettiamo questo valore nell'intervallo [0, 255] per poter cambiare il colore di sfondo della nostra attività principale in base alla luminosità ambientale.
                newValue = (int) ((255f * value) / maxValue);
                root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));

                if (first == 1) {
                    val.setText(value + " lx");
                    root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
                    first++;
                } else {
                    //avvio handler ogni due secondi -- guardare sopra questo metodo
                    mHandler.postDelayed(run, 2000);
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        //gestore per la temporizzazione della vsualizzazione della misura rilevata dallo strumento
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                val.setText(value + " lx");
                root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
                new Handler().postDelayed(this, 2000);
                mHandler.removeCallbacks(run);
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEventListener);
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
