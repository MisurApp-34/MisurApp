package it.uniba.di.misurapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class Favorite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle(R.string.favorite);
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView AddFavIcon = findViewById(R.id.add_favorite_mid_icon);
        AddFavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Activity aggiungi preferiti da implementare
                Toast.makeText(Favorite.this,"Activity Aggiungi preferiti \n DA IMPLEMENTARE",Toast.LENGTH_SHORT).show();
            }
        });

        TextView AddFavText = findViewById(R.id.add_favorite_mid_text);
        AddFavText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Activity aggiungi preferiti da implementare
                Toast.makeText(Favorite.this,"Activity Aggiungi preferiti \n DA IMPLEMENTARE",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addfavorite){
            // TODO Activity aggiungi preferiti da implementare
            Toast.makeText(Favorite.this,"Activity Aggiungi preferiti \n DA IMPLEMENTARE",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.addfavorite, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

