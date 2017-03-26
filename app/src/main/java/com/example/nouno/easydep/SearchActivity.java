package com.example.nouno.easydep;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ProgressBar;
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
    private ProgressBar searchProgressBar;
    private OnlineFiltre onlineFiltre;
    private Position searchPosition;
    private TextView noRepairServiceFoundTextView;
    private Button mapRefrechButton;
    private Button noNetworkButton;
    private boolean mapMarked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        Gson gson = new Gson();
        String defaultFiltreJson = gson.toJson(new OnlineFiltre());
        String filtreJson = sharedPref.getString("onlineFiltre",defaultFiltreJson);
        onlineFiltre = gson.fromJson(filtreJson,OnlineFiltre.class);
        bottomMargin = 416;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        noNetworkButton = (Button)findViewById(R.id.no_network);
        noNetworkButton.setVisibility(View.GONE);
        searchProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        searchProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.backgroundColor), PorterDuff.Mode.MULTIPLY);
        mapRefrechButton = (Button)findViewById(R.id.refrech_Button);
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
                    markCenter(searchPosition,true);
                    moveMapCamera(searchPosition);
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
        mapRefrechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForRepairServices();
            }
        });
        searchButton.setVisibility(View.GONE);
        Bundle extras = getIntent().getExtras();
        if (extras!=null)
        {
            searchPosition = gson.fromJson(extras.getString("position"),Position.class);
            swipeRefreshLayout.setEnabled(true);
            //Toast.makeText(getApplicationContext(),searchPosition.getLatitude()+" "+searchPosition.getLongitude(),Toast.LENGTH_LONG).show();
            searchForRepairServices();
        }
        else
        {
            mapFab.setVisibility(View.GONE);
            swipeRefreshLayout.setEnabled(false);
            if (isNetworkAvailable())
            {
            searchButton.setVisibility(View.VISIBLE);

            }
            else
            {
                noNetworkButton.setVisibility(View.VISIBLE);
                noNetworkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i3 = new Intent(getApplicationContext(),OfflineSearchActivity.class);
                        startActivity(i3);
                    }
                });
            }
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
                if (isNetworkAvailable())
                {
                Intent i2 = new Intent(getApplicationContext(),ManualSearchActivity.class);
                startActivity(i2);}
                return super.onOptionsItemSelected(item);
            case R.id.offline_list :

                Intent i3 = new Intent(getApplicationContext(),OfflineSearchActivity.class);
                startActivity(i3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void searchForRepairServices ()
    {
        LinkedHashMap<String,String> map= new LinkedHashMap<String,String>();
        map.put("longitude",Double.toString(searchPosition.getLongitude()));
        map.put("latitude",Double.toString(searchPosition.getLatitude()));
        map.put("radius", onlineFiltre.getSearchRadius()*1000+"");
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

    public void markCenter (Position position,boolean showInfoWindow)
    {
        mapMarked = true;
        Double centerLatitude = position.getLatitude();
        Double centerLongitude = position.getLongitude();
        LatLng centerauto=new LatLng(centerLatitude,centerLongitude);
        Marker marker = map.addMarker(new MarkerOptions().position(centerauto).title("Votre position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        if (showInfoWindow)
        marker.showInfoWindow();
        //map.moveCamera((CameraUpdateFactory.newLatLngZoom(centerauto,10)));
    }

    public void moveMapCamera (Position position)
    {
        Double centerLatitude = position.getLatitude();
        Double centerLongitude = position.getLongitude();
        LatLng centerauto=new LatLng(centerLatitude,centerLongitude);
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

    public void populateInfoBottomSheet (final RepairService repairService)
    {
        TextView distanceText = (TextView)findViewById(R.id.distanceTextInfo);
        TextView nameText = (TextView)findViewById(R.id.nameTextInfo);
        TextView durationTextInfo = (TextView)findViewById(R.id.durationTextInfo);
        TextView price = (TextView)findViewById(R.id.priceTextInfo);
        distanceText.setText(repairService.getDistanceString());
        nameText.setText(repairService.getFullName());
        durationTextInfo.setText(repairService.getDurationString());
        RatingBar ratingBar = (RatingBar)findViewById(R.id.infoRatingBar);
        ratingBar.setRating(repairService.getRating());

        TextView availableTextView = (TextView)findViewById(R.id.availableTextInfo);
        if (repairService.getPrice()==RepairService.NO_PRICE)
        {
            //price.setText("Tarifs non disponible");
            price.setText(repairService.NO_PRICE_STRING);
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
        infoBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String json = gson.toJson(repairService);
                String json2 = gson.toJson(searchPosition);
                Intent i = new Intent(getApplicationContext(),RepairServiceInfoActivity.class);
                i.putExtra("repairService",json);
                i.putExtra("searchPosition",json2);
                startActivity(i);
            }
        });
    }

    public void markRepairServices (ArrayList<RepairService> repairServices)
    {
        mapMarked = true;
        for (int i=0;i<repairServices.size();i++)
        {
            RepairService repairService = repairServices.get(i);
            LatLng latLng=new LatLng(repairService.getLatitude(),repairService.getLongitude());
            Marker marker=map.addMarker(new MarkerOptions().position(latLng).title(repairService.getFullName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            marker.setTag(repairService);
        }
        setMapMarkersListeners();

    }
    public void markRepairServices (RepairService selectedRepairService,ArrayList<RepairService> repairServices)
    {
        mapMarked = true;
        for (int i=0;i<repairServices.size();i++)
        {
            RepairService repairService = repairServices.get(i);
            LatLng latLng=new LatLng(repairService.getLatitude(),repairService.getLongitude());
            Marker marker=map.addMarker(new MarkerOptions().position(latLng).title(repairService.getFullName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            if (selectedRepairService.equals(repairService))
            {
                marker.showInfoWindow();
            }
            marker.setTag(repairService);
        }
        setMapMarkersListeners();

    }

    public void setMapMarkersListeners()
    {
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
            if (mapMarked)
            {
                map.clear();
                mapMarked = false;
                markCenter(searchPosition,false);
                infoBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)mapFab.getLayoutParams();
                CoordinatorLayout.LayoutParams layoutParams1 = (CoordinatorLayout.LayoutParams)listFab.getLayoutParams();
                if (layoutParams1.bottomMargin!=layoutParams.bottomMargin)
                changeMargin(listFab,layoutParams.bottomMargin);
            }
            mapRefrechButton.setVisibility(View.GONE);
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
            mapRefrechButton.setVisibility(View.VISIBLE);
            recyclerView = (RecyclerView)findViewById(R.id.repair_services_list);
            repairServices = RepairService.parseJson(s);
            RepairService.applyFiltre(repairServices, onlineFiltre);
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
            //moveMapCamera(searchPosition);
            if (repairServices.size()==0)
            {
                noRepairServiceFoundTextView.setVisibility(View.VISIBLE);
            }
            else
            {
                noRepairServiceFoundTextView.setVisibility(View.GONE);
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
