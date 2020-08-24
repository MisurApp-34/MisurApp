package it.uniba.di.misurapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ToolSaveAdapter extends ArrayAdapter<String> {

    Context context;
    String[] rTitle;
    String[] rDate;
    String[] rValue;
    String[] rToolName;
    int[] rImgs;
    ImageView rTrash;

    // Costruttore
    ToolSaveAdapter(Context c, String[] title, String[] date, String[] value, String[] toolname ,int[] imgs, ImageView trash) {
        super(c, R.layout.row, R.id.textViewSave, title);
        this.context = c;
        this.rTitle = title;
        this.rDate = date;
        this.rValue = value;
        this.rToolName = toolname;
        this.rImgs = imgs;
        this.rTrash = trash;

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
                Toast.makeText(context,"trash test " + position,Toast.LENGTH_SHORT).show();
                // TODO Eliminazione riga
            }
        });
        return row;
    }
}
