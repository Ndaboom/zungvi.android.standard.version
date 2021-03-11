package com.cyber.www.zungvi;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class registrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUsername, editTextUsername2, editTextEmail, editTextPassword, editTextPasswordConfirm;
    private Button buttonRegister;
    private ProgressDialog progressDialog;
    private TextView textViewLogin,textViewTerms;

    AwesomeValidation awesomeValidation;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if(DataStore.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,Main2Activity.class));
            return;
        }


        editTextUsername = findViewById(R.id.editTextUsername);
        editTextUsername2 = findViewById(R.id.editTextUsername2);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        textViewLogin = findViewById(R.id.textViewLogin);
        textViewTerms = findViewById(R.id.textViewTerms);

        buttonRegister = findViewById(R.id.buttonRegister);

        textViewLogin.setOnClickListener(this);
        textViewTerms.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        buttonRegister.setOnClickListener(this);

        //AwesomeValidation Initialization
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this,R.id.editTextUsername, RegexTemplate.NOT_EMPTY,R.string.invalid_username);
        awesomeValidation.addValidation(this,R.id.editTextUsername2, RegexTemplate.NOT_EMPTY,R.string.invalid_username);
        awesomeValidation.addValidation(this,R.id.editTextEmail, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
        awesomeValidation.addValidation(this,R.id.editTextPassword,".{6,}", R.string.invalid_password);
        awesomeValidation.addValidation(this,R.id.editTextPasswordConfirm,R.id.editTextPassword,R.string.invalid_confirm_password);


        if(Locale.getDefault().getLanguage() == "fr")
        {
            editTextUsername.setHint("Votre prenom");
            editTextUsername2.setHint("Votre nom de famille");
            editTextEmail.setHint("Votre adresse mail");
            editTextPassword.setHint("Creer un mot de passe");
            editTextPasswordConfirm.setHint("Confirmer le mot de passe");
            textViewTerms.setText("Termes et Conditions");
            textViewLogin.setText("Déjà inscrit ? Cliquez ici pour vous connecter");
            buttonRegister.setText("S'inscrire");
        }

    }

    private void registerUser() {
        final String name = editTextUsername.getText().toString().trim();
        final String nom2 = editTextUsername2.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String pass = editTextPassword.getText().toString().trim();
        final String passConfirm = editTextPasswordConfirm.getText().toString().trim();
        if(name != "" && nom2 != "" && email != "" && pass != "" && passConfirm != ""){
        progressDialog.setMessage("Registering...please wait");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            if(!jsonObject.getBoolean("error")){
                                DataStore.getInstance(getApplicationContext()).userLogin(
                                        jsonObject.getInt("id"),
                                        jsonObject.getString("name"),
                                        jsonObject.getString("email")
                                );
                                // Enregistrement du mail de l'user en local
                                DataStore.getInstance(getApplicationContext()).setEmail(email);
                                DataStore.getInstance(getApplicationContext()).account_state("desactivated"
                                );
                                startActivity(new Intent(getApplicationContext(), AccountActivationActivity.class));
                                finish();
                            }else{
                                Toast.makeText(
                                        getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), "Something went wrong...please try again", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("nom2", nom2);
                params.put("email", email);
                params.put("pass", pass);
                params.put("passConfirm", passConfirm);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }else
        {
            Toast.makeText(getApplicationContext(), "Please...fill in all the fields", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view){

        if(view == buttonRegister){

            if(awesomeValidation.validate()) {
                registerUser();
            }
        }

        if(view == textViewLogin){
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == textViewTerms){
            startActivity(new Intent(this,termsActivity.class));
        }

    }

}
