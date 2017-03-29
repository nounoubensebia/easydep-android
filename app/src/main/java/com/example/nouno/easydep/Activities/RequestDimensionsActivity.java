package com.example.nouno.easydep.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.nouno.easydep.Data.AssistanceRequest;
import com.example.nouno.easydep.R;
import com.google.gson.Gson;

public class RequestDimensionsActivity extends AppCompatActivity {
    private AssistanceRequest assistanceRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        retrieveData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_dimensions);
        displayInfo(assistanceRequest);
    }
    private void retrieveData()
    {
        Gson gson = new Gson();
        String json = getIntent().getExtras().getString("assistanceRequest");
        assistanceRequest = gson.fromJson(json,AssistanceRequest.class);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.request_dimensions_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.save_menu)
        {
            Gson gson = new Gson();
            String json = gson.toJson(assistanceRequest);
            Intent i = new Intent(getApplicationContext(),AssistanceRequestActivity.class);
            i.putExtra("assistanceRequest",json);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        assistanceRequest.setHeavy(false);
        Gson gson = new Gson();
        String json = gson.toJson(assistanceRequest);
        Intent i = new Intent(getApplicationContext(),AssistanceRequestActivity.class);
        i.putExtra("assistanceRequest",json);
        startActivity(i);
    }

    private void displayInfo (final AssistanceRequest heavyAssistanceRequest)
    {
        final TextView lengthText = (TextView)findViewById(R.id.length_text);
        final TextView weightText = (TextView)findViewById(R.id.weight_text);
        final CheckBox dontKnowWeight = (CheckBox)findViewById(R.id.weight_dont_know);
        final CheckBox dontKnowLength = (CheckBox)findViewById(R.id.length_dont_know);


        final SeekBar lenghSeekbar = (SeekBar)findViewById(R.id.length_seekbar);
        final SeekBar weightSeekbar = (SeekBar)findViewById(R.id.weight_seekbar);
        lenghSeekbar.setMax((int)(AssistanceRequest.MAX_LENGTH-AssistanceRequest.MIN_LENGTH)*100);
        weightSeekbar.setMax((int)(AssistanceRequest.MAX_WEIGHT-AssistanceRequest.MIN_WEIGHT)*100);
        if (heavyAssistanceRequest.getLength()==AssistanceRequest.DONT_KNOW)
        {
            lengthText.setText("Non défini");
            dontKnowLength.setChecked(true);
        }
        else
        {
            lengthText.setText(heavyAssistanceRequest.getLength()+"M");
            lenghSeekbar.setProgress((int)((heavyAssistanceRequest.getLength()-AssistanceRequest.MIN_LENGTH)*100));
        }

        if (heavyAssistanceRequest.getWeight()!=AssistanceRequest.DONT_KNOW)
        {
            weightText.setText(heavyAssistanceRequest.getWeight()+"Tonnes");
            weightSeekbar.setProgress((int)((heavyAssistanceRequest.getWeight()-AssistanceRequest.MIN_WEIGHT)*100));
        }
        else
        {
            weightText.setText("Non défini");
            dontKnowWeight.setChecked(true);
        }

        dontKnowLength.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                {
                    if (heavyAssistanceRequest.getLength()==AssistanceRequest.DONT_KNOW)
                    heavyAssistanceRequest.setLength(AssistanceRequest.MIN_LENGTH);
                    lengthText.setText(heavyAssistanceRequest.getLength()+"M");
                    lenghSeekbar.setProgress((int)((heavyAssistanceRequest.getLength()-AssistanceRequest.MIN_LENGTH)*100));
                }
                else
                {
                    heavyAssistanceRequest.setLength(AssistanceRequest.DONT_KNOW);
                    lengthText.setText("Non défini");
                    lenghSeekbar.setProgress(0);
                }
            }
        });

        dontKnowWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                {
                    if (heavyAssistanceRequest.getWeight()==AssistanceRequest.DONT_KNOW)
                    heavyAssistanceRequest.setWeight(AssistanceRequest.MIN_WEIGHT);
                    weightText.setText(heavyAssistanceRequest.getWeight()+"Tonnes");
                    weightSeekbar.setProgress((int)((heavyAssistanceRequest.getWeight()-AssistanceRequest.MIN_WEIGHT)*100));
                }
                else
                {
                    heavyAssistanceRequest.setWeight(AssistanceRequest.DONT_KNOW);
                    weightText.setText("Non défini");
                    weightSeekbar.setProgress(0);
                }
            }
        });






        lenghSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                {

                float length = (((float)(progress/100))+AssistanceRequest.MIN_LENGTH);
                lengthText.setText(length+"M");
                assistanceRequest.setLength(length);
                    if (dontKnowLength.isChecked())
                        dontKnowLength.setChecked(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        weightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                {

                float weight = (((float)(progress/100))+AssistanceRequest.MIN_WEIGHT);
                weightText.setText(weight+"Tonnes");
                assistanceRequest.setWeight(weight);
                    if (dontKnowWeight.isChecked())
                        dontKnowWeight.setChecked(true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
