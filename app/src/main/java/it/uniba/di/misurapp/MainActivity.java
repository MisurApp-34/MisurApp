package it.uniba.di.misurapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;

    Intent Favorite;
    DatabaseManager DBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);


        //avvio metodo onCreate della classe DatabaseManager per creare le tabelle nel caso in cui non dovessero esistere.
        DatabaseManager helper = new DatabaseManager(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        NavigationView nvDrawer;
        ActionBarDrawerToggle drawerToggle;

        mDrawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        // Inserimento toolbar per rimpiazzare la action bar
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this,mDrawer,toolbar,R.string.drawer_open,R.string.drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        nvDrawer = findViewById(R.id.nvView);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        // Vista drawer
        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);
        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        setupDrawerContent(nvDrawer);



        // Blocchi raggruppamento strumenti generali tramite cardview + Comportamento OnClick
        CardView cardviewambient = findViewById(R.id.CardViewAmbient);
        CardView cardviewmovement = findViewById(R.id.CardViewMovement);
        CardView cardviewposition = findViewById(R.id.CardViewPosition);

        cardviewambient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Ambient;
                Ambient = new Intent(MainActivity.this, AmbientalTools.class);
                startActivity(Ambient);
            }
        });

        cardviewmovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Movement;
                Movement = new Intent(MainActivity.this, MovimentTools.class);
                startActivity(Movement);
            }
        });

        cardviewposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Position;
                Position = new Intent(MainActivity.this, PositionTools.class);
                startActivity(Position);
            }
        });

    }

    // Contenuto Menu Drawer
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                }
        );
    }

    // Drawer Menu - Scelta dell'azione e relativo caricamento activity
    public void selectDrawerItem(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.FragmentStoricoGenerale:
                ToolSave.flag = 0;
                Intent GenericData;
                GenericData = new Intent(MainActivity.this,ToolSave.class);
                startActivity(GenericData);
                break;
            case R.id.FragmentImpostazioni:
                Intent Settings = new Intent(this , Settings.class);
                startActivity(Settings);
                break;
            case R.id.FragmentAbout:
                Intent About = new Intent(this , AboutUs.class);
                startActivity(About);
                break;
        }

    }

    // Intent Preferiti
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.favorite){
            Favorite = new Intent(MainActivity.this, Favorite.class);
            startActivity(Favorite);
        }
        return super.onOptionsItemSelected(item);
    }

    // Icona "cuore" per la pagina dei preferiti
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.favorite_shortcut, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Render icona Hamburger Menu
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

}