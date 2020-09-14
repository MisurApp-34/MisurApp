package it.uniba.di.misurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import static java.lang.Integer.parseInt;

public class ToolSave extends AppCompatActivity {

    // Nome salvataggio
    public static EditText editable_name;
    // context
    static Context context;
    // Lista salvataggi
    public static ListView listView;
    // Flag per decidere se stampare tutti i dati o solo quelli relativi a uno strumento
    public static int flag;
    // Id strumento di cui ricavare salvataggi
    public static int id_tool;
    // Titolo salvataggio
    static String[] mTitle;
    // Data salvataggio
    static String[] mDate;
    // Nome strumento del salvataggio
    static String[] mToolname;
    // Valore rilevato salvataggio
    static String[] mvalue;
    // Icona salvataggio relativa allo strumento
    static int[] images;
    // Id salvatagigo
    static int[] mId;
    // Immagine cestino
    static ImageView trash;
    // Image modifica
    static ImageView upload;
    // Adapter per la
    static ToolSaveAdapter adapter;
    // TV mancanza salvataggi
    static TextView nosave;
    // Db
    static DatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_list);

        context = getApplicationContext();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.save);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        nosave = (TextView)findViewById(R.id.textView);

        db = new DatabaseManager(this);
        editable_name = new EditText(ToolSave.this);

        // Vista lista
        listView = findViewById(R.id.my_list);

        trash = (ImageView) findViewById(R.id.trash);
        upload = (ImageView) findViewById(R.id.upload);

        // Metodo per ottenere da database i dati necessari
        getAll();
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

    /**
     * Metodo per inserire nella lista dei salvataggi le icone dei vari strumenti utilizzati
     * @param i indice relativo all'id dello strumento
     * @return int relativo al drawable legato all'indice strumento
     */
    public static int getIcon(int i){
        switch (i){
            case 1 :  return R.drawable.compass;
            case 2 :  return R.drawable.magnetic_fild;
            case 3 :  return R.drawable.light;
            case 4 :  return R.drawable.sound;
            case 5 :  return R.drawable.barometor;
            case 6 :  return R.drawable.thermometer;
            case 7 :  return R.drawable.altitude;
            case 8 :  return R.drawable.gps;
            case 9 :  return R.drawable.level;
            case 10 : return R.drawable.proxyimity;
            case 11 : return R.drawable.gravity;
            case 12 : return R.drawable.speedometer;
            case 13 : return R.drawable.accelerometer;
            default : return 0;
        }
    }

    /**
     * Metodo per ottenere da database le informazioni relative alle misurazioni effettuate;
     * Si occupa anche dell'inflate dell'adapter.
     */
    public static void getAll(){

        // Apertura db in lettura
        DatabaseManager dbmanager = new DatabaseManager(context);
        dbmanager.openDataBase();

        String[][] text;

        // Tutti i dati o i dati di strumento specifico
        if (flag == 0) {
            text = dbmanager.getData();
        } else {
            text = dbmanager.getToolData(id_tool);
        }

        // Controlli sui dati presi dal db
        int flag = DatabaseManager.rows;
        if (flag==0) {
            nosave.setVisibility(TextView.VISIBLE);
        } else
            nosave.setVisibility(TextView.INVISIBLE);


        dbmanager.close();

        // Setup textview
        mTitle = new String[DatabaseManager.rows];
        mDate = new String[DatabaseManager.rows];
        mToolname = new String[DatabaseManager.rows];
        mvalue = new String[DatabaseManager.rows];
        images = new int[DatabaseManager.rows];
        mId = new int[DatabaseManager.rows];

        // Creazione array dinamici
        // Lettura della matrice e assegnazione valori ai rispettivi array
        for (int i = 0; i < DatabaseManager.rows; i++) {
            mId[i] = parseInt(text[0][i]);
            mTitle[i] = text[1][i];
            mDate[i] = text[3][i];
            mToolname[i] = text[4][i];
            mvalue[i] = text[5][i];
            int append = parseInt(text[2][i]);
            images[i] = getIcon(append);
        }

        adapter = new ToolSaveAdapter(context, mTitle, mDate, mvalue, mToolname, images, trash, upload);
        listView.setAdapter(adapter);
    }

    /**
     * Alla rimozione di un elemento ricarica la lista dei salvataggi
     */
    public static void removeElement(){
        getAll();
    }
}