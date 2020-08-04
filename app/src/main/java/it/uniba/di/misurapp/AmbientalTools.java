package it.uniba.di.misurapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;


public class AmbientalTools extends AppCompatActivity
{
    Context context;
    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
context=this;
        // set activity layout
        setContentView(R.layout.ambiental_tools);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        try {
            getSupportActionBar().setTitle("");
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        CardView card_compass = (CardView) findViewById(R.id.card_compass); // creating a CardView and assigning a value.

        card_compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                  /*  // definisco l'intenzione
                    Intent Altitude = new Intent(Tools.this,Altitude.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Altitude);
                                   */}

        });
        CardView card_magnetometor = (CardView) findViewById(R.id.card_magnetometor); // creating a CardView and assigning a value.

        card_magnetometor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                 Intent MagneticField = new Intent(context , MagneticField.class);
                startActivity(MagneticField);}
        });


        CardView card_photometer = (CardView) findViewById(R.id.card_photometer); // creating a CardView and assigning a value.

        card_photometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                  /*  // definisco l'intenzione
                    Intent Gps = new Intent(Tools.this,Gps.class);
                    // passo all'attivazione dell'activity Pagina.java
                    startActivity(Gps);
                                   */}
        });

        CardView card_phonometer = (CardView) findViewById(R.id.card_phonometer); // creating a CardView and assigning a value.

        card_phonometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent SoundIntensity = new Intent(context , SoundIntensityDef.class);
                startActivity(SoundIntensity);
            }
        });

        CardView card_barometer = (CardView) findViewById(R.id.card_barometer); // creating a CardView and assigning a value.

        card_barometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent PressureTool = new Intent(context , PressureTool.class);
                startActivity(PressureTool);}
        });

        CardView card_thermometer = (CardView) findViewById(R.id.card_thermometer); // creating a CardView and assigning a value.

        card_photometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                    /*Intent thermometer = new Intent(context,Gps.class);
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

