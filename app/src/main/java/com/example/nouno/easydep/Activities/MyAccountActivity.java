package com.example.nouno.easydep.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nouno.easydep.Data.CarOwner;
import com.example.nouno.easydep.Data.Tokens;
import com.example.nouno.easydep.DialogUtils;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.Utils;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class MyAccountActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private MyAccountActivity myAccountActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myAccountActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        displayData();
    }
    private void displayData()
    {
        TextView email = (TextView)findViewById(R.id.email);
        TextView fullname = (TextView)findViewById(R.id.fullname);
        final CarOwner carOwner = Utils.getRegistredCarOwner(this);
        email.setText(carOwner.getEmail());
        fullname.setText(carOwner.getFullName());
        View passView = findViewById(R.id.password_layout);
        passView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildPasswordDialog().show();
            }
        });
    }

    private class ChangePasswordTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patientier",myAccountActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.ChANGE_PASSWORDçURL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s.equals("failed")) {
                Dialog dialog = DialogUtils.buildInfoDialog("Erreur", "Mot de passe actuel incorrect", myAccountActivity);
                dialog.show();
            } else {
                try {
                    CarOwner carOwner = Utils.getRegistredCarOwner(myAccountActivity);
                    JSONObject jsonObject = new JSONObject(s);
                    String refreshToken = jsonObject.getString("refresh_token");
                    String accessToken = jsonObject.getString("access_token");
                    Tokens tokens = new Tokens(accessToken, refreshToken);
                    carOwner.setTokens(tokens);
                    Utils.saveCarOwner(carOwner,myAccountActivity);
                    Dialog dialog = DialogUtils.buildInfoDialog("Mot de passe changé", "Votre mot de passe a été mis a jour", myAccountActivity);
                    dialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private Dialog buildPasswordDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(myAccountActivity);
        // Get the layout inflater
        LayoutInflater inflater = myAccountActivity.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String oldPassword = ((EditText) view.findViewById(R.id.old_password)).getText().toString();
                        String newPassword = ((EditText) view.findViewById(R.id.new_password)).getText().toString();
                        if (QueryUtils.validatePassword(newPassword)&&QueryUtils.validatePassword(oldPassword))
                            changePassword(oldPassword,newPassword);
                        else
                        {
                            Dialog dialog1=null;
                            if (!QueryUtils.validatePassword(newPassword)&&QueryUtils.validatePassword(oldPassword))
                            {
                                dialog1 = DialogUtils.buildInfoDialog("Erreur","Le nouveau mot de passe doit contenir au moins 5 Caractères ",myAccountActivity);
                            }
                            else
                            {
                                if (QueryUtils.validatePassword(newPassword)&&!QueryUtils.validatePassword(oldPassword))
                                    dialog1 = DialogUtils.buildInfoDialog("Erreur","L'ancien mot de passe doit contenir au moins 5 Caractères ",myAccountActivity);
                                else
                                    dialog1 = DialogUtils.buildInfoDialog("Erreur","Les mot de passes doivent contenir au moins 5 Caractères ",myAccountActivity);
                            }
                            dialog1.show();
                        }
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
    private void changePassword (String oldPassword,String newPassword)
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("old_password",oldPassword);
        map.put("new_password",newPassword);
        map.put("car_owner_id",Utils.getRegistredCarOwner(myAccountActivity).getId()+"");
        map.put("action","change_car_owner_passowrd");
        ChangePasswordTask changePasswordTask = new ChangePasswordTask();
        changePasswordTask.execute(map);
    }
}
