package it.uniba.di.misurapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

public class LightTool extends AppCompatActivity {

    Handler mHandler = new Handler();
    Runnable run;
    //variabile per memorizzare i valori sui tre assi, quando si salva la misurazione nello storico generale
    String value1;
    //controllo su variabile globale per procedere all'avvio dell'activity subito con la stampa del valore senza attendere 2 secondi
    private int  first =1;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;
    private float maxValue;
    private TextView val;
    Button addpreferenceButton,removepreferenceButton;
    int favourite;
    //variabile per memorizzare il risultato dell'evento del sensore
    float value;
    //variabile per memorizzare il valore numerico per il colore dello sfondo della vista
    int newValue;
    private LineChart mChart;
    View root;
    DatabaseManager helper;
    //pulsante aggiunta dati database
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_tool);

        //importo toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.photometer);

        // oggetto helper database
        helper = new DatabaseManager(this);

        // pulsante salva nei preferiti
        addpreferenceButton = (Button) findViewById(R.id.add_fav);
        removepreferenceButton = (Button) findViewById(R.id.remove_fav);

        // verifico l'entità dell'id nel database
        favourite = helper.getFavoriteTool(3);
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
                helper.favoriteTool(3,1);
                addpreferenceButton.setVisibility(View.GONE);
                removepreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Listener per rimuovere dalla lista preferiti il tool
        removepreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.favoriteTool(3,0);
                addpreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.setVisibility(View.GONE);
                addpreferenceButton.getBackground().setColorFilter(Color.parseColor("#80d10f"), PorterDuff.Mode.SRC_IN);

            }
        });

        buttonAdd = findViewById(R.id.add);

        // Storico misurazioni specifico dello strumento selezionato
        Button buttonHistory = (Button) findViewById(R.id.history);
        ToolSave.flag = 1;

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolSave.id_tool=3;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

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
                TextView details = findViewById(R.id.details);
                details.setText(R.string.light);
                value = sensorEvent.values[0];
                val.setText(value + " lx");

                buttonAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //salvo valore in variabile
                         value1 = String.valueOf(value) +" lx";

                        //dialog text acquisizione nome salvataggio
                        final EditText input = new EditText(LightTool.this);

                        //apertura dialog inserimento nome salvataggio
                        new AlertDialog.Builder(LightTool.this)
                                .setTitle(getResources().getString(R.string.name_saving))
                                .setMessage(getResources().getString(R.string.insert_name))
                                .setView(input)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        //acquisisco nome
                                        Editable nome = input.getText();

                                        //imposto nome tool
                                        // String name_tool ="Intensità Luminosa";
                                        String name_tool = getResources().getString(R.string.photometer);

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

                // otteniamo il nuovo valore per la luminosità in lx.
                // mettiamo questo valore nell'intervallo [0, 255] per poter cambiare il colore di sfondo della nostra attività principale in base alla luminosità ambientale.
                newValue = (int) ((255f * value) / maxValue);
                root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));

                if (first == 1) {
                    val.setText(value + " lx");
                    root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
                    first++;
                } else {
                    //avvio handler ogni due secondi -- guardare sopra questo metodo
                    mHandler.postDelayed(run, 2000);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}

        };

        // gestore per la temporizzazione della vsualizzazione della misura rilevata dallo strumento
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                val.setText(value + " lx");
                root.setBackgroundColor(Color.rgb(newValue, newValue, newValue));
                new Handler().postDelayed(this, 2000);
                mHandler.removeCallbacks(run);
            }
        }, 2000);
    }

    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }

}
