package com.example.nouno.easydep.Activities;

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

import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;

import java.util.LinkedHashMap;
import java.util.Map;

public class PasswordForgotten3_Actitivity extends AppCompatActivity {
    TextInputLayout newPasswordWrapper;
    TextInputLayout newPasswordConfirmWrapper;
    private View upimage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forgotten3);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        upimage = findViewById(R.id.go_button);
        upimage.setVisibility(View.VISIBLE);
        newPasswordConfirmWrapper = (TextInputLayout)findViewById(R.id.change_pass_new_password_confirm_wrapper);
        newPasswordWrapper = (TextInputLayout)findViewById(R.id.change_pass_new_password_wrapper);
        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {}
                String newPass = newPasswordWrapper.getEditText().getText().toString();
                String newPassConfirm = newPasswordConfirmWrapper.getEditText().getText().toString();
                if (!QueryUtils.validatePassword(newPass))
                {
                    newPasswordWrapper.getEditText().setError(getString(R.string.mot_de_passe_6_caracteres));
                }
                if (!newPass.equals(newPassConfirm))
                {
                    newPasswordConfirmWrapper.getEditText().setError(getString(R.string.mot_de_passe_pas_identique));
                }
                if (newPass.equals(newPassConfirm)&&QueryUtils.validatePassword(newPass))
                {
                    LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                    Bundle extras = getIntent().getExtras();
                    String email = extras.getString("email");
                    String code = extras.getString("code");
                    map.put("email",email);
                    map.put("code",code);
                    map.put("new_password",newPass);
                    SetNewPasswordTask setNewPasswordTask = new SetNewPasswordTask();
                    setNewPasswordTask.execute(map);
                }

            }
        });
    }
    private class SetNewPasswordTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
           progressBar.setVisibility(View.VISIBLE);
            upimage.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response=null;
            try {
                response=QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_PASSWORD_FORGOTTEN_URL,params[0],getApplicationContext());
            } catch (ConnectionProblemException e) {
                return QueryUtils.CONNECTION_PROBLEM;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            upimage.setVisibility(View.VISIBLE);
           if (s.equals(QueryUtils.CONNECTION_PROBLEM))
           {
               Snackbar snackbar = Snackbar.make(upimage,R.string.erreur_Connexion_Snackbar,Snackbar.LENGTH_LONG);
               View view = snackbar.getView();
               view.setBackgroundColor(getResources().getColor(R.color.white));
               TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
               tv.setTextColor(getResources().getColor(R.color.backgroundColor));
               tv.setMaxLines(10);
               snackbar.show();
           }
            else
           {
               if (s.equals("password changed"))
               {
                   Snackbar snackbar = Snackbar.make(upimage, R.string.mot_de_passe_change_avec_succes,Snackbar.LENGTH_INDEFINITE);

                   View view = snackbar.getView();
                   view.setBackgroundColor(getResources().getColor(R.color.white));
                   TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                   tv.setTextColor(getResources().getColor(R.color.backgroundColor));
                   tv.setMaxLines(10);
                   snackbar.setAction("Connexion", new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent i = new Intent(v.getContext(),Login_Activity.class);
                           Bundle extras = getIntent().getExtras();
                           String email = extras.getString("email");
                           i.putExtra("email",email);
                           startActivity(i);
                       }
                   });
                   snackbar.setActionTextColor(getResources().getColor(R.color.backgroundColor));
                   snackbar.show();
               }
           }

        }
    }
}
