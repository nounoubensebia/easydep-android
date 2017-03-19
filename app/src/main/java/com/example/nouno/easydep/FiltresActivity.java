package com.example.nouno.easydep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class FiltresActivity extends AppCompatActivity {
    RadioGroup sortRadioGroup;
    Switch availableSwitch;
    SeekBar radiusBar;
    SeekBar priceBar;
    RatingBar ratingBar;
    TextView radiusText;
    TextView priceText;
    Filtre filtre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        filtre = new Filtre();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtres);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sortRadioGroup = (RadioGroup)findViewById(R.id.sort_radio_group);
        availableSwitch = (Switch)findViewById(R.id.show_not_available);
        availableSwitch.setChecked(true);
        radiusBar = (SeekBar)findViewById(R.id.search_radius_bar);
        priceBar = (SeekBar)findViewById(R.id.price_bar);
        radiusText = (TextView)findViewById(R.id.radius_text);
        priceText = (TextView)findViewById(R.id.priceText);
        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        radiusBar.setMax(Filtre.MAX_RADIUS-Filtre.MIN_RADIUS);
        int radiusProgress = Filtre.DEFAULT_RADIUS-Filtre.MIN_RADIUS;
        radiusBar.setProgress(radiusProgress);
        radiusText.setText(Filtre.DEFAULT_RADIUS+" KM");
        radiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = progress+Filtre.MIN_RADIUS;
                radiusText.setText(radius+" KM");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //priceBar.setMax(Filtre.MAX_PRICE-Filtre.MIN_PRICE);

        int priceProgress = Filtre.MAX_PRICE-Filtre.MIN_PRICE;

        priceText.setText("");

        priceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int price = progress*100+Filtre.MIN_PRICE;
                priceText.setText(price+"Da/Km");
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
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filtres_menu, menu);
        return true;
    }

    public void updateUi ()
    {

    }
}
