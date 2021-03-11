package com.cyber.www.zungvi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextIdentifiant, editTextPassword;
    private TextView textViewFPassword,header_message;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient;

    private static int RC_SIGN_IN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(DataStore.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this,Main2Activity.class));
            return;
        }

        // Configure sign-in to request the user's ID, email address, and basic
       // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              signIn();
            }
        });


        editTextPassword = findViewById(R.id.editTextPassword);
        editTextIdentifiant = findViewById(R.id.editTextIdentifiant);
        textViewFPassword = findViewById(R.id.textViewFPassword);
        header_message = findViewById(R.id.header_message);
        buttonLogin = findViewById(R.id.buttonLogin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        buttonLogin.setOnClickListener(this);
        textViewFPassword.setOnClickListener(this);

        if(Locale.getDefault().getLanguage() == "fr")
        {
            header_message.setText("Entrez votre email ou votre nom de famille pour vous connecter");
            editTextIdentifiant.setHint("Email ou nom de famille");
            editTextPassword.setHint("Votre mot de passe");
            buttonLogin.setText("SE CONNECTER");
            textViewFPassword.setText("Mot de passe oublié?");
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        Log.d("Handler signInResult","signIn result is being executed");
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("Handler signInResult","in the try block");
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {

                Log.d("Handler signInResult","Data fetched");
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Toast.makeText(this,"User email:"+personEmail+ "First name:"+personName+" Lastname:"+personFamilyName, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), termsActivity.class));
            }else{
                Log.d("Handler signInResult","Something went wrong");
                Toast.makeText(
                        getApplicationContext(),
                        "Authentification failed",
                        Toast.LENGTH_LONG
                ).show();
            }
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
           Log.d("Message", e.toString());
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void userLogin() {
        final String identifiant = editTextIdentifiant.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        if(identifiant != "" && password != ""){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                DataStore.getInstance(getApplicationContext()).userLogin(
                                        obj.getInt("id"),
                                        obj.getString("username"),
                                        obj.getString("email")
                                );
                                startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                                finish();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        obj.getString("message"),
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
                        progressDialog.dismiss();
                        Toast.makeText(
                                getApplicationContext(),
                                /**error.getMessage()**/"Something went wrong...please try again",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("identifiant", identifiant);
                params.put("password", password);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }else{
            if(Locale.getDefault().getLanguage() == "fr") {
                Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            }
    }

    }



    @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            userLogin();
        }

        if(v == textViewFPassword){
            startActivity(new Intent(getApplicationContext(), Password_ForgottenActivity.class));
            finish();
        }

    }

    public void onBackPressed() {
        startActivity(new Intent(this, registrationActivity.class));
    }

}
