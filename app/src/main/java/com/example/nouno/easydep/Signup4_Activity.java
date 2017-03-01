package com.example.nouno.easydep;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

public class Signup4_Activity extends AppCompatActivity {
    private TextInputLayout passwordWrapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup4);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar4);
        passwordWrapper= (TextInputLayout)findViewById(R.id.passwordWrapper4);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View upimage = findViewById(R.id.upImage4);
        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Login_Activity.class);
                startActivity(i);
                Bundle extras = getIntent().getExtras();
                String email = extras.getString("email");
                String password = passwordWrapper.getEditText().getText().toString();
                LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                map.put("email",email);
                map.put("password",password);
                SignupTask task = new SignupTask();
                task.execute(map);
            }
        });
    }
    private class SignupTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response =  QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_SIGNUP_URL,params[0]);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
        }
    }
}
