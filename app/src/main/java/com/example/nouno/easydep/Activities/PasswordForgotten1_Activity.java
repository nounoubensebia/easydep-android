package com.example.nouno.easydep.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;

public class PasswordForgotten1_Activity extends AppCompatActivity {
    TextInputLayout emailWrapper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forgotten1);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //fin config
        emailWrapper = (TextInputLayout)findViewById(R.id.change_pass_email_wrapper);
        View upimage = findViewById(R.id.go_button);
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
                    emailWrapper.getEditText().setError(getString(R.string.adresse_email_incorrecte));
                }
                else {
                    Intent i = new Intent(getApplicationContext(),PasswordForgotten2_Activity.class);
                    i.putExtra("email",email);
                    startActivity(i);
                }
            }
        });
    }
}
