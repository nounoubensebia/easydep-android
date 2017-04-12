package com.example.nouno.easydep.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.nouno.easydep.CancelRequest;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.exceptions.ConnectionProblemException;
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
    private Long assistanceRequestId;
    private View root;
    private Button button;
    private TextView positionText;
    private TextView waitText;
    private QueueActivity queueActivity;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePosition();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("new_position"));
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
        button = (Button)findViewById(R.id.advance);
        positionText = (TextView) findViewById(R.id.position_text);
        waitText = (TextView)findViewById(R.id.wait_text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CancelRequest cancelRequest = new CancelRequest(queueActivity)
                CancelRequest cancelRequest = new CancelRequest(queueActivity);
                cancelRequest.cancelRequest(assistanceRequestId);
            }
        });
        getIntentData();
        if (position == 0)
        {
            loadRepairServiceDistanceData();
        }

        setInitialPosition();
    }

    private void getIntentData()
    {
        Bundle extras = getIntent().getExtras();
        position = extras.getInt("position");
        assistanceRequestId = extras.getLong("assistanceRequestId");
        //position++;
    }

    private void setInitialPosition ()
    {

        switch (position)
        {
            case 4 : position4.setVisibility(View.VISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                root.setVisibility(View.VISIBLE);
                break;
            case 3 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.VISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                root.setVisibility(View.VISIBLE);
                break;
            case 2 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.VISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                root.setVisibility(View.VISIBLE);
                break;
            case 1 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.VISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                root.setVisibility(View.VISIBLE);
                break;
            case 0 :position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.VISIBLE);
                root.setVisibility(View.INVISIBLE);

                break;
        }
    }

    private void loadRepairServiceDistanceData ()
    {

        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action",QueryUtils.GET_REPAIR_SERVICE_ETA);
        map.put("assistance_request_id",assistanceRequestId+"");
        LoadDurationTask loadDurationTask = new LoadDurationTask();
        loadDurationTask.execute(map);
    }

    private void updatePosition ()
    {
        position--;
        switch (position){
            case 3:updatePositionAnimation(position4,position3);
                break;
            case 2:updatePositionAnimation(position3,position2);
                break;
            case 1:updatePositionAnimation(position2,position1);
                break;
            case 0 : fadeOut(positionText);updatePositionAnimation(position1,positoin0);
                replaceText("Votre dépanneur arrive",waitText);
                break;
        }
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

        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.SEND_REQUEST_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            String waitTime = "Temps d'arrivée estimé : "+s;
            positionText.setText(waitTime);
            waitText.setText("Votre dépanneur devra bientot arriver");
            fadeIn(root);
        }
    }
}
