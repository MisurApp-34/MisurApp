package it.uniba.di.misurapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

public class Speed extends AppCompatActivity {

    static boolean status;
    private static String unitofmeasurement;
    LocationManager locationManager;
    static double speed;
    static long startTime, endTime;
    static int p = 1;
    String value1;
    private static boolean gps_off;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    Timer timer;
    private Thread thread;
    LocationService myService;
    private LineChart mChart;

    @SuppressLint("StaticFieldLeak")
    static TextView measure;
    DatabaseManager helper;
    //pulsante aggiunta dati database
    private Button buttonAdd;
    // stampa toast messaggio
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_tool);

        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);

        if ((settings.getString("speed", "Km/h").toString()).equals("Km/h")) {

            unitofmeasurement = "Km/h";

        }
        else if ((settings.getString("speed", "").toString()).equals("Mph")) {
            unitofmeasurement = "Mph";
        }

        //oggetto helper database
        helper = new DatabaseManager(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        // Storico misurazioni specifico dello strumento selezionato
        Button buttonHistory = (Button) findViewById(R.id.history);
        ToolSave.flag = 1;
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSave.id_tool=12;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

        buttonAdd = findViewById(R.id.add);

        mContext = this;
        p=0;

        TextView details = findViewById(R.id.details);
        details.setText(R.string.gps);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.speed);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        measure = findViewById(R.id.measure);

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
                    gps_off = false;
                    measure.setText(R.string.getting_location);
                    timer.cancel();
                    bindService();
                } else {
                    gps_off = true;
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



        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = getSharedPreferences("settings", 0);

                if ((settings.getString("speed", "").toString()).equals("Km/h")) {

                    speed = speed*3.6;

                }
                else if ((settings.getString("speed", "").toString()).equals("Mph")) {
                    speed = speed*2.24;
                }
                //salvo valore in variabile
                value1 = String.valueOf(speed) + "" +unitofmeasurement;
                //dialog text acquisizione nome salvataggio
                final EditText input = new EditText(Speed.this);

                //apertura dialog inserimento nome salvataggio
                new AlertDialog.Builder(Speed.this)
                        .setTitle(getResources().getString(R.string.name_saving))
                        .setMessage(getResources().getString(R.string.insert_name))
                        .setView(input)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                //acquisisco nome
                                Editable nome = input.getText();



                                //imposto nome tool
                                String name_tool ="Velocità";

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


    /**
     * Interfaccia con LocationService.java per la cattura della velocità instantenea in metri al seconodo
     * @param currSpeed velocità instantanea calcolata mediante GPS
     */
    public static void  getSpeed(double currSpeed){

        speed = currSpeed;

        if (!gps_off){

            if(unitofmeasurement.equals("Km/h")) speed = speed*3.6;
            else if(unitofmeasurement.equals("Mph")) speed = speed*2.24;

            String append = new DecimalFormat("#.#").format((speed));
            append = append + " "+unitofmeasurement;
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
            //double Actspeed = (double) speed;
            float speed_float = (float) speed;
            data.addEntry(new Entry(set.getEntryCount(), speed_float), 0);
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

    @Override
    public void onBackPressed() {
        status=false;

        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        status=false;

        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
