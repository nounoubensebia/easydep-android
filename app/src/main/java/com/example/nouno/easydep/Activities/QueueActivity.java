package com.example.nouno.easydep.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.nouno.easydep.R;

public class QueueActivity extends AppCompatActivity {

    private int position=4;
    private View position4;
    private View position3;
    private View position2;
    private View position1;
    private View positoin0;
    private Button button;
    private TextView positionText;
    private View waitText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        position4 = findViewById(R.id.position_4);
        position3 = findViewById(R.id.position_3);
        position2 = findViewById(R.id.position_2);
        position1 = findViewById(R.id.position_1);
        positoin0 = findViewById(R.id.position_0);
        button = (Button)findViewById(R.id.advance);
        positionText = (TextView) findViewById(R.id.position_text);
        waitText = findViewById(R.id.wait_text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePosition();
            }
        });
        //getPosition();
        position = 4;
        setInitialPosition();
    }

    private void getPosition ()
    {
        Bundle extras = getIntent().getExtras();
        position = extras.getInt("position");
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
                break;
            case 3 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.VISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                break;
            case 2 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.VISIBLE);
                position1.setVisibility(View.INVISIBLE);
                break;
            case 1 : position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.VISIBLE);
                positoin0.setVisibility(View.INVISIBLE);
                break;
            case 0 :position4.setVisibility(View.INVISIBLE);
                position3.setVisibility(View.INVISIBLE);
                position2.setVisibility(View.INVISIBLE);
                position1.setVisibility(View.INVISIBLE);
                positoin0.setVisibility(View.VISIBLE);
                break;
        }
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
            case 0 : fadeOut(waitText);updatePositionAnimation(position1,positoin0);replacePositionText();
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

    public void replacePositionText ()
    {
        Animation fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        final Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                positionText.setVisibility(View.VISIBLE);
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
                positionText.setText("Restez sur place votre dépanneur arrive");
                positionText.setVisibility(View.INVISIBLE);
                positionText.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        positionText.startAnimation(fadeOut);
    }
}
