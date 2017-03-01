package com.example.nouno.easydep;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class Signup3_Activity extends AppCompatActivity {
    private TextInputLayout emailWrapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar3);
        emailWrapper= (TextInputLayout)findViewById(R.id.emailWrapper1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View upimage = findViewById(R.id.upImage3);
        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailWrapper.getEditText().getText().toString();
                Intent i = new Intent(getApplicationContext(),Signup4_Activity.class);
                i.putExtra("email",email);
                startActivity(i);

            }
        });
    }
}
