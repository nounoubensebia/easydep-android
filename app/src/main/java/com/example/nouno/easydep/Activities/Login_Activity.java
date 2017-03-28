package com.example.nouno.easydep.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.easydep.Data.CarOwner;
import com.example.nouno.easydep.Data.Person;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.Utils;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public class Login_Activity extends AppCompatActivity {
    private TextInputLayout emailWrapper;
    private TextInputLayout passwordWrapper;
    private ProgressBar progressBar;
    private Button signinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkIfConnected();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        signinButton = (Button) findViewById(R.id.signin_button);
        Button signupButton = (Button) findViewById(R.id.signup_button);
        Button passForgottenButton = (Button) findViewById(R.id.password_forgotten_button);
        passwordWrapper = (TextInputLayout) findViewById(R.id.password_signin_wrapper);
        emailWrapper = (TextInputLayout) findViewById(R.id.email_signin_wrapper);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            if (email != null) {
                emailWrapper.getEditText().setText(email);
            }

        }


        // l'utilisateur se connecte
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                }

                String password = passwordWrapper.getEditText().getText().toString();
                String email = emailWrapper.getEditText().getText().toString();
                String token = FirebaseInstanceId.getInstance().getToken();
                emailWrapper.setErrorEnabled(false);
                passwordWrapper.setErrorEnabled(false);
                if (!QueryUtils.validateEmail(email)) {
                    emailWrapper.setErrorEnabled(true);
                    emailWrapper.setError("L'adresse e-mail n'est pas valide");
                }
                if (!QueryUtils.validatePassword(password)) {
                    passwordWrapper.setErrorEnabled(true);
                    passwordWrapper.setError("Le mot de passe n'est pas valide");
                }
                if (QueryUtils.validateEmail(email) && QueryUtils.validatePassword(password)) {
                    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(); // liste des parametres du post
                    map.put("email", email);
                    map.put("password", password);
                    map.put("token",token);
                    LoginTask task = new LoginTask();
                    task.execute(map);
                }


            }
        });
        // l'utilisateur crée un compte
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Signup1_Activity.class);
                startActivity(i);
            }
        });
        passForgottenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PasswordForgotten1_Activity.class);
                startActivity(i);
            }
        });

    }

    public void checkIfConnected ()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPref.contains("carOwner"))
        {
            Intent i = new Intent(this,SearchActivity.class);
            startActivity(i);
            finish();
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private class LoginTask extends AsyncTask<Map<String, String>, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            signinButton.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_LOGIN_URL, params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
                response = QueryUtils.CONNECTION_PROBLEM;
            } finally {
                return response;
            }

        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equals(QueryUtils.CONNECTION_PROBLEM)) {
                signinButton.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar.make(signinButton, R.string.erreur_Connexion_Snackbar, Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                progressBar.setVisibility(View.GONE);
                signinButton.setVisibility(View.VISIBLE);
                view.setBackgroundColor(getResources().getColor(R.color.white));
                TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(getResources().getColor(R.color.backgroundColor));
                tv.setMaxLines(10);
                snackbar.show();
            } else {
                if (s.equals("failure")) {
                    signinButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(signinButton, R.string.login_failed, Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    progressBar.setVisibility(View.GONE);
                    signinButton.setVisibility(View.VISIBLE);
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(getResources().getColor(R.color.backgroundColor));
                    tv.setMaxLines(10);
                    snackbar.show();
                }
                else
                {
                    loginUser(s);
                }
            }
        }
    }

    private void loginUser(String s)
    {
        CarOwner carOwner = (CarOwner) Person.fromJson(s);
        Gson gson = new Gson();
        String json = gson.toJson(carOwner,CarOwner.class);

        saveUserData(carOwner);
        Utils.resetSettings(getApplicationContext());
        Intent i = new Intent(getApplicationContext(),SearchActivity.class);
        startActivity(i);
    }
    private void saveUserData (CarOwner carOwner)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(carOwner);
        editor.putString("carOwner",json);
        editor.commit();
    }
}
