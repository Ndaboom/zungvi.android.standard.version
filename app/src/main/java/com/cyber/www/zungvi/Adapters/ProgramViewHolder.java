package com.cyber.www.zungvi.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyber.www.zungvi.R;


public class ProgramViewHolder {

    ImageView itemImage;
    TextView programTitle;
    TextView programDescription;

    ProgramViewHolder(View v){
        itemImage = v.findViewById(R.id.imageView);
        programTitle = v.findViewById(R.id.textView1);
        programDescription = v.findViewById(R.id.textView2);
    }
}
