package com.example.winther.densiometer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.winther.densiometer.adapters.MeasurementAdapter;
import com.example.winther.densiometer.models.Measurement;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ResultListActivity extends AppCompatActivity {
    private Realm realm;
    private MeasurementAdapter mAdapter;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        realm = Realm.getInstance(realmConfiguration); // Create a new empty instance of Realm

        if (mAdapter == null) {
            List<Measurement> measurements = loadMeasurements();

            //This is the GridView adapter
            mAdapter = new MeasurementAdapter(this);
            mAdapter.setData(measurements);

            //This is the GridView which will display the list of measurements
            listview = (ListView) findViewById(R.id.measurement_list_view);
            assert listview != null;
            listview.setAdapter(mAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.result_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_items:
                // Show dialog to delete all items
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle("Slet alle målinger")
                        .setMessage("Er du sikker på, at du vil slette alle målinger? Dette kan ikke gøres om.")
                        .setNeutralButton("Annuller", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Slet alle målinger", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.deleteAllItems(); // Delete all items
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private List<Measurement> loadMeasurements() {
        // Pull all the cities from the realm
        return realm.where(Measurement.class).findAll();
    }
}
