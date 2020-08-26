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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DecimalFormat;

/**
 * This class implements the SensorEventListener interface. When the application creates the MQTT
 * connection, it registers listeners for the accelerometer and magnetometer sensors.
 * Output from these sensors is used to publish accel event messages.
 */
public class MagneticField extends AppCompatActivity implements SensorEventListener {
    private TextView value;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private SensorManager sensorManager;
    public static DecimalFormat DECIMAL_FORMATTER;
    int first = 1;
    DatabaseManager helper;
    //pulsante aggiunta dati database
    private Button buttonAdd;
    String value1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //oggetto helper database
        helper = new DatabaseManager(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        //setto layout
        setContentView(R.layout.single_tool);
        buttonAdd = findViewById(R.id.add);

        // Storico misurazioni specifico dello strumento selezionato
        Button buttonHistory = (Button) findViewById(R.id.history);
        ToolSave.flag = 1;

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSave.id_tool=2;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

        // variabile in cui verrà memorizzata la misura del sensore
        value = (TextView) findViewById(R.id.measure);

        //importo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            getSupportActionBar().setTitle(R.string.magneticfield);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


//inventario dei sensori disponibili nel nostro dispositivo.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //associo il chart del layout alla variabile LineChart
        mChart = (LineChart) findViewById(R.id.chart1);

        // disabilito label descrizione chart
        mChart.getDescription().setEnabled(false);

        // abilito interazione touch con il grafico
        mChart.setTouchEnabled(true);

        // abilito scaling
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        //abilito pinch to zoom
        mChart.setPinchZoom(true);

        // definisco background chart
        mChart.setBackgroundColor(Color.WHITE);

        //Creo oggetti data e definisco colore del testo dei dati
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // aggiungo inzialmente dati vuoti
        mChart.setData(data);

        // richiamo leggenda
        Legend l = mChart.getLegend();

        // modifico leggenda
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMaximum(10f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(true);

        mChart.getAxisLeft().setDrawGridLines(true);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();

        // recupero il valore selezionato nel grafico, per poter eventualmente salvarlo.
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String value = mChart.getXAxis().getValueFormatter().getFormattedValue(e.getY(), mChart.getXAxis());
            }

            @Override
            public void onNothingSelected() {

            }
        });



    }




    // stampa toast messaggio
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume() {
        super.onResume();


        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);


    }


    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null) {
            thread.interrupt();
        }
        sensorManager.unregisterListener(this);

    }


    // temporizzo la stampa
    Handler mHandler = new Handler();
    double magnitude;// intialize it
    Runnable run = new Runnable() {

        @Override
        public void run() {

            value.setText(DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla");
            mHandler.removeCallbacks(run);

        }
    };




    @Override
    public void onSensorChanged(final SensorEvent event) {

        //se l'evento generato è di tipo  MAGNETIC_FIELD
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // prendo i valori generati dai singoli assi

            float magX = event.values[0];
            float magY = event.values[1];
            float magZ = event.values[2];
            //determino il valore del campo magnetico utilizzando le misure di tutti i tre assi
            magnitude = Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));
            //definisco il numero  di cifre decimali del valore da stampare
            DECIMAL_FORMATTER = new DecimalFormat("#.0");
            //imposto testo nella textview
            if (first == 1) {
                value.setText(DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla");
                first++;
            } else {
                //avvio handler ogni due secondi -- guardare sopra questo metodo
                mHandler.postDelayed(run, 2000);
            }

            TextView details = findViewById(R.id.details);

            details.setText(R.string.magneticfield_details);

            //gestione listner pulsante aggiunta database
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //salvo valore in variabile
                     value1 = String.valueOf(DECIMAL_FORMATTER.format(magnitude)) +" \u00B5Tesla";
                    //dialog text acquisizione nome salvataggio
                    final EditText input = new EditText(MagneticField.this);

                    //apertura dialog inserimento nome salvataggio
                    new AlertDialog.Builder(MagneticField.this)
                            .setTitle(getResources().getString(R.string.name_saving))
                            .setMessage(getResources().getString(R.string.insert_name))
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    //acquisisco nome
                                    Editable nome = input.getText();



                                    //imposto nome tool
                                    String name_tool ="Campo Magnetico";

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
            //invio evento ed attributi al metodo addEntry che aggiungerà gli elementi al grafico
            if (plotData) {
                addEntry(event);
                plotData = false;
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void feedMultiple() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    plotData = true;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            //prelevo le tre misure sui tre assi, inviate con l'oggetto event
            float magX = event.values[0];
            float magY = event.values[1];
            float magZ = event.values[2];
            float magnitude = (float) Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));
            data.addEntry(new Entry(set.getEntryCount(), magnitude), 0);
            data.notifyDataChanged();

            // mostra il cambiamento dei dati presenti nel chart
            mChart.notifyDataSetChanged();

//numero di elementi visibili nel chart prima dello scroll automatico
            mChart.setVisibleXRangeMaximum(15);

            // muovi l'ultimo elemento
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    //creo il tracciato nel grafico
    private LineDataSet createSet() {


        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.setLineWidth(1f);
        set.setColor(Color.BLUE);
        set.setHighlightEnabled(true);
        set.setDrawValues(true);
        set.setDrawCircles(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }
}