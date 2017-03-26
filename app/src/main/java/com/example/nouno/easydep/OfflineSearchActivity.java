package com.example.nouno.easydep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class OfflineSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_search);
        ArrayList<OfflineRepairService> repairServices = new ArrayList<>();
        repairServices.add(new OfflineRepairService(1,"Test","El Harrache","Alger","0549958428",1,RepairService.NO_PRICE));
        repairServices.add(new OfflineRepairService(2,"Hind","Birkhadem","Mostaganem","0549958428",3,50));
        repairServices.add(new OfflineRepairService(3,"Bensaber","Kouba","Chlef","0549958428",4,RepairService.NO_PRICE));
        DBConnection db=new DBConnection(this);
        db.deleteFromDepanneur();
        db.InsetToDB(repairServices);
        repairServices = db.getAllrecords();
        populateRepairServicesList(repairServices);

    }

    private void populateRepairServicesList (ArrayList<OfflineRepairService> repairServices)
    {
        ListView listView = (ListView)findViewById(R.id.list);
        OfflineRepairServiceAdapter offlineRepairServiceAdapter = new OfflineRepairServiceAdapter(this,repairServices);
        listView.setAdapter(offlineRepairServiceAdapter);
    }

}
