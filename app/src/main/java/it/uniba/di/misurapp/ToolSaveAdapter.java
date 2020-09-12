package it.uniba.di.misurapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import static it.uniba.di.misurapp.ToolSave.db;
import static it.uniba.di.misurapp.ToolSave.getAll;

public class ToolSaveAdapter extends ArrayAdapter<String> {

    Context context;
    String[] rTitle;
    String[] rDate;
    String[] rValue;
    String[] rToolName;
    int[] rImgs;
    ImageView rTrash;
    ImageView rUpload;

    // Costruttore
    ToolSaveAdapter(Context c, String[] title, String[] date, String[] value, String[] toolname ,int[] imgs, ImageView trash, ImageView upload) {
        super(c, R.layout.row, R.id.textViewSave, title);
        this.context = c;
        this.rTitle = title;
        this.rDate = date;
        this.rValue = value;
        this.rToolName = toolname;
        this.rImgs = imgs;
        this.rTrash = trash;
        this.rUpload = upload;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.row, parent, false);

        // Setup viste
        ImageView images = row.findViewById(R.id.image);
        TextView myTitle = row.findViewById(R.id.textViewSave);
        TextView myDate = row.findViewById(R.id.textViewDate);
        TextView myValue = row.findViewById(R.id.textViewValue);
        TextView myToolName = row.findViewById(R.id.ToolName);
        ImageView trash = row.findViewById(R.id.trash);
        ImageView upload = row.findViewById(R.id.upload);

        // Collegamento elementi della vista con elementi di ToolSave
        images.setImageResource(rImgs[position]);
        myTitle.setText(rTitle[position]);
        myDate.setText(rDate[position]);
        myValue.setText(rValue[position]);
        myToolName.setText(rToolName[position]);

        // Listener per click cestino
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id elemento visualizzato sulla lista  da passare a metodo di eliminazione
                int id = ToolSave.mId[position];
                DatabaseManager myDbOBJ = new DatabaseManager(getContext());
                myDbOBJ.deleteItem(id);
                if(myDbOBJ.deleteItem(id)) {
                    // Riferimento a classe per aggiornare la lista di elementi presenti sullo storico misurazioni
                    ToolSave.removeElement();
                    Toast.makeText(context,context.getString(R.string.eliminato),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,context.getString(R.string.errore),Toast.LENGTH_SHORT).show();
                }
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(context);

                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setMessage(context.getResources().getString(R.string.name_saving));
                alertbox.setTitle(context.getResources().getString(R.string.insert_name));
                alertbox.setView(input);
                alertbox.setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {
                        int id = ToolSave.mId[position];

                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                String nome = input.getText().toString();
                                if(db.updateName(nome,id)){
                                    getAll();
                                    Toast.makeText(context, context.getString(R.string.modificato),Toast.LENGTH_SHORT).show();
                                }else Toast.makeText(context,context.getString(R.string.errore),Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        });
              return row;
    }

    private void toastMessage(String you_must_enter_a_name) {
    }
}