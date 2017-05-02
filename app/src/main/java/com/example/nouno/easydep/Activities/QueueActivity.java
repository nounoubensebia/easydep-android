package com.example.nouno.easydep.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.easydep.CancelRequest;
import com.example.nouno.easydep.Data.AssistanceRequestListItem;
import com.example.nouno.easydep.Data.UserComment;
import com.example.nouno.easydep.DialogUtils;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.Utils;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.gson.Gson;
import com.yayandroid.theactivitymanager.TAMBaseActivity;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueueActivity extends TAMBaseActivity {

    private int position=4;
    private View position4;
    private View position3;
    private View position2;
    private View position1;
    private View positoin0;
    private View requestCanceled;
    private View currentPosition;
    private AssistanceRequestListItem assistanceRequest;
    private View root;
    private Button button;
    private Button evaluateButton;
    private TextView positionText;
    private TextView waitText;
    private ProgressDialog progressDialog;

    private QueueActivity queueActivity;
    private BroadcastReceiver newPositionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePosition();
        }
    };
    private BroadcastReceiver requestCanceledReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cancelRequest();
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(newPositionBroadcastReceiver,new IntentFilter("new_position"));
        LocalBroadcastManager.getInstance(this).registerReceiver(requestCanceledReceiver,new IntentFilter("intervention_canceled"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        queueActivity=this;
        root = findViewById(R.id.root);
        root.setVisibility(View.INVISIBLE);
        position4 = findViewById(R.id.position_4);
        position3 = findViewById(R.id.position_3);
        position2 = findViewById(R.id.position_2);
        position1 = findViewById(R.id.position_1);
        positoin0 = findViewById(R.id.position_0);
        requestCanceled = findViewById(R.id.request_canceled);
        evaluateButton=(Button)findViewById(R.id.evaluate_repair_service);
        button = (Button)findViewById(R.id.advance);
        positionText = (TextView) findViewById(R.id.position_text);
        waitText = (TextView)findViewById(R.id.wait_text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position!=-2)
                {
                    CancelRequest cancelRequest = new CancelRequest(queueActivity);
                    cancelRequest.cancelRequest(assistanceRequest.getId());
                }
                //updatePosition();
            }
        });
        evaluateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateRepairService();
            }
        });
        getIntentData();

        if (position == 0)
        {

            loadRepairServiceDistanceData();
        }

        setInitialPosition();
    }

    private void evaluateRepairService ()
    {
        UserComment userComment = new UserComment(Utils.getRegistredCarOwner(getApplicationContext()),assistanceRequest.getRepairService(),0);
        Intent i = new Intent(getApplicationContext(),AddCommentActivity.class);
        i.putExtra("userComment",userComment.toJson());
        startActivity(i);
        finish();
    }



    private void getIntentData()
    {
        Bundle extras = getIntent().getExtras();
        position = extras.getInt("position");
        Gson gson = new Gson();
        assistanceRequest = gson.fromJson(extras.getString("assistanceRequest"),AssistanceRequestListItem.class);
        //position++;
    }

    private void setInitialPosition ()
    {

        switch (position)
        {
            case -2 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.VISIBLE);
                waitText.setText("Intervention terminée");
                positionText.setVisibility(View.GONE);
                currentPosition = positoin0;
                root.setVisibility(View.VISIBLE);
                evaluateButton.setVisibility(View.VISIBLE);
                button.setText("Ne plus afficher demande");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DontShowRequestTask dontShowRequestTask = new DontShowRequestTask();
                        LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                        map.put("action","change_status");
                        map.put("assistance_request_id",assistanceRequest.getId()+"");
                        map.put("status",5+"");
                        dontShowRequestTask.execute(map);
                    }
                });
                break;
            case 4 : position4.setVisibility(View.VISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                currentPosition = position4;
                root.setVisibility(View.VISIBLE);
                break;
            case 3 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.VISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                currentPosition = position3;
                root.setVisibility(View.VISIBLE);
                break;
            case 2 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.VISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                currentPosition = position2;
                root.setVisibility(View.VISIBLE);
                break;
            case 1 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.VISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                currentPosition = position1;
                root.setVisibility(View.VISIBLE);
                break;
            case 0 :position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.VISIBLE);
                currentPosition = positoin0;
                root.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void loadRepairServiceDistanceData ()
    {

        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action",QueryUtils.GET_REPAIR_SERVICE_ETA);
        map.put("assistance_request_id",assistanceRequest.getId()+"");
        LoadDurationTask loadDurationTask = new LoadDurationTask();
        loadDurationTask.execute(map);
    }

    private void updatePosition ()
    {
        position--;
        switch (position){

            case 3:updatePositionAnimation(position4,position3);
                currentPosition = position3;
                break;
            case 2:updatePositionAnimation(position3,position2);
                currentPosition = position2;
                break;
            case 1:updatePositionAnimation(position2,position1);
                currentPosition = position1;
                break;
            case 0 : //fadeOut(positionText);//updatePositionAnimation(position1,positoin0);
                //replaceText("Votre dépanneur arrive",waitText);
                loadRepairServiceDistanceData();
                break;
            case -1 : fadeOut(positionText);replaceText("Intervention terminée",waitText);
                replaceText("Ne plus afficher demande",button);
                fadeIn(evaluateButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DontShowRequestTask dontShowRequestTask = new DontShowRequestTask();
                        LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
                        map.put("action","change_status");
                        map.put("assistance_request_id",assistanceRequest.getId()+"");
                        map.put("status",5+"");
                        dontShowRequestTask.execute(map);
                    }
                });
                break;
        }
    }

    private void cancelRequest ()
    {
        updatePositionAnimation(currentPosition,requestCanceled);
        replaceText("Intervention annulée par le dépanneur",positionText);
        fadeOut(waitText);
        fadeOut(button);
        //replaceText("Supprimer demande",button);
    }


    public void fadeOut (final View view)
    {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(),R.anim.fade_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }
    public void fadeIn (final View view)
    {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(),R.anim.fade_in);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public void updatePositionAnimation (final View exitView, final View enterView)
    {
        final Animation fabExit = AnimationUtils.loadAnimation(exitView.getContext(),R.anim.fade_out);
        final Animation fabEnter = AnimationUtils.loadAnimation(enterView.getContext(),R.anim.fade_in);
        //fabEnter.setStartTime(2000);
        fabExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                enterView.startAnimation(fabEnter);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fabEnter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                enterView.setVisibility(View.VISIBLE);
                exitView.setVisibility(View.INVISIBLE);
                //enterView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                enterView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        exitView.startAnimation(fabExit);
    }

    public void replaceText(final String newText, final TextView textView)
    {
        Animation fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        final Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                textView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setText(newText);
                textView.setVisibility(View.INVISIBLE);
                textView.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        positionText.startAnimation(fadeOut);
    }

    private class LoadDurationTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            fadeOut(root);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            String waitTime = "Temps d'arrivée estimé : "+s;
            positionText.setText(waitTime);
            positionText.setVisibility(View.VISIBLE);
            setInitialPosition();
            waitText.setVisibility(View.VISIBLE);
            waitText.setText("Votre dépanneur devrait bientôt arriver");
            //updatePositionAnimation(position1,positoin0);
            fadeIn(root);
        }
    }

    private class DontShowRequestTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressDialog = (ProgressDialog) DialogUtils.buildProgressDialog("Veuillez patienter...",queueActivity);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success"))
            {
                progressDialog.dismiss();
                Dialog dialog = DialogUtils.buildClickableInfoDialog("Opération terminée", "Cette demande ne sera plus affichée", queueActivity, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(),RequestsListActivity.class);
                        startActivity(i);
                    }
                });
                dialog.show();
                progressDialog.dismiss();
            }
            else
            {

            }
        }
    }

}
