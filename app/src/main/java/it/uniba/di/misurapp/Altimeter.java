package it.uniba.di.misurapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Altimeter extends AppCompatActivity {

    // variabile per la gestione del Bind
    static boolean status;
    // servizio LocationService
    LocationService myService;
    private static String append;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    String value1;
    LocationManager locationManager;
    static long startTime, endTime;
    static int p=1;
    Button addpreferenceButton,removepreferenceButton;
    int favourite;
    private static boolean gps_off;
    Timer timer;
    private LineChart mChart;
    private Thread thread;
    static double altitudevalue;
    DatabaseManager helper;
    //pulsante aggiunta dati database
    private Button buttonAdd;


    @SuppressLint("StaticFieldLeak")
    static TextView measure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_tool);
        mContext = this;
        p=0;

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.altitude);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // oggetto helper database
        helper = new DatabaseManager(this);

        buttonAdd = findViewById(R.id.add);
        TextView details = findViewById(R.id.details);
        details.setText(R.string.gps);
        measure = findViewById(R.id.measure);
        mChart = (LineChart) findViewById(R.id.chart1);
        Button buttonHistory = (Button) findViewById(R.id.history);

        // pulsante salva nei preferiti
        addpreferenceButton = (Button) findViewById(R.id.add_fav);
        removepreferenceButton = (Button) findViewById(R.id.remove_fav);

        // verifico l'entità dell'id nel database
        favourite = helper.getFavoriteTool(7);
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
                helper.favoriteTool(7,1);
                addpreferenceButton.setVisibility(View.GONE);
                removepreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Listener per rimuovere dalla lista preferiti il tool
        removepreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.favoriteTool(7,0);
                addpreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.setVisibility(View.GONE);
                addpreferenceButton.getBackground().setColorFilter(Color.parseColor("#80d10f"), PorterDuff.Mode.SRC_IN);

            }
        });

        ToolSave.flag = 1;
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSave.id_tool=7;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // salvo valore in variabile
                value1 = (altitudevalue)+ " " +getContext().getResources().getString(R.string.meters);

                // dialog text acquisizione nome salvataggio
                final EditText input = new EditText(Altimeter.this);

                // apertura dialog inserimento nome salvataggio
                new AlertDialog.Builder(Altimeter.this)
                        .setTitle(getResources().getString(R.string.name_saving))
                        .setMessage(getResources().getString(R.string.insert_name))
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                //acquisisco nome
                                Editable nome = input.getText();

                                //imposto nome tool
                                String name_tool ="Altitudine";

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

        // Controllo permessi
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                },1000);
            }
        }

        timer = new Timer();

        // Runnable per iterare il controllo sul gps
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (checkGPS()){
                    gps_off = false;
                    measure.setText(R.string.getting_location);
                    timer.cancel();
                    bindService();
                } else {
                    measure.setText(R.string.alert_gps_off);
                    gps_off = true;
                }
            }
        },0,5000);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String value = mChart.getXAxis().getValueFormatter().getFormattedValue(e.getY(), mChart.getXAxis());
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // Setup MPAndroidChart
        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        // Asse x
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        // Asse y
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

        // Listener per il grafico - utile per ottenere il dato nel punto del grafico selezionato
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String value = mChart.getXAxis().getValueFormatter().getFormattedValue(e.getY(), mChart.getXAxis());
            }

            @Override
            public void onNothingSelected(){}
        });

    }

    /**
     * Boolean di controllo presenza del segnale GPS
     * @return true/false - se è attivo o meno il gps
     */
    private boolean checkGPS() {
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Connessione al servizio LocationService
     */
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.LocalBinder binder = (LocationService.LocalBinder)iBinder;
            myService = binder.getService();
            status = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            status = false;
        }
    };

    /**
     * Unbind servizio in "onDestroy" per evitare NullObject
     */
    @Override
    public void onDestroy(){
        if(status) unbindService();
        super.onDestroy();
    }

    /**
     * Scollegamento servizio LocationService
     */
    private void unbindService() {
        if(status) return;
        unbindService(sc);
        status = false;
    }

    /**
     * Bind di LocationService
     */
    private void bindService(){
        if(status) return;
        Intent i = new Intent(getApplicationContext(),LocationService.class);
        bindService(i,sc, BIND_AUTO_CREATE);
        status = true;
        startTime = System.currentTimeMillis();
    }

    // stampa toast messaggio
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        status = false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        status = false;
        return super.onSupportNavigateUp();
    }

    /**
     * Interfaccia con LocationService per ottenere il valore dell'Altitudine
     * @param currHeight Double contenente il valore dell'altitudine
     */
    public static void updateValue(double currHeight){
        altitudevalue = currHeight;
        if (!gps_off) {
           append = new DecimalFormat("#.#").format((altitudevalue));
            String unitofmeasurement = getContext().getResources().getString(R.string.meters);
            append = append + " " + unitofmeasurement;
            measure.setText(append);
        }
    }

    public static Context getContext(){
        return mContext;
    }

    /**
     * Thread per inserire i dati sul grafico gestendo il tempo di stampa
     */
    private void feedMultiple() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        addEntry();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * Inserimento del valore calcolato nel dataset
     */
    private void addEntry() {

        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            float altitude = (float) altitudevalue;
            data.addEntry(new Entry(set.getEntryCount(), altitude), 0);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(10);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    /**
     * Creazione dataset per il grafico lineare
     * @return struttura dataset
     */
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
