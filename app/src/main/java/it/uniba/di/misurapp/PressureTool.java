package it.uniba.di.misurapp;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
        //setto layout
        setContentView(R.layout.single_tool);
        //oggetto helper database
        helper = new DatabaseManager(this);
        SQLiteDatabase db = helper.getReadableDatabase();



        buttonAdd = findViewById(R.id.add);
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
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null){
            feedMultiple();
        }
        else {
            Resources res = getResources();

            value.setText(R.string.missing_sensor);
            TextView details = findViewById(R.id.details);
            details.setText(R.string.pressure_details);
        }

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


    //registro listner sensori
    @Override
    protected void onResume() {
        super.onResume();

        //
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_UI);

    }

    //interrompo thread e de-registro
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

        //importo preferenze dalle impostazioni per visualizzare dati in hPa o  Pascal
        SharedPreferences settings = getSharedPreferences("settings", 0);
        if ((settings.getString("pressure", "").toString()).equals("hPa")) {

            if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
                // prendo i valori generati dai singoli assi
                pressione = event.values[0];

                //controllo su variabile globale per procedere all'avvio dell'activity subito con la stampa del valore senza attendere 2 secondi
                if (first == 1) {
                    value.setText(String.format("%.1f hPa", pressione));
                    first++;
                } else {
                    //avvio handler ogni due secondi -- guardare sopra questo metodo
                    mHandler.postDelayed(run, 2000);
                }

                TextView details = findViewById(R.id.details);
                details.setText(R.string.pressure_details);



                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //dialog text acquisizione nome salvataggio
                        final EditText input = new EditText(PressureTool.this);

                        //apertura dialog inserimento nome salvataggio
                        new AlertDialog.Builder(PressureTool.this)
                                .setTitle(getResources().getString(R.string.name_saving))
                                .setMessage(getResources().getString(R.string.insert_name))
                                .setView(input)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        //acquisisco nome
                                        Editable nome = input.getText();

                                        //salvo valore in variabile
                                        String value1 = String.valueOf(pressione);

                                        //imposto nome tool
                                        String name_tool ="Pressione";

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


    //gestisco thread per la stampa sul grafico
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

    //inserisco dati sul grafico
    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            //prelevo valori dall'evento
            float[] values = event.values;



            //catturo preferenza dalle impostazioni circa l'unità di misura
            SharedPreferences settings = getSharedPreferences("settings", 0);
            if ((settings.getString("pressure", "").toString()).equals("hPa")) {

                data.addEntry(new Entry(set.getEntryCount(), (float) round(values[0], 1)), 0);

            } else {
                data.addEntry(new Entry(set.getEntryCount(), (float) round(values[0] * 100, 1)), 0);

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

  // pulsante indietro
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}