package com.example.nouno.easydep.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;

import com.example.nouno.easydep.R;

public class Signup1_Activity extends AppCompatActivity {
    RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //configuration de la toolbar (ou se trouve le text inscription)
        setContentView(R.layout.activity_signup1);
        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //fin config
        View upimage = findViewById(R.id.go_button);
        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = radioGroup.getCheckedRadioButtonId();
                if (id == -1)
                {
                    Snackbar.make(v,R.string.choisir_type,Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    Intent i = new Intent(v.getContext(),Signup2_Activity.class);
                    startActivity(i);
                }
            }
        });
    }
}
