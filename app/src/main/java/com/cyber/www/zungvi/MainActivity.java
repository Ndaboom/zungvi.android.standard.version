package com.cyber.www.zungvi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{

    Handler handler;
    ImageView vlogo;
    String word = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vlogo = findViewById(R.id.icon);

        vlogo.animate().alpha(3000).setDuration(0);

        word = DataStore.getInstance(this).getAccountStatus().trim().toString();

        handler = new Handler();
        if(word.equals("desactivated")){

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent secondForm = new Intent(MainActivity.this,AccountActivationActivity.class);
                    startActivity(secondForm);
                    finish();
                }
            },4000);

        }else{

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent secondForm = new Intent(MainActivity.this,WelcomeActivity.class);
                    startActivity(secondForm);
                    finish();
                }
            },4000);

        }



    }
}
