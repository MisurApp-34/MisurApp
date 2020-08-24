package it.uniba.di.misurapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.Toolbar;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class ToolSave extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_list);

        // Apertura db in lettura
        DatabaseManager dbmanager = new DatabaseManager(this);
        dbmanager.openDataBase();

        // Cattura dati presenti nel database
        String[][] text = dbmanager.getData();
        dbmanager.close();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.save);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Vista lista
        ListView listView = findViewById(R.id.my_list);

        // Creazione array dinamici
        String[] mTitle = new String[DatabaseManager.rows];
        String[] mDate = new String[DatabaseManager.rows];
        String[] mToolname = new String[DatabaseManager.rows];
        String[] mvalue = new String[DatabaseManager.rows];
        int[] images = new int[DatabaseManager.rows];
        ImageView trash = (ImageView) findViewById(R.id.trash);

        // Lettura della matrice e assegnazione valori ai rispettivi array
        for (int i = 0; i < DatabaseManager.rows; i++) {
            mTitle[i] = text[1][i];
            mDate[i] = text[3][i];
            mToolname[i] = text[4][i];
            mvalue[i] = text[5][i];
            int append = parseInt(text[2][i]);
            images[i] = getIcon(append);
        }

        // Inflate adapter
        ToolSaveAdapter adapter = new ToolSaveAdapter(this, mTitle, mDate, mvalue, mToolname, images, trash);
        listView.setAdapter(adapter);
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
    public int getIcon(int i){
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
}