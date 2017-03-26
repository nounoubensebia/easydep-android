package com.example.nouno.easydep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class OfflineSearchActivity extends AppCompatActivity {
    OfflineFilter offlineFilter;
    ArrayList<OfflineRepairService> repairServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String defaultFiltreJson = gson.toJson(new OfflineFilter());
        String filtreJson = sharedPref.getString("offlineFiltre",defaultFiltreJson);
        offlineFilter = gson.fromJson(filtreJson,OfflineFilter.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_search);
        repairServices = new ArrayList<>();
        repairServices.add(new OfflineRepairService(1, "Test", "El Harrache", "Alger", "0549958428", 1, RepairService.NO_PRICE));
        repairServices.add(new OfflineRepairService(2, "Hind", "Birkhadem", "Mostaganem", "0549958428", 3, 50));
        repairServices.add(new OfflineRepairService(3, "Bensaber", "Kouba", "Chlef", "0549958428", 1, RepairService.NO_PRICE));
        repairServices.add(new OfflineRepairService(4, "Abri", "Kenza", "Oran", "0549958428", 5, RepairService.NO_PRICE));
        repairServices.add(new OfflineRepairService(5, "Test", "Biskra", "Biskra", "0549958428", 0, 50));
        repairServices.add(new OfflineRepairService(3, "Test", "Bejaia", "Bejaia", "0549958428", 4, 80));
        DBConnection db = new DBConnection(this);
        db.deleteFromDepanneur();
        db.InsetToDB(repairServices);
        repairServices = db.getAllrecords();
        OfflineRepairService.applyFilter(repairServices, offlineFilter);
        populateRepairServicesList(repairServices);
    }

    private void populateRepairServicesList(final ArrayList<OfflineRepairService> repairServices) {
        ListView listView = (ListView) findViewById(R.id.list);
        OfflineRepairServiceAdapter offlineRepairServiceAdapter = new OfflineRepairServiceAdapter(this, repairServices);
        offlineRepairServiceAdapter.setOnButtonClickListener(new OnButtonClickListener<String>() {
            @Override
            public void onButtonClick(String s) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+s));
                startActivity(intent);
            }
        });
        listView.setAdapter(offlineRepairServiceAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.offline_search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.app_bar_searche);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Wilaya");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<OfflineRepairService> filteredList = OfflineRepairService.filterByInput(repairServices,newText);
                populateRepairServicesList(filteredList);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filtres_menu:
                Intent i = new Intent(getApplicationContext(), OfflineFiltreActivity.class);

                startActivity(i);
                return super.onOptionsItemSelected(item);

            case R.id.online_mode:

                Intent i3 = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
