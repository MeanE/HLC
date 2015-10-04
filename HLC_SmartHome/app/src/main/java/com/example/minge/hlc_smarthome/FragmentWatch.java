package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentWatch extends Fragment {
    private Activity act;
    private View v;
    private Sensor sensorDHT11, sensorFire, sensorWash, sensorWindow, sensorCO, sensorDoor;
    private ServiceConnection connSensorDHT11, connSensorFire, connSensorWash, connSensorWindow, connSensorCO, connSensorDoor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_watch, container, false);
        act = getActivity();

        setSensor();

        return v;
    }

    private void setSensor() {
        act = getActivity();

        sensorDHT11 = new SensorDHT11();
        Intent itSensorDHT11 = new Intent(act, SensorDHT11.class);
        act.startService(itSensorDHT11);
        connSensorDHT11 = setServiceConnection("sensorDHT11");
        act.bindService(itSensorDHT11, connSensorDHT11, Context.BIND_AUTO_CREATE);


        sensorFire = new SensorFire();
        Intent itSensorFire = new Intent(act, SensorFire.class);
        connSensorFire = setServiceConnection("sensorFire");
        act.bindService(itSensorFire, connSensorFire, Context.BIND_AUTO_CREATE);

        sensorWash = new SensorWash();
        Intent itSensorWash = new Intent(act, SensorWash.class);
        connSensorWash = setServiceConnection("sensorWash");
        act.bindService(itSensorWash, connSensorWash, Context.BIND_AUTO_CREATE);

        sensorWindow = new SensorWindow();
        Intent itSensorWindow = new Intent(act, SensorWindow.class);
        connSensorWindow = setServiceConnection("sensorWindow");
        act.bindService(itSensorWindow, connSensorWindow, Context.BIND_AUTO_CREATE);

        sensorCO = new SensorCO();
        Intent itSensorCO = new Intent(act, SensorCO.class);
        connSensorCO = setServiceConnection("sensorCO");
        act.bindService(itSensorCO, connSensorCO, Context.BIND_AUTO_CREATE);

        sensorDoor = new SensorDoor();
        Intent itSensorDoor = new Intent(act, SensorDoor.class);
        connSensorDoor = setServiceConnection("sensorDoor");
        act.bindService(itSensorDoor, connSensorDoor, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection setServiceConnection(final String className) {
        ServiceConnection coon = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                switch (className) {
                    case "sensorDHT11":
                        sensorDHT11 = ((SensorDHT11.MyBinder) service).getService();
                        sensorDHT11.initUI(act, v);
                        ((SensorDHT11.MyBinder) service).start();
                        break;
                    case "sensorFire":
                        sensorFire = ((SensorFire.MyBinder) service).getService();
                        sensorFire.initUI(act, v);
                        ((SensorFire.MyBinder) service).start();
                        break;
                    case "sensorWash":
                        sensorWash = ((SensorWash.MyBinder) service).getService();
                        sensorWash.initUI(act, v);
                        ((SensorWash.MyBinder) service).start();
                        break;
                    case "sensorWindow":
                        sensorWindow = ((SensorWindow.MyBinder) service).getService();
                        sensorWindow.initUI(act, v);
                        ((SensorWindow.MyBinder) service).start();
                        break;
                    case "sensorCO":
                        sensorCO = ((SensorCO.MyBinder) service).getService();
                        sensorCO.initUI(act, v);
                        ((SensorCO.MyBinder) service).start();
                        break;
                    case "sensorDoor":
                        sensorDoor = ((SensorDoor.MyBinder) service).getService();
                        sensorDoor.initUI(act, v);
                        ((SensorDoor.MyBinder) service).start();
                        break;
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        return coon;
    }

    @Override
    public void onDestroy() {
        Log.i("Fragment", "onDestroy");
        act.unbindService(connSensorDHT11);
        act.unbindService(connSensorFire);
        act.unbindService(connSensorWash);
        act.unbindService(connSensorWindow);
        act.unbindService(connSensorCO);
        act.unbindService(connSensorDoor);
        super.onDestroy();
    }
}
