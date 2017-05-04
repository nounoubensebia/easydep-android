package com.example.nouno.easydep.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;

public class Signup1Activity extends AppCompatActivity {
    TextInputLayout firstNameWrapper;
    TextInputLayout lastNameWrapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firstNameWrapper = (TextInputLayout)findViewById(R.id.firstNameWrapper);
        lastNameWrapper = (TextInputLayout)findViewById(R.id.lastNameWrapper);
        View upimage = findViewById(R.id.go_button);
        upimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameWrapper.getEditText().getText().toString();
                String lastName = lastNameWrapper.getEditText().getText().toString();
                if (!QueryUtils.validateName(firstName))
                {

                    firstNameWrapper.getEditText().setError(getString(R.string.erreur_nom));

                }

                if (!QueryUtils.validateName(lastName))
                {
                    lastNameWrapper.getEditText().setError(getString(R.string.erreur_prenom));
                }

                if (QueryUtils.validateName(firstName)&&QueryUtils.validateName(lastName))
                {
                Intent i = new Intent(getApplicationContext(),Signup2Activity.class);
                    i.putExtra("firstname",firstName);
                    i.putExtra("lastname",lastName);
                startActivity(i);
                }
            }
        });
    }
}
