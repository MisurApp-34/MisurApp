package it.uniba.di.misurapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import java.util.Objects;

import it.uniba.di.misurapp.*;


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
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);



            try {
                getSupportActionBar().setTitle("");
            } catch (NullPointerException e){
                e.printStackTrace();
            }
            CardView card_altitude = (CardView) findViewById(R.id.card_altitude); // creating a CardView and assigning a value.

            card_altitude.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Activity Altimetro
                    Intent Altitude = new Intent(PositionTools.this, Altimeter.class);
                    startActivity(Altitude);
                }

            });
            CardView card_level = (CardView) findViewById(R.id.card_level); // creating a CardView and assigning a value.

            card_level.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Activity Livella
                    Intent Level = new Intent(PositionTools.this,SpiritLevel.class);
                    startActivity(Level);
                }
            });


            CardView card_gps = (CardView) findViewById(R.id.card_gps); // creating a CardView and assigning a value.

            card_gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Activity Coordinate GPS
                    Intent Gps = new Intent(PositionTools.this, MapActivity.class);
                    startActivity(Gps);
                }
            });

            CardView card_proxyimity = (CardView) findViewById(R.id.card_proxyimity); // creating a CardView and assigning a value.

            card_proxyimity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Activity Sensore di Prossimità
                    Intent Proxyimity = new Intent(PositionTools.this,ProximityTool.class);
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

