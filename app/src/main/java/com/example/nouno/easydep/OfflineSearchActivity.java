package com.example.nouno.easydep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class OfflineSearchActivity extends AppCompatActivity {
    OfflineFilter offlineFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String defaultFiltreJson = gson.toJson(new OfflineFilter());
        String filtreJson = sharedPref.getString("offlineFiltre",defaultFiltreJson);
        offlineFilter = gson.fromJson(filtreJson,OfflineFilter.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_search);
        ArrayList<OfflineRepairService> repairServices = new ArrayList<>();
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

    private void populateRepairServicesList(ArrayList<OfflineRepairService> repairServices) {
        ListView listView = (ListView) findViewById(R.id.list);
        OfflineRepairServiceAdapter offlineRepairServiceAdapter = new OfflineRepairServiceAdapter(this, repairServices);
        listView.setAdapter(offlineRepairServiceAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.offline_search_menu, menu);
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
