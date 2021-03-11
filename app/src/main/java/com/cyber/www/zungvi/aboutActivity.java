package com.cyber.www.zungvi;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class aboutActivity extends AppCompatActivity implements View.OnClickListener  {

    private ImageView imageView;
    private TextView textView, textView_about,textView_Terms,about_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        imageView = findViewById(R.id.zlogo);
        textView = findViewById(R.id.text_view);
        textView_about = findViewById(R.id.about_title);
        textView_Terms = findViewById(R.id.textViewTerms);
        about_title = findViewById(R.id.about_title);

        textView_Terms.setOnClickListener(this);

        textView_about.setTypeface(null, Typeface.BOLD);

        //Auto translation
        if(Locale.getDefault().getLanguage() == "fr")
        {

            textView.setText("Zungvi est un réseau social 100% congolais. Accessible sur le web et sur Android via son application, il donne à chaque utilisateur disposant d'un compte unique la possibilité de se rencontrer dans des forums et visiter des lieux virtuels afin de partager des connaissances et des idées dans les domaines de leur choix... Chaque utilisateur membre du forum a la possibilité de poser une question, ou de soumettre un sujet aux membres du forum, qui peuvent alors donner leur avis sur le sujet posté par cet utilisateur tout en respectant leur vie privée et leur sécurité.\n"+"Les utilisateurs peuvent créer des forums personnalisés et des lieux virtuels où ils peuvent partager et discuter sur différents sujets de leur choix. La messagerie directe permet aux gens d'avoir des discussions privées en tête à tête, de manière privée et sécurisée. Les utilisateurs peuvent partager des fichiers texte, des images, des citations, des liens, etc. sur le réseau. La vie privée et la sécurité de l'utilisateur sont assurées. Nominé comme meilleure solution de la categorie Apprentissage et Education  par l'expert de la WSA RDC");
            about_title.setText("A propos");
            textView_Terms.setText("Termes et conditions");
        }


    }

    @Override
    public void onClick(View v) {

        if(v == textView_Terms){
            startActivity(new Intent(this,termsActivity.class));
        }
    }
}