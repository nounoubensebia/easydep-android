package com.example.nouno.easydep;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.nouno.easydep.exceptions.ConnectionProblemException;

import java.util.LinkedHashMap;
import java.util.Map;

public class Login_Activity extends AppCompatActivity {
    private TextInputLayout emailWrapper;
    private TextInputLayout passwordWrapper;
    LinearLayout root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        root = (LinearLayout)findViewById(R.id.root);
        Button signinButton = (Button)findViewById(R.id.signin_button);
        Button signupButton = (Button)findViewById(R.id.signup_button);
        // l'utilisateur se connecte
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordWrapper= (TextInputLayout)root.findViewById(R.id.password_signin_wrapper);
                emailWrapper= (TextInputLayout)root.findViewById(R.id.email_signin_wrapper);
                String password = passwordWrapper.getEditText().getText().toString();
                String email = emailWrapper.getEditText().getText().toString();
                LinkedHashMap<String,String> map=new LinkedHashMap<String, String>(); // liste des parametres du post
                map.put("email",email);
                map.put("password",password);
                LoginTask task = new LoginTask();
                task.execute(map);// envoi requete de connexion

            }
        });
        // l'utilisateur crée un compte
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Signup1_Activity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private class LoginTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_LOGIN_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();response = "Probleme de connexion";
            }
            finally {
                return response;
            }

        }

        @Override
        protected void onPostExecute(String s) {
             Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
    }
}
