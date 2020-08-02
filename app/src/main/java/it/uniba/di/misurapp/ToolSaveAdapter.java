package it.uniba.di.misurapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ToolSaveAdapter extends ArrayAdapter<String> {

    Context context;
    String rTitle[];
    String rDate[];
    String rValue[];
    int rImgs[];

    ToolSaveAdapter(Context c, String title[], String date[], String value[], int imgs[]) {
        super(c, R.layout.row, R.id.textViewSave, title);
        this.context = c;
        this.rTitle = title;
        this.rDate = date;
        this.rValue = value;
        this.rImgs = imgs;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.row, parent, false);
        ImageView images = row.findViewById(R.id.image);
        TextView myTitle = row.findViewById(R.id.textViewSave);
        TextView myDate = row.findViewById(R.id.textViewDate);
        TextView myValue = row.findViewById(R.id.textViewValue);


        // now set our resources on views
        images.setImageResource(rImgs[position]);
        myTitle.setText(rTitle[position]);
        myDate.setText(rDate[position]);
        myValue.setText(rValue[position]);

        return row;
    }
}
