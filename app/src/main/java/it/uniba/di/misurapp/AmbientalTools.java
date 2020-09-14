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

        //riferimento card view
        CardView card_compass = (CardView) findViewById(R.id.card_compass); // creating a CardView and assigning a value.

        card_compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent CompassTool = new Intent(context , CompassTool.class);
                startActivity(CompassTool);}

        });

        //riferimento card magnetometero
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
                Intent photometer_tool = new Intent(context , LightTool.class);
                startActivity(photometer_tool);
            }
        });

        CardView card_phonometer = (CardView) findViewById(R.id.card_phonometer); // creating a CardView and assigning a value.

        card_phonometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

              Intent SoundIntensity = new Intent(context , SoundIntensity.class);
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

        card_thermometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent thermometer_temperature_tool = new Intent(context , TemperatureTool.class);
                startActivity(thermometer_temperature_tool);
            }
        });

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }





}

