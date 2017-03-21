package com.example.nouno.easydep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nouno.easydep.exceptions.ConnectionProblemException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback{
    private RecyclerView recyclerView;
    private ArrayList<RepairService> repairServices;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomSheetBehavior mapBottomSheetBehavior;
    private View mapFab;
    private View listFab;
    private View mapBottomSheet;
    private View infoBottomSheet;
    private BottomSheetBehavior infoBottomSheetBehaviour;
    private GoogleMap map;
    private int bottomMargin;
    private Button searchButton;
    private Filtre filtre;
    private Position searchPosition;
    private TextView noRepairServiceFoundTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        Gson gson = new Gson();
        String defaultFiltreJson = gson.toJson(new Filtre());
        String filtreJson = sharedPref.getString("filtre",defaultFiltreJson);
        filtre = gson.fromJson(filtreJson,Filtre.class);


        bottomMargin = 416;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        repairServices = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refrech_layout);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1,R.color.refresh_progress_2,R.color.refresh_progress_3);
        noRepairServiceFoundTextView = (TextView)findViewById(R.id.no_repair_service_found);
        noRepairServiceFoundTextView.setVisibility(View.GONE);
        mapBottomSheet = findViewById(R.id.map_bottom_sheet);
        mapBottomSheetBehavior = BottomSheetBehavior.from(mapBottomSheet);
        mapBottomSheetBehavior.setHideable(true);
        mapBottomSheetBehavior.setSkipCollapsed(true);
        mapBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        infoBottomSheet = findViewById(R.id.bottomInfo);
        infoBottomSheetBehaviour = BottomSheetBehavior.from(infoBottomSheet);
        infoBottomSheetBehaviour.setHideable(true);
        infoBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        searchButton = (Button) findViewById(R.id.search_for_repair_services);
        infoBottomSheetBehaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                CoordinatorLayout.LayoutParams layoutParams1 = (CoordinatorLayout.LayoutParams)listFab.getLayoutParams();
                if (newState == BottomSheetBehavior.STATE_EXPANDED)
                {
                    layoutParams1.bottomMargin=bottomMargin;
                    enter(listFab);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        listFab=findViewById(R.id.listFab);
        mapFab=findViewById(R.id.mapFab);
        listFab.setVisibility(View.GONE);
        mapFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_HIDDEN)
                {
                    mapBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    CoordinatorLayout.LayoutParams maplayoutParams = (CoordinatorLayout.LayoutParams) mapFab.getLayoutParams();
                    CoordinatorLayout.LayoutParams listlayoutParams = (CoordinatorLayout.LayoutParams) listFab.getLayoutParams();
                    listlayoutParams.bottomMargin=maplayoutParams.bottomMargin;
                    exit(mapFab);
                    enter(listFab);
                }
            }
        });
        listFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
                {
                    infoBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                    mapBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    exit(listFab);
                    enter(mapFab);
                    //markCenter(new Position(36.708630, 3.212020));
                }
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               searchForRepairServices();
            }
        });
        searchButton.setVisibility(View.GONE);
        Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {

            searchPosition = gson.fromJson(extras.getString("position"),Position.class);
            //Toast.makeText(getApplicationContext(),searchPosition.getLatitude()+" "+searchPosition.getLongitude(),Toast.LENGTH_LONG).show();
            searchForRepairServices();
        }
        else
        {
            searchButton.setVisibility(View.VISIBLE);
            //googleApiClient.connect();
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i2 = new Intent(getApplicationContext(),ManualSearchActivity.class);
                startActivity(i2);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.filtres_menu:
                Intent i = new Intent(getApplicationContext(),FiltresActivity.class);

                Gson gson = new Gson();
                if (searchPosition!=null){
                String positionString = gson.toJson(searchPosition);
                i.putExtra("position",positionString);}
                startActivity(i);
                return super.onOptionsItemSelected(item);

            case R.id.app_bar_search:
                Intent i2 = new Intent(getApplicationContext(),ManualSearchActivity.class);


                startActivity(i2);
                return super.onOptionsItemSelected(item);


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void searchForRepairServices ()
    {
        LinkedHashMap<String,String> map= new LinkedHashMap<String,String>();
        map.put("longitude",Double.toString(searchPosition.getLongitude()));
        map.put("latitude",Double.toString(searchPosition.getLatitude()));
        map.put("radius",filtre.getSearchRadius()*1000+"");
        SearchTask searchTask = new SearchTask();
        searchTask.execute(map);

    }

    public void enter (final View view)
    {
        view.setVisibility(View.VISIBLE);
        final Animation fabEnter = AnimationUtils.loadAnimation(view.getContext(),R.anim.fabenter);
        fabEnter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fabEnter);

    }


    public void exit (final View view)
    {
        final Animation fabExit = AnimationUtils.loadAnimation(view.getContext(),R.anim.fabexit);
        fabExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fabExit);
    }

    public void changeMargin (final View view, final int newMargin)
    {
        //view.setVisibility(View.VISIBLE);
        final Animation fabExit = AnimationUtils.loadAnimation(view.getContext(),R.anim.fabexit);
        final Animation fabEnter = AnimationUtils.loadAnimation(view.getContext(),R.anim.fabenter);
        fabExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)view.getLayoutParams();
                layoutParams.bottomMargin = newMargin;
                view.setLayoutParams(layoutParams);
                enter(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fabExit);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                infoBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)mapFab.getLayoutParams();
                changeMargin(listFab,layoutParams.bottomMargin);
            }
        });
        //markCenter(new Position(36.708630, 3.212020));
    }

    public void markCenter (Position position)
    {
        Double centerLatitude = position.getLatitude();
        Double centerLongitude = position.getLongitude();
        LatLng centerauto=new LatLng(centerLatitude,centerLongitude);
        Marker marker = map.addMarker(new MarkerOptions().position(centerauto).title("Votre position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        marker.showInfoWindow();
        map.moveCamera((CameraUpdateFactory.newLatLngZoom(centerauto,10)));
    }

    @Override
    public void onBackPressed() {
        if (mapBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
        {
            infoBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
            mapBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            exit(listFab);
            enter(mapFab);
            //markCenter(new Position(36.708630, 3.212020));
        }
        else {
        super.onBackPressed();
        }
    }

    public void populateInfoBottomSheet (RepairService repairService)
    {
        TextView distanceText = (TextView)findViewById(R.id.distanceTextInfo);
        TextView nameText = (TextView)findViewById(R.id.nameTextInfo);
        TextView durationTextInfo = (TextView)findViewById(R.id.durationTextInfo);
        TextView price = (TextView)findViewById(R.id.priceTextInfo);
        distanceText.setText(repairService.getDistanceString());
        nameText.setText(repairService.getFirstName()+" "+repairService.getLastName());
        durationTextInfo.setText(repairService.getDurationString());
        RatingBar ratingBar = (RatingBar)findViewById(R.id.infoRatingBar);
        ratingBar.setRating(repairService.getRating());

        TextView availableTextView = (TextView)findViewById(R.id.availableTextInfo);
        if (repairService.getPrice()==RepairService.NO_PRICE)
        {
            price.setText("Tarifs non disponible");
            //price.setVisibility(View.GONE);
            //bottomMargin = 400;
        }
        else
        {
            bottomMargin=416;
            price.setVisibility(View.VISIBLE);
            price.setText(repairService.getPriceString());
        }
        if (repairService.isAvailable())
        {
            availableTextView.setText("Disponible");
            availableTextView.setTextColor(getResources().getColor(R.color.green));
        }
        else
        {
            availableTextView.setTextColor(Color.parseColor("#F44336"));
            availableTextView.setText("Occupé");
        }
    }

    public void markRepairServices (ArrayList<RepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {
            RepairService repairService = repairServices.get(i);
            LatLng latLng=new LatLng(repairService.getLatitude(),repairService.getLongitude());
            Marker marker=map.addMarker(new MarkerOptions().position(latLng).title(repairService.getFirstName()+" "+repairService.getLastName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            marker.setTag(repairService);
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag()!=null)
                {
                    populateInfoBottomSheet((RepairService)marker.getTag());
                    if (infoBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN)
                    {
                        exit(listFab);
                        infoBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
                return false;
            }
        });


    }
    public void markRepairServices (RepairService selectedRepairService,ArrayList<RepairService> repairServices)
    {
        for (int i=0;i<repairServices.size();i++)
        {
            RepairService repairService = repairServices.get(i);
            LatLng latLng=new LatLng(repairService.getLatitude(),repairService.getLongitude());
            Marker marker=map.addMarker(new MarkerOptions().position(latLng).title(repairService.getFirstName()+" "+repairService.getLastName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            if (selectedRepairService.equals(repairService))
            {
                marker.showInfoWindow();
            }
            marker.setTag(repairService);
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTag()!=null)
                {
                    populateInfoBottomSheet((RepairService)marker.getTag());
                    if (infoBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN)
                    {
                        exit(listFab);
                        infoBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
                return false;
            }
        });

    }

    public void exitInfo (final View view)
    {
        final Animation fabExit = AnimationUtils.loadAnimation(view.getContext(),R.anim.fabexit);
        fabExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                mapBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                infoBottomSheetBehaviour.setState(infoBottomSheetBehaviour.STATE_EXPANDED);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fabExit);
    }




    private class SearchTask extends AsyncTask<Map<String,String>,Void,String>
    {
        @Override
        protected void onPreExecute() {
            recyclerView = (RecyclerView)findViewById(R.id.repair_services_list);
            recyclerView.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(true);

        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String answer=null;
            try {
                answer = QueryUtils.makeHttpPostRequest(QueryUtils.LOCAL_GET_REPAIR_SERVICES_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
                return QueryUtils.CONNECTION_PROBLEM;
               }
            return answer;
        }

        @Override
        protected void onPostExecute(String s) {
            recyclerView = (RecyclerView)findViewById(R.id.repair_services_list);
            repairServices = RepairService.parseJson(s);
            RepairService.applyFiltre(repairServices,filtre);
           // RepairService.deleteNotAvailable(repairServices);

            RepairServiceAdapter adapter = new RepairServiceAdapter(repairServices, R.layout.repair_service_list_item);
            adapter.setOnItemClickListner(new OnItemClickListner() {
                @Override
                public void onItemClick(View v, int position) {
                    RepairService repairService = repairServices.get(position);
                    populateInfoBottomSheet(repairService);
                    markRepairServices(repairService,repairServices);
                    LatLng latLng = new LatLng(repairService.getLatitude(),repairService.getLongitude());
                    map.moveCamera((CameraUpdateFactory.newLatLngZoom(latLng,10)));
                    exitInfo(mapFab);

                }
            });
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            markRepairServices(repairServices);
            markCenter(searchPosition);
            if (repairServices.size()==0)
            {
                noRepairServiceFoundTextView.setVisibility(View.VISIBLE);
            }
        }
    }


}
