package com.example.nouno.easydep.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;

import java.util.Calendar;
import java.util.LinkedHashMap;

public class TokenTestActivity extends AppCompatActivity {
    Button tesButton;
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_test);
        tesButton = (Button)findViewById(R.id.test_button);
        txt=(TextView)findViewById(R.id.test);
        tesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestTask testTask = new TestTask();
                testTask.execute();
            }
        });
    }

    private class TestTask extends AsyncTask<Void,Void,String>
    {

        @Override
        protected String doInBackground(Void... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest("http://192.168.1.7/EasyDep/security_tokens_utils.php",new LinkedHashMap<String, String>(),getApplicationContext());
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
                response = QueryUtils.CONNECTION_PROBLEM;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals(QueryUtils.CONNECTION_PROBLEM))
            {
                Toast.makeText(getApplicationContext(),"Accés refusé",Toast.LENGTH_LONG).show();
            }
            txt.setText(s);
        }
    }
}
