package it.uniba.di.misurapp;

import java.io.IOException;


import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.github.mikephil.charting.charts.LineChart;

public class SoundIntensity extends AppCompatActivity {
    public static final int RECORD_AUDIO = 0;
    TextView mStatusView;
    MediaRecorder mRecorder;
    Thread runner;

    private LineChart mChart;

    final Handler mHandler = new Handler();


    //ogni thread esegue il metodo run ogni 300ms
    final Runnable updater = new Runnable() {

        public void run() {
            updateTv();
        }

        ;
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.single_tool);
        mStatusView = (TextView) findViewById(R.id.measure);
        mChart = findViewById(R.id.chart1);
        mChart.setNoDataText("");
        mChart.setNoDataTextColor(Color.BLACK);
        TextView details = findViewById(R.id.details);
        details.setText(R.string.decibel_details);
        //importo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try {
            getSupportActionBar().setTitle(R.string.sound_intensity);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }




        //gestione thread per l'acquisizione dei segnali dal microfono
        if (runner == null) {
            runner = new Thread() {
                public void run() {
                    while (runner != null) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                        }
                        ;
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
        }
    }


    //chiedo permessi micrfono e procedo nel registrare l'audio
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, SoundIntensity.RECORD_AUDIO);

        } else {

            startRecorder();

        }
    }

    //stoppo registrazione audio
    public void onPause() {
        super.onPause();
        stopRecorder();
    }

    //preparo la registrazione dell'audio circostante il quale verrà processato per trovare l'intensità  del suono
    public void startRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {


            } catch (java.lang.SecurityException e) {

            }
            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {

            }

        }

    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    //aggiorno valore textview
    public void updateTv() {
        Resources res = getResources();
        //calcolo valore decibel captato dal microfono
        double value = 20 * Math.log10((double) Math.abs(getAmplitude()));
        //arrotondo valore
        value = round(value, 1);


        //classificazione dei risultati restituendo nella parte superiore dell'activity il colore relativo all'acutezza del suono e al tipo di disturbo avvertito
        if (value <= 35) {

            mChart.setNoDataText(String.format(res.getString(R.string.decibel_level_1)));
            mChart.setBackgroundColor(Color.parseColor("#c1ed2c"));

        }
        if (value >= 36 && value <= 50) {
            mChart.setNoDataText(String.format(res.getString(R.string.decibel_level_2)));

            mChart.setBackgroundColor(Color.GREEN);

        }
        if (value >= 51 && value <= 80) {
            mChart.setNoDataText(String.format(res.getString(R.string.decibel_level_3)));

            mChart.setBackgroundColor(Color.parseColor("#f4f532"));

        }
        if (value >= 81 && value <= 110) {
            mChart.setNoDataText(String.format(res.getString(R.string.decibel_level_4)));

            mChart.setBackgroundColor(Color.parseColor("#f3b035"));

        }
        if (value >= 111) {
            mChart.setNoDataText(String.format(res.getString(R.string.decibel_level_5)));

            mChart.setBackgroundColor(Color.RED);

        }
        //visualizzo valori decibel
        if (value < 0) {
            mStatusView.setText(R.string.initialization);

        } else
            mStatusView.setText(value + " dB");

    }

    //ricavo massima amplificazione
    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    //troncamento numeri decimali
    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }


    //tasto back
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}