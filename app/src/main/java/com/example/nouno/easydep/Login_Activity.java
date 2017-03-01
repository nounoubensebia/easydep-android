package com.example.nouno.easydep;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
        //emailWrapper= (TextInputLayout) findViewById(R.id.emailWrapper3);
        //passwordWrapper=(TextInputLayout) findViewById(R.id.passwordWrapper);
        root = (LinearLayout)findViewById(R.id.root);
        Button btn = (Button)findViewById(R.id.signinButton);
        Button inscriver = (Button)findViewById(R.id.signUp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                passwordWrapper= (TextInputLayout)root.findViewById(R.id.passwordWrapper);
                emailWrapper= (TextInputLayout)root.findViewById(R.id.emailWrapper3);
                String password = passwordWrapper.getEditText().getText().toString();
                String email = emailWrapper.getEditText().getText().toString();
                LinkedHashMap<String,String> map=new LinkedHashMap<String, String>();
                map.put("email",email);
                map.put("password",password);
                //String parametres=QueryUtils.buildPostParametersString(map);
                //Toast.makeText(v.getContext(),parametres,Toast.LENGTH_LONG).show();
                LoginTask task = new LoginTask();
                task.execute(map);
                //Log.i("tag",emailText);

            }
        });
        inscriver.setOnClickListener(new View.OnClickListener() {
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
            String response =  QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_LOGIN_URL,params[0]);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
             Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
    }
}
