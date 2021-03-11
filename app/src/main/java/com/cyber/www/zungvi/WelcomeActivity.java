package com.cyber.www.zungvi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView title1, subtitle1, privacy_text,about_text;
    private Button agreeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if(DataStore.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,Main2Activity.class));
            return;
        }

        title1 = findViewById(R.id.title1);
        subtitle1 = findViewById(R.id.subtitle1);
        privacy_text = findViewById(R.id.privacy_text);
        agreeButton = findViewById(R.id.btn_agree);
        about_text = findViewById(R.id.about_text);

        privacy_text.setOnClickListener(this);
        agreeButton.setOnClickListener(this);

        if(Locale.getDefault().getLanguage() == "fr")
        {
            title1.setText("Bienvenue sur Zungvi!");
            subtitle1.setText("Nous serons ravi de vous avoir parmi nous, joignez nous!");
            privacy_text.setText("Lisez notre politique de confidentialité. Appuyez sur consentir et continuez pour accepter les conditions de service");
            agreeButton.setText("CONSENTIR ET CONTINUER");
            about_text.setText("Vous garde connecter a vos proches et amis");

        }
    }

    @Override
    public void onClick(View view) {

        if(view == privacy_text){
            startActivity(new Intent(getApplicationContext(), termsActivity.class));
        }

        if(view == agreeButton){
            startActivity(new Intent(getApplicationContext(),registrationActivity.class));
        }
    }
}