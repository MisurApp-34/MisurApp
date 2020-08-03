package it.uniba.di.misurapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;


public class PositionTools extends AppCompatActivity
    {
        @Override
        public void onCreate(Bundle bundle)
        {
            super.onCreate(bundle);

            // set activity layout
            setContentView(R.layout.position_tools);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);



            try {
                getSupportActionBar().setTitle("");
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            CardView card_altitude = (CardView) findViewById(R.id.card_altitude); // creating a CardView and assigning a value.

            card_altitude.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                  /*  // definisco l'intenzione
                    Intent Altitude = new Intent(Tools.this,Altitude.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Altitude);
                                   */}

            });
            CardView card_level = (CardView) findViewById(R.id.card_level); // creating a CardView and assigning a value.

            card_level.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                  /*  // definisco l'intenzione
                    Intent Level = new Intent(Tools.this,Level.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Level);
                                   */}
            });


            CardView card_gps = (CardView) findViewById(R.id.card_gps); // creating a CardView and assigning a value.

            card_gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                  /*  // definisco l'intenzione
                    Intent Gps = new Intent(Tools.this,Gps.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Gps);
                                   */}
            });

            CardView card_proxyimity = (CardView) findViewById(R.id.card_proxyimity); // creating a CardView and assigning a value.

            card_proxyimity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    Intent Proxyimity = new Intent(PositionTools.this,ProximityTool.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Proxyimity);
                                   }
            });


        }

        @Override
        public boolean onSupportNavigateUp() {
            onBackPressed();
            return true;
        }

    }

