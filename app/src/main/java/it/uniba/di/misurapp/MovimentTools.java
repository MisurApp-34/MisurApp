package it.uniba.di.misurapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import java.util.Objects;


public class MovimentTools extends AppCompatActivity {
    Context context;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context=this;

        setContentView(R.layout.moviment_tools);

        //importa toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            getSupportActionBar().setTitle("");
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        //riferimento cardview
        CardView card_gravity = (CardView) findViewById(R.id.card_gravity);


        //listner click cardview
        card_gravity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gravity_tool = new Intent(context , GravityTool.class);
                startActivity(gravity_tool);
            }
        });
        CardView card_speed = (CardView) findViewById(R.id.card_speed);

        card_speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Speed = new Intent(MovimentTools.this, Speed.class);
                startActivity(Speed);
            }
        });


        CardView card_acceleration = (CardView) findViewById(R.id.card_acceleration);

        card_acceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accelerometer_tool = new Intent(context , AccelerometerTool.class);
                startActivity(accelerometer_tool);
            }
        });
    }


    //pulsante indietro
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

