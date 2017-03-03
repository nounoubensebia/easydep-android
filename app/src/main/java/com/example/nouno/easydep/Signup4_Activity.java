package com.example.nouno.easydep;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nouno.easydep.exceptions.ConnectionProblemException;

import java.util.LinkedHashMap;
import java.util.Map;

public class Signup4_Activity extends AppCompatActivity {
    private TextInputLayout passwordWrapper;
    private View upimage;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar4);
        passwordWrapper= (TextInputLayout)findViewById(R.id.password_signup_wrapper);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        upimage = findViewById(R.id.go_button);
        upimage.setVisibility(View.VISIBLE);
        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {}
                Bundle extras = getIntent().getExtras(); // recuperation des info envoyees par l'activite precedente
                String email = extras.getString("email");
                String password = passwordWrapper.getEditText().getText().toString();
                if (QueryUtils.validatePassword(password)) {
                    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                    map.put("email", email);
                    map.put("password", password);
                    SignupTask task = new SignupTask();
                    task.execute(map);//envoi requete d'inscription
                }
                else
                {
                    passwordWrapper.getEditText().setError("Le mot de passe doit contenir au moins 6 caractères");
                }
            }
        });
    }
    private class SignupTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            upimage.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_SIGNUP_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();response ="Probleme de connexion";
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("Probleme de connexion"))
            {
                Snackbar snackbar = Snackbar.make(upimage,"Erreur problème de connexion.",Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                progressBar.setVisibility(View.GONE);
                upimage.setVisibility(View.VISIBLE);
                view.setBackgroundColor(getResources().getColor(R.color.white));
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(getResources().getColor(R.color.backgroundColor));
                tv.setMaxLines(10);
                snackbar.show();
            }
            else
            {
                Intent i = new Intent(getApplicationContext(),Login_Activity.class);
                startActivity(i);
            }

        }
    }
}
