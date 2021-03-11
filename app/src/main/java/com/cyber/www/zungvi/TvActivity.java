package com.cyber.www.zungvi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.cyber.www.zungvi.Adapters.ProgramAdapter;

public class TvActivity extends AppCompatActivity {

    Button watch ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv);

        watch = findViewById(R.id.button);

        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),PlayerActivity.class);
                startActivity(intent);
            }
        });



    }
}