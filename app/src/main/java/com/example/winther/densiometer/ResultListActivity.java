package com.example.winther.densiometer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.winther.densiometer.adapters.MeasurementAdapter;
import com.example.winther.densiometer.models.Measurement;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ResultListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Realm realm;
    private MeasurementAdapter mAdapter;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfiguration); // Create a new empty instance of Realm

        if(mAdapter == null) {
            List<Measurement> measurements = loadMeasurements();

            //This is the GridView adapter
            mAdapter = new MeasurementAdapter(this);
            mAdapter.setData(measurements);

            //This is the GridView which will display the list of measurements
            listview = (ListView) findViewById(R.id.measurement_list_view);
            assert listview != null;
            listview.setAdapter(mAdapter);
            listview.setOnItemClickListener(ResultListActivity.this);
            mAdapter.notifyDataSetChanged();
            listview.invalidate();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.setData(loadMeasurements());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private List<Measurement> loadMeasurements() {
        // Pull all the cities from the realm
        return realm.where(Measurement.class).findAll();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // DO nothing
    }
}
