package it.uniba.di.misurapp;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;

public class LightTool extends AppCompatActivity {

    private int  first =1;
    Handler mHandler = new Handler();
    Runnable run;
    public static DecimalFormat DECIMAL_FORMATTER;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private float maxValue;
    private TextView val;
    float value;
    int newValue;
    private LineChart mChart;
    View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_tool);
        val = (TextView) findViewById(R.id.measure);

        root = findViewById(R.id.chart1);

        //disabilitare il testo al centro della CardView
        mChart = findViewById(R.id.chart1);
        mChart.setNoDataText("");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            Toast.makeText(this, "The device has no light sensor !", Toast.LENGTH_SHORT).show();
            finish();
        }

        //massimo valore del sensore di luminosità
        maxValue = lightSensor.getMaximumRange();

        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                value = sensorEvent.values[0];
                val.setText(value + " lx");

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
                    mHandler.postDelayed(run, 3000);
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                val.setText(value + " lx");
                root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
                new Handler().postDelayed(this, 3000);
                mHandler.removeCallbacks(run);
            }
        }, 3000);
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

}
