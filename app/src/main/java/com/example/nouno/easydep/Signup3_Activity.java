package com.example.nouno.easydep;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Signup3_Activity extends AppCompatActivity {
    private TextInputLayout emailWrapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar3);
        emailWrapper= (TextInputLayout)findViewById(R.id.email_signup_wrapper);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View upimage = findViewById(R.id.go_button3);
        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // envoi de l'email a l'activité suivante
                String email = emailWrapper.getEditText().getText().toString();
                Intent i = new Intent(getApplicationContext(),Signup4_Activity.class);
                i.putExtra("email",email);
                startActivity(i);

            }
        });
    }
}
