package com.example.nouno.easydep;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class RepairServiceInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private RepairService repairService;
    private TextView toolBarDurationText;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        repairService = new RepairService("Test","EL harach","EL mohamadia, Alger",true,
        1000,10000,36.675442,3.077989,3,100);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_service_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        displayRepairServiceData();
        setSupportActionBar(toolbar);
        hideTitleText();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void hideTitleText()
    {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        collapsingToolbarLayout.setTitle(" ");
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(repairService.getFirstName()+" "+repairService.getLastName());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    //carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

    }

    private void displayRepairServiceData ()
    {
        TextView toolBarDistanceText;
        TextView toolBarNameText;
        RatingBar ratingBar;
        toolBarDistanceText = (TextView)findViewById(R.id.toolbar_distanceText);
        toolBarNameText = (TextView)findViewById(R.id.nameText);
        ratingBar = (RatingBar)findViewById(R.id.toolbar_ratingbar);
        toolBarDistanceText.setText(repairService.getDistanceString());
        toolBarNameText.setText(repairService.getFirstName()+" "+repairService.getLastName());
        ratingBar.setRating(repairService.getRating());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        double latitudecenter = repairService.getLatitude();
        double longitudecenter = repairService.getLongitude();
        LatLng centerauto=new LatLng(latitudecenter,longitudecenter);
        Marker marker1 = map.addMarker(new MarkerOptions().position(centerauto).title("Votre position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        map.moveCamera((CameraUpdateFactory.newLatLngZoom(centerauto,12)));

    }
}
