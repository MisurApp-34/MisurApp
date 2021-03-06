package it.uniba.di.misurapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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

public class AccelerometerTool extends AppCompatActivity implements SensorEventListener {

    private TextView value;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private SensorManager sensorManager;
    DatabaseManager helper;
    // pulsante aggiunta dati database
    private Button buttonAdd;
    Button addpreferenceButton,removepreferenceButton;
    int favourite;
    //variabile per memorizzare i valori sui tre assi, quando si salva la misurazione nello storico generale
    String value1;
    //variabili per memorizzare l'evento del sensore
    private float x,y,z;
    //textview per la visualizzazione dei valori sui tre assi
    private TextView xText, yText, zText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_tool2);

        // variabile in cui verrà memorizzata la misura del sensore
        value = (TextView) findViewById(R.id.measure);
        helper = new DatabaseManager(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        buttonAdd = findViewById(R.id.add);

        // importo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.accelerometer);

        // Assign TextView
        xText = (TextView)findViewById(R.id.measureX);
        yText = (TextView)findViewById(R.id.measureY);
        zText = (TextView)findViewById(R.id.measureZ);

        // pulsante salva nei preferiti
        addpreferenceButton = (Button) findViewById(R.id.add_fav);
        removepreferenceButton = (Button) findViewById(R.id.remove_fav);

        // verifico l'entità dell'id nel database
        favourite = helper.getFavoriteTool(13);


        if (favourite == 1) {

            addpreferenceButton.setVisibility(View.GONE);
            removepreferenceButton.setVisibility(View.VISIBLE);
            removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

        } else {

            addpreferenceButton.setVisibility(View.VISIBLE);
            removepreferenceButton.setVisibility(View.GONE);
            addpreferenceButton.getBackground().setColorFilter(Color.parseColor("#80d10f"), PorterDuff.Mode.SRC_IN);

        }

        // Listener per aggiungere il tool all'insieme di tool preferiti
        addpreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.favoriteTool(13,1);
                addpreferenceButton.setVisibility(View.GONE);
                removepreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Listener per rimuovere dalla lista preferiti il tool
        removepreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.favoriteTool(13,0);
                addpreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.setVisibility(View.GONE);
                addpreferenceButton.getBackground().setColorFilter(Color.parseColor("#80d10f"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Storico misurazioni specifico dello strumento selezionato
        Button buttonHistory = (Button) findViewById(R.id.history);
        ToolSave.flag = 1;

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSave.id_tool=13;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

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

        // Stampa grafico
        feedMultiple();

        // recupero il valore selezionato nel grafico, per poter eventualmente salvarlo.
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String value=  mChart.getXAxis().getValueFormatter().getFormattedValue(e.getY(), mChart.getXAxis());
            }

            @Override
            public void onNothingSelected(){}
        });

    }

    // stampa toast messaggio
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI);
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
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            String textX,textY,textZ;

            // stampo i valori generati dai singoli assi in ogni textView
            textX = "X: " + round(x,2)+ " m/s²";
            textY = "Y: " + round(y,2)+ " m/s²";
            textZ = "Z: " +  round(z,2)+ " m/s²";

            xText.setText(textX);
            yText.setText(textY);
            zText.setText(textZ);

            TextView details = findViewById(R.id.details);
            details.setText(R.string.accelerometer);

            //gestione listner pulsante aggiunta database
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //salvo valore in variabile
                    value1 = "X: "+(round(x,2))+" m/s² Y: "+(round(y,2))+"  m/s² Z: "+(round(z,2)+"  m/s²");

                    //dialog text acquisizione nome salvataggio
                    final EditText input = new EditText(AccelerometerTool.this);

                    //apertura dialog inserimento nome salvataggio
                    new AlertDialog.Builder(AccelerometerTool.this)
                            .setTitle(getResources().getString(R.string.name_saving))
                            .setMessage(getResources().getString(R.string.insert_name))
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    //acquisisco nome
                                    Editable nome = input.getText();

                                    //imposto nome tool
                                    //String name_tool ="Accelerazione";
                                    String name_tool = getResources().getString(R.string.accelerometer);

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
                                    }
                                }
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

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
            ILineDataSet sety = data.getDataSetByIndex(1);
            ILineDataSet setz = data.getDataSetByIndex(2);

            if (set == null || sety == null || setz == null) {
                set = createSet();
                sety = createSety();
                setz = createSetz();
                data.addDataSet(set);
                data.addDataSet(sety);
                data.addDataSet(setz);
            }

            //prelevo le tre misure sui tre assi, inviate con l'oggetto event
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            data.addEntry(new Entry(set.getEntryCount(), x), 0);
            data.addEntry(new Entry(sety.getEntryCount(), y), 1);
            data.addEntry(new Entry(setz.getEntryCount(), z), 2);
            data.notifyDataChanged();

            // mostra il cambiamento dei dati presenti nel chart
            mChart.notifyDataSetChanged();

            //numero di elementi visibili nel chart prima dello scroll automatico
            mChart.setVisibleXRangeMaximum(15);

            // muovi l'ultimo elemento
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    // creo il tracciato nel grafico, sui valori dell'asse X
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "X");
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

    // creo il tracciato nel grafico, sui valori dell'asse Y
    private LineDataSet createSety() {

        LineDataSet sety = new LineDataSet(null, "Y");
        sety.setAxisDependency(YAxis.AxisDependency.RIGHT);
        sety.setLineWidth(1f);
        sety.setColor(Color.GREEN);
        sety.setHighlightEnabled(true);
        sety.setDrawValues(true);
        sety.setDrawCircles(true);
        sety.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        sety.setCubicIntensity(0.2f);

        return sety;
    }

    // creo il tracciato nel grafico, sui valori dell'asse Z
    private LineDataSet createSetz() {

        LineDataSet setz = new LineDataSet(null, "Z");
        setz.setAxisDependency(YAxis.AxisDependency.RIGHT);
        setz.setLineWidth(1f);
        setz.setColor(Color.RED);
        setz.setHighlightEnabled(true);
        setz.setDrawValues(true);
        setz.setDrawCircles(true);
        setz.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        setz.setCubicIntensity(0.2f);

        return setz;
    }

    //funzione per l'arrontondamento delle cifre decimali definite in scale
    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }
}
