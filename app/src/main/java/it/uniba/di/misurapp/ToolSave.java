package it.uniba.di.misurapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.view.*;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class ToolSave extends AppCompatActivity {

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_list);

        ListView listView = findViewById(R.id.my_list);
        String mTitle[]={"Salvataggio 1","Salvataggio 2","Salvataggio 3","Salvataggio 4","Slvataggio 5","Salvataggio 6","Salvataggio 7","Salvataggio 8","Salvataggio 9"};
        String mDate[]= {"Data 1","Data 2","Data 3","Data 4","Data 5","Data 6","Data 7","Data 8","Data 9"};
        String mvalue[]= {"valore 1","valore 2","valore 3","valore 4","valore 5","valore 6","valore 7","valore 8","valore 9"};
        int images [] = {R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background,R.drawable.ic_launcher_background};
        ImageView trash = (ImageView) findViewById(R.id.trash);

        //set our images and other things are set in array


        List<String> mylist = new ArrayList<>();
        mylist.add("Ciao");
        mylist.add("Addio");
        mylist.add("Prova");
        mylist.add("Buongiorno");
        mylist.add("Buonanotte");
        mylist.add("Casa");
        mylist.add("Cane");
        mylist.add("Gatto");
        mylist.add("Leone");


        ToolSaveAdapter adapter = new ToolSaveAdapter(this, mTitle, mDate, mvalue, images);

        listView.setAdapter(adapter);

      /*  trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StrumentSave.this,"Activity Strumenti ambientali \n DA COLLEGARE",Toast.LENGTH_SHORT).show();            }
        });

       */


       /*  QUESTA E' LA VECCHIA LISTA!!

       arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mylist);
        listView.setAdapter(arrayAdapter);

        */
    }




    //QUESTA BARRA DI RICERCA NON FUNZIONA E FA RIFERIMENTO ALLA VECCHIA LISTA
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Cerca");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}