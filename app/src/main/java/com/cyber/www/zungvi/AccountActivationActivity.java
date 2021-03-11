package com.cyber.www.zungvi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AccountActivationActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView main_title,title1,resend_link;
    private EditText activation_code,prefix;
    private Button btn_next;
    private ProgressDialog progressDialog;
    String user_email = DataStore.getInstance(this).getCurrentUserEmail();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_activation);

        main_title = findViewById(R.id.main_title);
        title1 = findViewById(R.id.title1);
        prefix = findViewById(R.id.prefix);
        activation_code = findViewById(R.id.activation_code);
        resend_link = findViewById(R.id.resend_link);
        btn_next = findViewById(R.id.btn_next);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        prefix.setEnabled(false);
        btn_next.setOnClickListener(this);
        resend_link.setOnClickListener(this);

        title1.setText("Zungvi just sent you an email at "+DataStore.getInstance(this).getCurrentUserEmail()+" containing your activation code.");

        if(Locale.getDefault().getLanguage() == "fr")
        {

            main_title.setText("Activation du compte");
            title1.setText("Zungvi vous a envoyé un email au "+DataStore.getInstance(this).getCurrentUserEmail()+" contenant votre code d'activation.");
            activation_code.setHint("Code d'activation");
            resend_link.setText("Vous n'avez pas reçu le mail ? Essayez à nouveau");

        }

    }

    @Override
    public void onClick(View view) {

        if(view == btn_next){
            if(activation_code.getText().toString().trim() != ""){
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        Constants.URL_ACCOUNT_REACTIVATION,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    if (!obj.getBoolean("error")) {
                                        Toast.makeText(
                                                getApplicationContext(),
                                                obj.getString("message"),
                                                Toast.LENGTH_LONG
                                        ).show();
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
                        params.put("user_email", user_email);
                        return params;
                    }
                };
                RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
            }else{
                if(Locale.getDefault().getLanguage() == "fr") {
                    Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                }
            }


        }

        if(view == resend_link){
            progressDialog.setMessage("Resending new email...");
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    Constants.URL_ACCOUNT_ACTIVATION,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {
                                    DataStore.getInstance(getApplicationContext()).account_state("activated"
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
                    params.put("user_email", user_email);
                    params.put("activation_code", activation_code.getText().toString().trim());
                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
            
        }

    }
}