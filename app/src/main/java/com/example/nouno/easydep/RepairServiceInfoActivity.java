package com.example.nouno.easydep;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.Date;

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
        /*ArrayList<String> comments = new ArrayList<>();
        comments.add("hahaha");
        comments.add("bababa");
        comments.add("sasasa");
        comments.add("dadada");
        populateCommentList(comments);*/
        ArrayList<UserComment> userComments = new ArrayList<>();
        CarOwner carOwner = new CarOwner("Noureddine","Bensebia");
        CarOwner carOwner1 = new CarOwner("Meriem","Bensebia");
        CarOwner carOwner2 = new CarOwner("Thomas","Muller");
        Date date = new Date(1490238425);
        userComments.add(new UserComment(carOwner,4,"tres bon dépanneur",date,false));
        userComments.add(new UserComment(carOwner1,1,"Y3ayi bezzaf bezzaf -_-",date,false));
        userComments.add(new UserComment(carOwner2,5,"Service excellent rien a dire",date,false));
        populateCommentList(userComments);


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

    public void populateCommentList (ArrayList<UserComment> comments)
    {
        ListView listView = (ListView)findViewById(R.id.comments_list);
        UserCommentAdapter userCommentAdapter = new UserCommentAdapter(this,comments);
        listView.setAdapter(userCommentAdapter);
        justifyListViewHeightBasedOnChildren(listView);
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
    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }
}
