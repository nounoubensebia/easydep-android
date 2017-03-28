package com.example.nouno.easydep.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nouno.easydep.Data.Position;
import com.example.nouno.easydep.QueryUtils;
import com.example.nouno.easydep.R;
import com.example.nouno.easydep.Data.SearchSuggestion;
import com.example.nouno.easydep.ListAdapters.SearchSuggestionAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ManualSearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    ListView listView;
    ManualSearchActivity manualSearchActivity;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Position userPosition;
    private TextView userLocationText;
    private View userPositionLayout;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_search);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView)findViewById(R.id.list);
        manualSearchActivity=this;
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        userLocationText = (TextView)findViewById(R.id.user_position_text);
        userPositionLayout = findViewById(R.id.user_position_layout);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manual_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_searche);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setFocusable(true);
        searchView.setQueryHint("Lieu de recherche");
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length()>1)
                {
                    SuggestionsTask suggestionsTask = new SuggestionsTask();
                    suggestionsTask.execute(newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public class SuggestionsTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {
            LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap = QueryUtils.buildSearchSuggestionsParamsMap(params[0]);
            String response = QueryUtils.makeHttpGetRequest(QueryUtils.GET_PLACE_PREDICTIONS_URL,linkedHashMap);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            final ArrayList<SearchSuggestion> arrayList = SearchSuggestion.parseJson(s);
            SearchSuggestionAdapter searchSuggestionAdapter = new SearchSuggestionAdapter(manualSearchActivity,arrayList);
            listView.setDividerHeight(0);
            listView.setAdapter(searchSuggestionAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GetPositionTask getPositionTask = new GetPositionTask();
                    getPositionTask.execute(arrayList.get(position));
                }
            });
        }
    }

    public class GetPositionTask extends AsyncTask<SearchSuggestion,Void,Position> {

        @Override
        protected Position doInBackground(SearchSuggestion... params) {
            return (params[0].getPosition());
        }

        @Override
        protected void onPostExecute(Position position) {
            Gson gson = new Gson();
            String positionJson = gson.toJson(position);
            Intent i = new Intent(getApplicationContext(),SearchActivity.class);
            i.putExtra("position",positionJson);
            startActivity(i);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(
                    this, // Activity
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            googleApiClient.disconnect();
            return;

        }


        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onLocationChanged(Location location) {
        Double latitude =location.getLatitude();
        Double longitude = location.getLongitude();
        userPosition = new Position(latitude,longitude);
        GetUserPositionTask getUserPositionTask = new GetUserPositionTask();
        getUserPositionTask.execute(userPosition);
        googleApiClient.disconnect();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    googleApiClient.connect();


                } else {


                }
                return;
            }
        }
    }

    public class GetUserPositionTask extends AsyncTask<Position,Void,Position>
    {

        @Override
        protected Position doInBackground(Position... params) {
            LinkedHashMap map = new LinkedHashMap();
            Position position = null;
            map.put("latlng",params[0].getLatitude()+","+params[0].getLongitude());
            String response = QueryUtils.makeHttpGetRequest(QueryUtils.GET_USER_LOCATION_NAME_URL,map);
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray resultes = jsonObject.getJSONArray("results");
                JSONObject result = resultes.getJSONObject(0);
                String loc = result.getString("formatted_address");
                String[] tab = loc.split(", Algérie");
                String location =tab[0];
                position =params[0];
                position.setLocationName(location);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return position;

        }

        @Override
        protected void onPostExecute(final Position position) {
            userPosition = position;
            userLocationText.setText(position.getLocationName());
            userPositionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    String positionJson = gson.toJson(position);
                    Intent i = new Intent(getApplicationContext(),SearchActivity.class);
                    i.putExtra("position",positionJson);
                    startActivity(i);
                }
            });
        }
    }


}
