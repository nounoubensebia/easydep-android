package com.example.nouno.easydep.Activities;

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
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.nouno.easydep.Data.OfflineFilter;
import com.example.nouno.easydep.Data.OnlineFiltre;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.Data.RepairService;
import com.example.nouno.easydep.Utils;
import com.google.gson.Gson;

public class OfflineFiltreActivity extends AppCompatActivity {
    OfflineFilter offlineFilter;
    RadioGroup radioGroup;
    TextView priceText;
    SeekBar priceBar;
    RatingBar ratingBar;
    View upImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_filtre);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String defaultFiltreJson = gson.toJson(new OfflineFilter());
        String filtreJson = sharedPref.getString(Utils.OFFLINEFILTER_KEY,defaultFiltreJson);
        offlineFilter = gson.fromJson(filtreJson,OnlineFiltre.class);
        radioGroup = (RadioGroup)findViewById(R.id.sort_radio_group);
        priceText= (TextView)findViewById(R.id.priceText);
        priceBar = (SeekBar)findViewById(R.id.price_bar);
        ratingBar = (RatingBar)findViewById(R.id.ratingbar);
        upImage = findViewById(R.id.go_button);
        upImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(offlineFilter);
                //Toast.makeText(getApplicationContext(),json,Toast.LENGTH_LONG).show();
                editor.putString(Utils.OFFLINEFILTER_KEY,json);
                editor.commit();
                //editor.apply();
                Intent i = new Intent(getApplicationContext(),OfflineSearchActivity.class);
                startActivity(i);
            }
        });
        filtreUI(offlineFilter);
        listenFilter();
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
                offlineFilter = new OfflineFilter();
                filtreUI(offlineFilter);
                return super.onOptionsItemSelected(item);

            case android.R.id.home :  onBackPressed();
                return super.onOptionsItemSelected(item);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void listenFilter()
    {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId)
                {
                    case R.id.sort_by_wilaya:offlineFilter.setSortingMethod(offlineFilter.SORT_BY_LOCATION);
                        break;
                    case R.id.sort_by_rating:offlineFilter.setSortingMethod(offlineFilter.SORT_BY_RATING);
                        break;
                    case R.id.sort_by_price:offlineFilter.setSortingMethod(offlineFilter.SORT_BY_PRICE);
                        break;
                }

            }
        });
        priceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!(progress==0&&fromUser==false))
                {
                    int price = progress*100+ OnlineFiltre.MIN_PRICE;
                    priceText.setText(price+"Da/Km");
                    offlineFilter.setMaxPrice(price);
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
                offlineFilter.setMinRating(rat);
            }
        });

    }

    private void filtreUI (OfflineFilter offlineFilter)
    {
        switch (offlineFilter.getSortingMethod())
        {
            case OfflineFilter.SORT_BY_LOCATION : radioGroup.check(R.id.sort_by_wilaya);
                break;
            case OfflineFilter.SORT_BY_PRICE:  radioGroup.check(R.id.sort_by_price);
                break;
            case OfflineFilter.SORT_BY_RATING: radioGroup.check(R.id.sort_by_rating);
                break;
        }
        if (offlineFilter.getMaxPrice() == RepairService.NO_PRICE)
        {
            priceText.setText(" ");
            priceBar.setProgress(0);
        }
        else
        {
            priceText.setText(offlineFilter.getMaxPrice()+"");
            priceBar.setProgress(offlineFilter.getMaxPrice());
        }

        ratingBar.setRating(offlineFilter.getMinRating());

    }
}
