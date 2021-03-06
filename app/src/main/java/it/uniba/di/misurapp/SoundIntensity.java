package it.uniba.di.misurapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaRecorder;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;

import java.io.IOException;

public class SoundIntensity extends AppCompatActivity {
    public static final int RECORD_AUDIO = 0;
    TextView mStatusView;
    MediaRecorder mRecorder;
    Thread runner;
    double value;
    private LineChart mChart;
    //valore decibel calcolato
    String value1;
    //pulsante di aggiunta preferenza e rimozione preferenza
    Button addpreferenceButton,removepreferenceButton;
    //flag controllo database per verificare se strumento è preferito o no (valori 0/1)
    int favourite;
    //supporto al thread
    final Handler mHandler = new Handler();
    DatabaseManager helper;
    //pulsante aggiunta dati database
    private Button buttonAdd;
    // stampa toast messaggio
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
    }


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

        //oggetto helper database
        helper = new DatabaseManager(this);

        // pulsante salva nei preferiti
        addpreferenceButton = (Button) findViewById(R.id.add_fav);
        removepreferenceButton = (Button) findViewById(R.id.remove_fav);

        // verifico l'entità dell'id nel database
        favourite = helper.getFavoriteTool(4);

        //strumento impostato comre preferito
        if (favourite == 1) {

            //nascondo pulsante aggiungi
            addpreferenceButton.setVisibility(View.GONE);
            //mostro pulsante rimuovi
            removepreferenceButton.setVisibility(View.VISIBLE);
            removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

        } else { //strumento non preferito

            //mostro pulsante aggiungi
            addpreferenceButton.setVisibility(View.VISIBLE);
            //nascondo pulsante rimuovi

            removepreferenceButton.setVisibility(View.GONE);
            addpreferenceButton.getBackground().setColorFilter(Color.parseColor("#80d10f"), PorterDuff.Mode.SRC_IN);

        }

        // Listener per aggiungere il tool all'insieme di tool preferiti
        addpreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set strumento preferito nel database
                helper.favoriteTool(4,1);
                addpreferenceButton.setVisibility(View.GONE);
                removepreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Listener per rimuovere dalla lista preferiti il tool
        removepreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set strumento preferito nel database

                helper.favoriteTool(4,0);
                addpreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.setVisibility(View.GONE);
                addpreferenceButton.getBackground().setColorFilter(Color.parseColor("#80d10f"), PorterDuff.Mode.SRC_IN);

            }
        });

        // pulsante Storico misurazioni specifico dello strumento selezionato
        Button buttonHistory = (Button) findViewById(R.id.history);
        ToolSave.flag = 1;

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //avvio intent con id tool
                ToolSave.id_tool=4;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

        //riferimento pulsante aggiungi salvataggio
        buttonAdd = findViewById(R.id.add);

        //stampa valore calcolato
        mStatusView = (TextView) findViewById(R.id.measure);
        mChart = findViewById(R.id.chart1);
        mChart.setNoDataText("");
        mChart.setNoDataTextColor(Color.BLACK);
        TextView details = findViewById(R.id.details);
        //
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

        // gestione thread per l'acquisizione dei segnali dal microfono
        if (runner == null) {
            runner = new Thread() {
                public void run() {
                    while (runner != null) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
        }
    }

    // chiedo permessi micrfono e procedo nel registrare l'audio
    public void onResume() {
        super.onResume();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.RECORD_AUDIO,
                },1000);
            }

        } else {

            startRecorder();

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    value1 = String.valueOf(value) + " dB";

                    //dialog text acquisizione nome salvataggio
                    final EditText input = new EditText(SoundIntensity.this);

                    //apertura dialog inserimento nome salvataggio
                    new AlertDialog.Builder(SoundIntensity.this)
                            .setTitle(getResources().getString(R.string.name_saving))
                            .setMessage(getResources().getString(R.string.insert_name))
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    //acquisisco nome
                                    Editable nome = input.getText();

                                    //salvo valore in variabile

                                    //imposto nome tool
                                    //String name_tool ="Intensità sonora";
                                    String name_tool = getResources().getString(R.string.phonometer);

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
        }
    }

    // stoppo registrazione audio
    public void onPause() {
        super.onPause();
        stopRecorder();
    }

    // preparo la registrazione dell'audio circostante il quale verrà processato per trovare l'intensità  del suono
    public void startRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (IOException | SecurityException ioe) {
                ioe.printStackTrace();
            }

            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                e.printStackTrace();
            }
        }
    }


    //stop registrazione
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    // aggiorno valore textview
    public void updateTv() {
        Resources res = getResources();
        //calcolo valore decibel captato dal microfono
        value = 20 * Math.log10((double) Math.abs(getAmplitude()));
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
        } else {
            mStatusView.setText(value + " dB");
        }
    }

    // ricavo massima amplificazione
    public double getAmplitude() {
        if (mRecorder != null) return (mRecorder.getMaxAmplitude());
        else return 0;
    }

    // troncamento numeri decimali
    public static double round(double value, int scale) {
        return Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale);
    }


    //pulsante indietro
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }
}