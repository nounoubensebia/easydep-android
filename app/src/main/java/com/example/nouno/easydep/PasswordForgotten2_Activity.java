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

import com.example.nouno.easydep.exceptions.ConnectionProblemException;

import java.util.LinkedHashMap;
import java.util.Map;

public class PasswordForgotten2_Activity extends AppCompatActivity {
    TextInputLayout codeWrapper;
    ProgressBar progressBar;
    View upimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forgotten2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        codeWrapper = (TextInputLayout) findViewById(R.id.change_pass_code_wrapper);
        upimage = findViewById(R.id.go_button);
        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {}
                String code = codeWrapper.getEditText().getText().toString();
                if (code.length() != 10) {
                    codeWrapper.getEditText().setError("Le code doit contenir exactement 10 caractères");
                } else {
                    Bundle extras = getIntent().getExtras(); // recuperation des info envoyees par l'activite precedente
                    String email = extras.getString("email");
                    LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                    map.put("email",email);
                    map.put("code",code);
                    VerifyCodeTask verifyCodeTask = new VerifyCodeTask();
                    verifyCodeTask.execute(map);

                }
            }
        });

    }

    private class VerifyCodeTask extends AsyncTask<Map<String, String>, Void, String>

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
                response = QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_PASSWORD_FORGOTTEN_URL, params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
                return "Connection problem";
            }
            return response;

        }

        @Override
        protected void onPostExecute(String s) {
            upimage.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if (s.equals("code correct")) {
                Bundle extras = getIntent().getExtras(); // recuperation des info envoyees par l'activite precedente
                String email = extras.getString("email");
                Intent i = new Intent(getApplicationContext(),PasswordForgotten3_Actitivity.class);
                i.putExtra("code",codeWrapper.getEditText().getText().toString());
                i.putExtra("email",email);
                startActivity(i);

            } else {
                if (s.equals("code incorrect")) {
                    Snackbar snackbar = Snackbar.make(upimage, "Erreur le code saisi est incorrect", Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(getResources().getColor(R.color.backgroundColor));
                    tv.setMaxLines(10);
                    snackbar.show();
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(upimage,"Erreur de connexion",Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(getResources().getColor(R.color.backgroundColor));
                    tv.setMaxLines(10);
                    snackbar.show();
                }
            }
        }
    }
}
