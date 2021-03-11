package com.cyber.www.zungvi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cyber.www.zungvi.DataStore;
import com.cyber.www.zungvi.R;

public class ProgramAdapter extends ArrayAdapter<String> {
    Context context;
    int[] programImages;
    String[] programName;
    String[] programDescription;
    String channel;

    public ProgramAdapter(Context context, String[] programName,int[] programImages, String[] programDescription) {
        super(context, R.layout.single_item, R.id.textView1, programName);

        this.context = context;
        this.programImages = programImages;
        this.programName = programName;
        this.programDescription = programDescription;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View singleItem = convertView;
        ProgramViewHolder holder = null;

        if(singleItem == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_item,parent,false);
            holder = new ProgramViewHolder(singleItem);
            singleItem.setTag(holder);
        }else{
            holder = (ProgramViewHolder) singleItem.getTag();
        }

        holder.programTitle.setText(programName[position]);
        holder.programDescription.setText(programDescription[position]);
        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"You clicked :"+ programName[position], Toast.LENGTH_LONG).show();
                DataStore.getInstance(context).tv_channel(programName[position]);

            }
        });

        return singleItem;

    }
}
