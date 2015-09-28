package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentWatch extends Fragment {
    private Sensor sensorDHT11, sensorFire, sensorWash, sensorWindow, sensorCO, sensorDoor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_watch, container, false);

        setSensor(v);

        return v;
    }

    private void setSensor(View v) {
        Activity act = getActivity();
        sensorDHT11 = new SensorDHT11(act, v);
        sensorDHT11.start();

        sensorFire = new SensorFire(act, v);
        sensorFire.start();

        sensorWash = new SensorWash(act, v);
        sensorWash.start();

        sensorWindow = new SensorWindow(act, v);
        sensorWindow.start();

        sensorCO = new SensorCO(act, v);
        sensorCO.start();

        sensorDoor = new SensorDoor(act, v);
        sensorDoor.start();
    }
}
