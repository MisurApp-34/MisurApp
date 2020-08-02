package it.uniba.di.misurapp;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Barra di ricerca Custom - Mostra l'intera barra di ricerca, non la singola icona
        // TODO Implementare la ricerca per elementi
        final SearchView searchView = findViewById(R.id.search_bar);
        searchView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                searchView.setIconified(false);
            }
        });

        // Blocchi raggruppamento strumenti generali tramite cardview + Comportamento OnClick
        CardView cardviewambient = findViewById(R.id.CardViewAmbient);
        CardView cardviewmovement = findViewById(R.id.CardViewMovement);
        CardView cardviewposition = findViewById(R.id.CardViewPosition);

        cardviewambient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Activity Strumenti Ambientali da collegare - Sostituire il Toast con il pezzo di codice commentato modificando eventuali nomi di classe
              //  Toast.makeText(MainActivity.this,"Activity Strumenti ambientali \n DA COLLEGARE",Toast.LENGTH_SHORT).show();

                Intent Ambient;
                Ambient = new Intent(MainActivity.this, AmbientalTools.class);
                startActivity(Ambient);

            }
        });

        cardviewmovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Activity Strumenti di Movimento da collegare - Sostituire il Toast con il pezzo di codice commentato modificando eventuali nomi di classe
                //oast.makeText(MainActivity.this,"Activity Strumenti di Movimento \n DA COLLEGARE",Toast.LENGTH_SHORT).show();

                Intent Movement;
                Movement = new Intent(MainActivity.this, MovimentTools.class);
                startActivity(Movement);

            }
        });

        cardviewposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Activity Strumenti di Posizione da collegare - Sostituire il Toast con il pezzo di codice commentato modificando eventuali nomi di classe
                //Toast.makeText(MainActivity.this,"Activity Strumenti di Posizione \n DA COLLEGARE",Toast.LENGTH_SHORT).show();

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
                // TODO Fragment/Activity storico Generale
                Toast.makeText(this,"Activity Storico Generale \n DA COLLEGARE",Toast.LENGTH_SHORT).show();
                break;
            case R.id.FragmentImpostazioni:
                // TODO Fragment/Activity Impostazioni
                Toast.makeText(this,"Activity Impostazioni \n DA COLLEGARE",Toast.LENGTH_SHORT).show();
                break;
            case R.id.FragmentAbout:
                // TODO Fragment/Activity About
                Toast.makeText(this,"Activity About \n DA COLLEGARE",Toast.LENGTH_SHORT).show();
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