package com.example.minge.hlc_smarthome;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentWatch extends Fragment {
    private Sensor_DHT11 sensorDHT11;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_watch, container, false);

        setSensor(v);

        return v;
    }

    private void setSensor(View v) {
        sensorDHT11=new Sensor_DHT11(getActivity(), v);
        sensorDHT11.start();
    }

}
