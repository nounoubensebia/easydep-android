package com.example.nouno.easydep;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.easydep.exceptions.ConnectionProblemException;

import java.util.LinkedHashMap;
import java.util.Map;

public class Signup3_Activity extends AppCompatActivity {
    private TextInputLayout emailWrapper;
    private LayoutInflater mInflater;
    private ProgressBar progressBar;
    private View upimage;
    private String firstName;
    private String lastName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        firstName = extras.getString("firstname");
        lastName = extras.getString("lastname");
        setContentView(R.layout.activity_signup3);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar3);
        emailWrapper= (TextInputLayout)findViewById(R.id.email_signup_wrapper);
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
                String email = emailWrapper.getEditText().getText().toString();
                if (!QueryUtils.validateEmail(email))
                {
                    emailWrapper.getEditText().setError("Veuillez entrer une adresse email valide");
                }
                else
                {


                    LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                    map.put("email",email);
                    v.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    VerifyEmailTask verifyEmailTask = new VerifyEmailTask();
                    verifyEmailTask.execute(map);

                }

            }
        });
    }






    private class VerifyEmailTask extends AsyncTask<Map<String,String>,Void,String>
   {
       @Override
       protected void onPreExecute() {
           progressBar.setVisibility(View.VISIBLE);
           upimage.setVisibility(View.GONE);
       }

       @Override
       protected String doInBackground(Map<String, String>... params) {
           String response;

           try {
               response=QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_SIGNUP_URL,params[0]);
               if (response.equals("false"))
               {
                   return "Email not found";
               }
               else
               {
                   return "Email found";
               }
           } catch (ConnectionProblemException e) {
               e.printStackTrace();
               return QueryUtils.CONNECTION_PROBLEM;
           }

       }

       @Override
       protected void onPostExecute(String s) {
           if (s.equals("Email not found"))
           {
               String email = emailWrapper.getEditText().getText().toString();
               progressBar.setVisibility(View.GONE);
               upimage.setVisibility(View.VISIBLE);
               Intent i = new Intent(getApplicationContext(),Signup4_Activity.class);
               i.putExtra("email",email);
               i.putExtra("firstname",firstName);
               i.putExtra("lastname",lastName);
               startActivity(i);
           }
           else
           {
               if (s.equals("Email found"))
               {

                   Snackbar snackbar = Snackbar.make(upimage, R.string.compte_existe_deja,Snackbar.LENGTH_INDEFINITE);
                   View view = snackbar.getView();
                   view.setBackgroundColor(getResources().getColor(R.color.white));
                   TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                   tv.setTextColor(getResources().getColor(R.color.backgroundColor));
                   tv.setMaxLines(10);
                   snackbar.setAction("Connexion", new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Intent i = new Intent(v.getContext(),Login_Activity.class);
                           String email = emailWrapper.getEditText().getText().toString();
                           i.putExtra("email",email);
                           startActivity(i);
                       }
                   });
                   snackbar.setActionTextColor(getResources().getColor(R.color.backgroundColor));
                   snackbar.show();
               }
               else
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

           }
       }

   }
}
