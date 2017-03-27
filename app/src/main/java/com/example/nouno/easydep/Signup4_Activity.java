package com.example.nouno.easydep;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.LinkedHashMap;
import java.util.Map;

public class Signup4_Activity extends AppCompatActivity {
    private TextInputLayout passwordWrapper;
    private View upimage;
    private ProgressBar progressBar;
    private FirebaseInstanceId firebaseInstanceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);
        firebaseInstanceId = FirebaseInstanceId.getInstance();
        Log.i("tag",firebaseInstanceId.getToken());
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
                String firstname = extras.getString("firstname");
                String lastname = extras.getString("lastname");
                String password = passwordWrapper.getEditText().getText().toString();
                String token = firebaseInstanceId.getToken();
                if (QueryUtils.validatePassword(password)) {
                    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                    map.put("email", email);
                    map.put("password", password);
                    map.put("firstname",firstname);
                    map.put("lastname",lastname);
                    map.put("token",token);
                    SignupTask task = new SignupTask();
                    task.execute(map);//envoi requete d'inscription
                }
                else
                {
                    passwordWrapper.getEditText().setError(getString(R.string.mot_de_passe_6_caracteres));
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
                e.printStackTrace();response =QueryUtils.CONNECTION_PROBLEM;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals(QueryUtils.CONNECTION_PROBLEM))
            {
                Snackbar snackbar = Snackbar.make(upimage,R.string.erreur_Connexion_Snackbar,Snackbar.LENGTH_LONG);
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
