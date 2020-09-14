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
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DecimalFormat;
import java.util.Objects;

public class SpiritLevel extends AppCompatActivity implements SensorEventListener {


    SensorManager mSensorManager;
    float yAngle ;
    float zAngle ;
    float xAngle ;
    int DEGREE = 90;
    float sqrt;
    String value1;
    int favourite;
    Button addpreferenceButton,removepreferenceButton;
    LevelView levelView;
    private TextView XYZ_tv;
    public static DecimalFormat DECIMAL_FORMATTER;
    DatabaseManager helper;
    //pulsante aggiunta dati database
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vista
        setContentView(R.layout.single_tool_level);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.level);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        levelView = (LevelView) findViewById(R.id.Direction_View);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        levelView.MAX_ANGLE= DEGREE;
        XYZ_tv = (TextView) findViewById(R.id.measure);
        TextView details = (TextView) findViewById (R.id.details);
        details.setText(R.string.gyroscope);

        //oggetto helper database
        helper = new DatabaseManager(this);

        // pulsante salva nei preferiti
        addpreferenceButton = (Button) findViewById(R.id.add_fav);
        removepreferenceButton = (Button) findViewById(R.id.remove_fav);

        // verifico l'entità dell'id nel database
        favourite = helper.getFavoriteTool(9);
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
                helper.favoriteTool(9,1);
                addpreferenceButton.setVisibility(View.GONE);
                removepreferenceButton.setVisibility(View.VISIBLE);
                removepreferenceButton.getBackground().setColorFilter(Color.parseColor("#ff3333"), PorterDuff.Mode.SRC_IN);

            }
        });

        // Listener per rimuovere dalla lista preferiti il tool
        removepreferenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.favoriteTool(9,0);
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
                ToolSave.id_tool=9;
                Intent saves;
                saves = new Intent(getApplicationContext(),ToolSave.class);
                startActivity(saves);
            }
        });

        buttonAdd = findViewById(R.id.add);
    }

    // stampa toast messaggio
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_LONG).show();
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
            zAngle = values[1]; // ASSE Z
            DECIMAL_FORMATTER = new DecimalFormat("#.#");

            sqrt= (float) Math.sqrt((xAngle*xAngle) + (yAngle*yAngle));
            String append =(DECIMAL_FORMATTER.format(sqrt) + "°");
            XYZ_tv.setText(append);

            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //salvo valore in variabile
                     value1 = String.valueOf(DECIMAL_FORMATTER.format(sqrt))+" °";
                    //dialog text acquisizione nome salvataggio
                    final EditText input = new EditText(SpiritLevel.this);

                    //apertura dialog inserimento nome salvataggio
                    new AlertDialog.Builder(SpiritLevel.this)
                            .setTitle(getResources().getString(R.string.name_saving))
                            .setMessage(getResources().getString(R.string.insert_name))
                            .setView(input)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    //acquisisco nome
                                    Editable nome = input.getText();

                                    //imposto nome tool
                                    String name_tool ="Livella";

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

            levelView.yAngle = values[1];
            levelView.zAngle = values[2];
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
