package com.example.nouno.easydep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;

public class FiltresActivity extends AppCompatActivity {
    RadioGroup sortRadioGroup;
    Switch availableSwitch;
    SeekBar radiusBar;
    SeekBar priceBar;
    RatingBar ratingBar;
    TextView radiusText;
    TextView priceText;
    View upImage;
    OnlineFiltre onlineFiltre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //onlineFiltre = new OnlineFiltre();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String defaultFiltreJson = gson.toJson(new OnlineFiltre());
        String filtreJson = sharedPref.getString("onlineFiltre",defaultFiltreJson);
        onlineFiltre = gson.fromJson(filtreJson,OnlineFiltre.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtres);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        upImage = findViewById(R.id.go_button);
        sortRadioGroup = (RadioGroup)findViewById(R.id.sort_radio_group);
        availableSwitch = (Switch)findViewById(R.id.show_not_available);
        availableSwitch.setChecked(true);
        radiusBar = (SeekBar)findViewById(R.id.search_radius_bar);
        priceBar = (SeekBar)findViewById(R.id.price_bar);
        radiusText = (TextView)findViewById(R.id.radius_text);
        priceText = (TextView)findViewById(R.id.priceText);
        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        radiusBar.setMax(OnlineFiltre.MAX_RADIUS- OnlineFiltre.MIN_RADIUS);
        listenFilter();

        upImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(onlineFiltre);
                //Toast.makeText(getApplicationContext(),json,Toast.LENGTH_LONG).show();
                editor.putString("onlineFiltre",json);
                editor.commit();
                //editor.apply();
                startSearchActivity ();
            }
        });
        filtreUi(onlineFiltre);

    }

    private void listenFilter()
    {
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = progress+ OnlineFiltre.MIN_RADIUS;
                radiusText.setText(radius+" KM");
                onlineFiltre.setSearchRadius(radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        priceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!(progress==0&&fromUser==false))
                {
                    int price = progress*100+ OnlineFiltre.MIN_PRICE;
                    priceText.setText(price+"Da/Km");
                    onlineFiltre.setMaxPrice(price);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int rat = (int)rating;
                ratingBar.setRating(rat);
                onlineFiltre.setMinRating(rat);
            }
        });
        sortRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.sort_by_distance : onlineFiltre.setSortingMethod(OnlineFiltre.SORT_BY_DISTANCE);
                        break;
                    case R.id.sort_by_price : onlineFiltre.setSortingMethod(OnlineFiltre.SORT_BY_PRICE);
                        break;
                    case R.id.sort_by_rating : onlineFiltre.setSortingMethod(OnlineFiltre.SORT_BY_RATING);
                        break;
                }

            }
        });
        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onlineFiltre.setShowNotAvailable(isChecked);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filtres_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_filtres:
                onlineFiltre = new OnlineFiltre();
                filtreUi(onlineFiltre);
                return super.onOptionsItemSelected(item);

            case android.R.id.home :  onBackPressed();
                return super.onOptionsItemSelected(item);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void filtreUi (OnlineFiltre onlineFiltre)
    {
        switch (onlineFiltre.getSortingMethod())
        {
            case OnlineFiltre.SORT_BY_RATING : sortRadioGroup.check(R.id.sort_by_rating);
                break;
            case OnlineFiltre.SORT_BY_PRICE : sortRadioGroup.check(R.id.sort_by_price);
                break;
            case OnlineFiltre.SORT_BY_DISTANCE : sortRadioGroup.check(R.id.sort_by_distance);
                break;
        }
        availableSwitch.setChecked(onlineFiltre.isShowNotAvailable());
        radiusBar.setProgress(onlineFiltre.getSearchRadius()- OnlineFiltre.MIN_RADIUS);
        if (onlineFiltre.getMaxPrice()==RepairService.NO_PRICE)
        {
            priceText.setText("");
            priceBar.setProgress(0);
        }
        else
        {
            priceText.setText(onlineFiltre.getMaxPrice()+"Da/Km");
            priceBar.setProgress(onlineFiltre.getMaxPrice()/100);
        }
        ratingBar.setRating(onlineFiltre.getMinRating());
    }
    public void startSearchActivity ()
    {
        Intent i = new Intent(getApplicationContext(),SearchActivity.class);
        Bundle extras = getIntent().getExtras();
        if (extras!=null&&extras.containsKey("position"))
        {

            i.putExtra("position",extras.getString("position"));
        }
        startActivity(i);
    }
}
