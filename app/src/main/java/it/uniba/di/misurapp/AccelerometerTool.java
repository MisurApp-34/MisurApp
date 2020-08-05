package it.uniba.di.misurapp;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

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

public class AccelerometerTool extends AppCompatActivity implements SensorEventListener {

    //variabili per l'impostazione del tempo di visualizzazione della misura
    private int  first =1;
    Handler mHandler = new Handler();
    Runnable run;

    private TextView value;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private SensorManager sensorManager;
    public static DecimalFormat DECIMAL_FORMATTER;


    private float lastAcc = 0.0f; //accelerazione precedentemente misurata dopo il primo movimento
    private float acceleration = 0.0f; //forza di accelerazione di inzio
    private float totAcc = 0.0f; //differenza delle due misurazioni per misurare l'intensità di squotimento


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setto layout
        setContentView(R.layout.single_tool);

        //gravità terrestre, ovvero lo stato di quiete
        lastAcc=SensorManager.GRAVITY_EARTH;
        acceleration=SensorManager.GRAVITY_EARTH;
        // variabile in cui verrà memorizzata la misura del sensore
        value = (TextView) findViewById(R.id.measure);

        //importo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            getSupportActionBar().setTitle(R.string.accelerometer);
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
                String value=  mChart.getXAxis().getValueFormatter().getFormattedValue(e.getY(), mChart.getXAxis());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        //gestore per la temporizzazione della vsualizzazione della misura rilevata dallo strumento
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                value.setText(DECIMAL_FORMATTER.format(totAcc) + " m/s²");
                new Handler().postDelayed(this, 1000);
                mHandler.removeCallbacks(run);
            }
        }, 1000);

    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null) {
            thread.interrupt();
        }
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //se l'evento generato è di tipo  TYPE_ACCELEROMETER
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // prendo i valori generati dai singoli assi
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //determino il valore dell'accelerazione utilizzando le misure di tutti i tre assi
            lastAcc = acceleration;
            acceleration = (x * x) + (y * y) + (z * z);
            float diff = acceleration - lastAcc;
            totAcc = diff*acceleration;

            //definisco il numero  di cifre decimali del valore da stampare
            DECIMAL_FORMATTER = new DecimalFormat("#.0");

            if (first == 1) {
                value.setText(DECIMAL_FORMATTER.format(totAcc) + "m/s²");
                first++;
            } else {
                //avvio handler ogni due secondi -- guardare sopra questo metodo
                mHandler.postDelayed(run, 1000);
            }
            //imposto testo nella textview
            //value.setText(DECIMAL_FORMATTER.format(totAcc) + "m/s²");

            TextView details = findViewById(R.id.details);
            details.setText(R.string.accelerometer);

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
                        Thread.sleep(500);
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
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //determino il valore dell'accelerazione utilizzando le misure di tutti i tre assi
            lastAcc = acceleration;
            acceleration = (x * x) + (y * y) + (z * z);
            float diff = acceleration - lastAcc;
            totAcc = diff*acceleration;

            data.addEntry(new Entry(set.getEntryCount(), acceleration), 0);
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
