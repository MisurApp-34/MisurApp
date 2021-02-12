package it.uniba.di.misurapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.SensorManager;
import android.os.BatteryManager;
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

public class TemperatureTool extends AppCompatActivity  {

    Button addpreferenceButton,removepreferenceButton;
    private TextView value;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private SensorManager sensorManager;
    IntentFilter intentfilter;
    float batteryTemp;
    //variabile per memorizzare i valori sui tre assi, quando si salva la misurazione nello storico generale
    String value1;
    //variabile per memorizzare l'unità di misura della temperatura
    String unit;
    DatabaseManager helper;
    int favourite;
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

        // pulsante salva nei preferiti
        addpreferenceButton = (Button) findViewById(R.id.add_fav);
        removepreferenceButton = (Button) findViewById(R.id.remove_fav);

        // verifico l'entità dell'id nel database
        favourite = helper.getFavoriteTool(6);
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
                helper.favoriteTool(6,1);
                addpreferenceButton.setVisibility(View.GONE);
                removepreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Listener per rimuovere dalla lista preferiti il tool
        removepreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.favoriteTool(6,0);
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
                ToolSave.id_tool=6;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

        buttonAdd = findViewById(R.id.add);
        // variabile in cui verrà memorizzata la misura del sensore
        value = (TextView) findViewById(R.id.measure);
        intentfilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        //importo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            getSupportActionBar().setTitle(R.string.thermometer);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //inventario dei sensori disponibili nel nostro dispositivo.
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        feedMultiple();

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
    }

    private BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Resources res = getResources();

            batteryTemp = (float)(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0))/10;

            // verifico che la temperatura sia uguale all'unità di misura Celsius dopo l'impostazione dei settings:
            // se è vero, controllo che la temperatura della batteria sia entro un livello normale e non anomalo, stampando il relativo valore in gradi celsius
            // se è falso, converto la temperatura da celsius a fahrenheit ed effettuo la verifica sull'intervallo della temperatura, stampando il relativo valore in fahrenheit
            SharedPreferences settings = getSharedPreferences("settings", 0);
            if ((settings.getString("temperature", "Celsius").toString()).equals("Celsius")) {

                if (batteryTemp >= 16 && batteryTemp < 45) {
                    value.setText(batteryTemp + " " + (char) 0x00B0 + " C");
                    unit = " °C";

                } else {
                    value.setText(String.format(res.getString(R.string.attention) + "\n" + batteryTemp + (char) 0x00B0 + "C"));
                }
            }
            else{
                    batteryTemp= (float) ((batteryTemp*1.8)+32);
                if (batteryTemp >= 60.8 && batteryTemp < 113) {
                    value.setText(batteryTemp + " " + (char) 0x00B0 + "F");
                    unit =" °F";   } else {
                    value.setText(String.format(res.getString(R.string.attention) + "\n" + batteryTemp + (char) 0x00B0 + "F"));
                }
            }

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //salvo valore in variabile
                     value1 = String.valueOf(batteryTemp) + " "+unit;

                    //dialog text acquisizione nome salvataggio
                    final EditText input = new EditText(TemperatureTool.this);

                    //apertura dialog inserimento nome salvataggio
                    new AlertDialog.Builder(TemperatureTool.this)
                            .setTitle(getResources().getString(R.string.name_saving))
                            .setMessage(getResources().getString(R.string.insert_name))
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    //acquisisco nome
                                    Editable nome = input.getText();


                                    //imposto nome tool
                                    //String name_tool ="Temperatura";
                                    String name_tool = getResources().getString(R.string.temperature);

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
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    // Do nothing.
                                }
                            }).show();
                }
            });

            //passo i valori impostati per realizzare il grafico
            if (plotData) {
                addEntry(batteryTemp);
                plotData = false;
            }
            //memorizzo il nome dello strumento nella stringa temperature
            TextView details = findViewById(R.id.details);
            details.setText(R.string.temperature);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null) {
            thread.interrupt();
        }
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
                    TemperatureTool.this.registerReceiver(broadcastreceiver,intentfilter);
                }
            }
        });
        thread.start();
    }

    private void addEntry(float event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), event), 0);
            data.notifyDataChanged();

            // mostra il cambiamento dei dati presenti nel chart
            mChart.notifyDataSetChanged();

            //numero di elementi visibili nel chart prima dello scroll automatico
            mChart.setVisibleXRangeMaximum(15);

            // muovi l'ultimo elemento
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    // creo il tracciato nel grafico
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }
}