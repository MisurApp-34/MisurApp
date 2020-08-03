package it.uniba.di.misurapp;


import android.content.SharedPreferences;
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
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class PressureTool extends AppCompatActivity implements SensorEventListener {
    private TextView value;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private SensorManager sensorManager;
    public static DecimalFormat DECIMAL_FORMATTER;
    int first = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setto layout
        setContentView(R.layout.single_tool);

        // variabile in cui verrà memorizzata la misura del sensore
        value = (TextView) findViewById(R.id.measure);

        //importo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            getSupportActionBar().setTitle(R.string.pressure);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


//inventario dei sensori disponibili nel nostro dispositivo.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //associo il chart del layout alla variabile LineChart
        mChart = findViewById(R.id.chart1);

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

    @Override
    protected void onResume() {
        super.onResume();

        //
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
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

    // temporizzo la stampa
    Handler mHandler = new Handler();
    double pressione;// intialize it
    Runnable run = new Runnable() {

        @Override
        public void run() {

            value.setText(String.format("%.1f hPa", pressione));
            mHandler.removeCallbacks(run);

        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        if ((settings.getString("pressure", "").toString()).equals("hPa")) {

            if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
                // prendo i valori generati dai singoli assi
                pressione = event.values[0];
                if (first == 1) {
                    value.setText(String.format("%.1f hPa", pressione));
                    first++;
                } else {
                    //avvio handler ogni due secondi -- guardare sopra questo metodo
                    mHandler.postDelayed(run, 2000);
                }

                TextView details = findViewById(R.id.details);
                details.setText(R.string.pressure_details);

                //invio evento ed attributi al metodo addEntry che aggiungerà gli elementi al grafico
                if (plotData) {
                    addEntry(event);
                    plotData = false;
                }
            }
        } else {
            if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
                // prendo i valori generati dai singoli assi
                float[] values = event.values;
                value.setText(String.format("%.1f Pascal", values[0] * 100));

                TextView details = findViewById(R.id.details);
                details.setText(R.string.pressure_details);

                //invio evento ed attributi al metodo addEntry che aggiungerà gli elementi al grafico
                if (plotData) {
                    addEntry(event);
                    plotData = false;
                }
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
            float[] values = event.values;


            SharedPreferences settings = getSharedPreferences("settings", 0);
            if ((settings.getString("pressure", "").toString()).equals("hPa")) {

                data.addEntry(new Entry(set.getEntryCount(), (float) round(values[0], 1)), 0);

            } else {
                data.addEntry(new Entry(set.getEntryCount(), (float) round(values[0] * 100, 0)), 0);

            }
            //prelevo le tre misure sui tre assi, inviate con l'oggetto event

            data.notifyDataChanged();

            // mostra il cambiamento dei dati presenti nel chart
            mChart.notifyDataSetChanged();

//numero di elementi visibili nel chart prima dello scroll automatico
            mChart.setVisibleXRangeMaximum(15);

            // muovi l'ultimo elemento
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    //troncamento numeri decimali
    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
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