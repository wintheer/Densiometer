package com.example.winther.densiometer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.winther.densiometer.R;
import com.example.winther.densiometer.models.Measurement;

import java.util.List;
import java.util.Locale;

/**
 * Created by Christoffer on 05-05-2016.
 */
public class MeasurementAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Measurement> measurements = null;

    public MeasurementAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    @Override
    public int getCount() {
        if (measurements == null) return 0;

        return measurements.size();
    }

    @Override
    public Object getItem(int position) {
        if (measurements == null || measurements.get(position) == null) {
            return null;
        }
        return measurements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.measurement_list_item, parent, false);
        }

        Measurement measurement = measurements.get(position);

        if (measurement != null) {
            ((TextView) convertView.findViewById(R.id.measurement_list_item_text)).setText(measurement.getMeasurement() +"");
        }

        return convertView;
    }
}
