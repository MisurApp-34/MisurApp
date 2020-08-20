package it.uniba.di.misurapp.location_tools;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;
import java.util.Objects;

import it.uniba.di.misurapp.R;
import it.uniba.di.misurapp.location_tools.spirit_level_view.LevelView;

public class SpiritLevel extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    float yAngle ;
    float zAngle ;
    float xAngle ;
    int DEGREE = 90;
    LevelView levelView;
    private TextView XYZ_tv;
    public static DecimalFormat DECIMAL_FORMATTER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vista
        setContentView(R.layout.single_tool_level);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        levelView = (LevelView) findViewById(R.id.Direction_View);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        levelView.MAX_ANGLE= DEGREE;
        XYZ_tv = (TextView) findViewById(R.id.measure);
        TextView details = (TextView) findViewById (R.id.details);
        details.setText(R.string.gyroscope);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.level);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Gestione sensori
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE ), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    /**
     * in "onSensorChanged()" si ottengono i valori da stampare nelle TV e l'inclinazione della livella
     * @param event definizione del tipo di evento del sensore
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_ORIENTATION) {
            float[] values = event.values;
            yAngle = values[2]; // ASSE X
            xAngle = values[1]; // ASSE Z
            DECIMAL_FORMATTER = new DecimalFormat("#.0");

            float sqrt= (float) Math.sqrt((xAngle*xAngle) + (yAngle*yAngle));
            String append =(DECIMAL_FORMATTER.format(sqrt) + "°");
            XYZ_tv.setText(append);
            levelView.yAngle = values[2]; // TODO Fix direzione bolla
            levelView.xAngle = values[1];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
