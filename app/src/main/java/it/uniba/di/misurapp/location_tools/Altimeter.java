package it.uniba.di.misurapp.location_tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

import androidx.annotation.Nullable;
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

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import it.uniba.di.misurapp.R;

public class Altimeter extends AppCompatActivity {

    // variabile per la gestione del Bind
    static boolean status;

    LocationManager locationManager;
    static long startTime, endTime;
    static int p=0;

    // servizio LocationService
    LocationService myService;
    Timer timer;
    private LineChart mChart;
    private Thread thread;
    static double altitudevalue;

    @SuppressLint("StaticFieldLeak")
    static TextView measure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_tool);

        TextView details = findViewById(R.id.details);
        details.setText(R.string.gps);

        measure = findViewById(R.id.measure);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.altitude);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mChart = (LineChart) findViewById(R.id.chart1);

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
                    measure.setText(R.string.getting_location);
                    timer.cancel();
                    bindService();
                } else {
                    measure.setText(R.string.alert_gps_off);
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
