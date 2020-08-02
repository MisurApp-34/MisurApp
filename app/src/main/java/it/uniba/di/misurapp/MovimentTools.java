package it.uniba.di.misurapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class MovimentTools extends AppCompatActivity
{

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        // set activity layout
        setContentView(R.layout.moviment_tools);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        try {
            getSupportActionBar().setTitle("");
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        CardView card_gravity = (CardView) findViewById(R.id.card_gravity); // creating a CardView and assigning a value.

        card_gravity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                  /*  // definisco l'intenzione
                    Intent Altitude = new Intent(Tools.this,Altitude.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Altitude);
                                   */}

        });
        CardView card_speed = (CardView) findViewById(R.id.card_speed); // creating a CardView and assigning a value.

        card_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                  /*  // definisco l'intenzione
                    Intent Level = new Intent(Tools.this,Level.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Level);
                                   */}
        });


        CardView card_acceleration = (CardView) findViewById(R.id.card_acceleration); // creating a CardView and assigning a value.

        card_acceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                  /*  // definisco l'intenzione
                    Intent Gps = new Intent(Tools.this,Gps.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Gps);
                                   */}
        });




    }






    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

