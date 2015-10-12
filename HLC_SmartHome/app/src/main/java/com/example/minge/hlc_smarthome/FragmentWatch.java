package com.example.minge.hlc_smarthome;

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
    private MainActivity act;
    private View v;
    private Sensor sensorDHT11, sensorFire, sensorWash, sensorWindow, sensorCO, sensorDoor;
    private ServiceConnection connSensorDHT11, connSensorFire, connSensorWash, connSensorWindow, connSensorCO, connSensorDoor;
    private Intent itSensorDHT11, itSensorFire, itSensorWash, itSensorWindow, itSensorCO, itSensorDoor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_watch, container, false);
        act = (MainActivity) getActivity();

        setBindService();

        return v;
    }

    private void setBindService() {
        sensorDHT11 = act.getSensorDHT11();
        itSensorDHT11 = new Intent(act, SensorDHT11.class);
        connSensorDHT11 = setServiceConnection(sensorDHT11.getClass().getSimpleName());
        //Log.i("sensorDHT11", sensorDHT11.getClass().getSimpleName());
        act.bindService(itSensorDHT11, connSensorDHT11, Context.BIND_AUTO_CREATE);

        sensorFire = act.getSensorFire();
        itSensorFire = new Intent(act, SensorFire.class);
        connSensorFire = setServiceConnection(sensorFire.getClass().getSimpleName());
        act.bindService(itSensorFire, connSensorFire, Context.BIND_AUTO_CREATE);

        sensorWash = act.getSensorWash();
        itSensorWash = new Intent(act, SensorWash.class);
        connSensorWash = setServiceConnection(sensorWash.getClass().getSimpleName());
        act.bindService(itSensorWash, connSensorWash, Context.BIND_AUTO_CREATE);

        sensorWindow = act.getSensorWindow();
        itSensorWindow = new Intent(act, SensorWindow.class);
        connSensorWindow = setServiceConnection(sensorWindow.getClass().getSimpleName());
        act.bindService(itSensorWindow, connSensorWindow, Context.BIND_AUTO_CREATE);

        sensorCO = act.getSensorCO();
        itSensorCO = new Intent(act, SensorCO.class);
        connSensorCO = setServiceConnection(sensorCO.getClass().getSimpleName());
        act.bindService(itSensorCO, connSensorCO, Context.BIND_AUTO_CREATE);

        sensorDoor = act.getSensorDoor();
        itSensorDoor = new Intent(act, SensorDoor.class);
        connSensorDoor = setServiceConnection(sensorDoor.getClass().getSimpleName());
        act.bindService(itSensorDoor, connSensorDoor, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection setServiceConnection(final String className) {
        ServiceConnection coon = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                switch (className) {
                    case "SensorDHT11":
                        sensorDHT11 = ((SensorDHT11.MyBinder) service).getService();
                        sensorDHT11.initUI(act, v);
                        ((SensorDHT11.MyBinder) service).start();
                        break;
                    case "SensorFire":
                        sensorFire = ((SensorFire.MyBinder) service).getService();
                        sensorFire.initUI(act, v);
                        ((SensorFire.MyBinder) service).start();
                        break;
                    case "SensorWash":
                        sensorWash = ((SensorWash.MyBinder) service).getService();
                        sensorWash.initUI(act, v);
                        ((SensorWash.MyBinder) service).start();
                        break;
                    case "SensorWindow":
                        sensorWindow = ((SensorWindow.MyBinder) service).getService();
                        sensorWindow.initUI(act, v);
                        ((SensorWindow.MyBinder) service).start();
                        break;
                    case "SensorCO":
                        sensorCO = ((SensorCO.MyBinder) service).getService();
                        sensorCO.initUI(act, v);
                        ((SensorCO.MyBinder) service).start();
                        break;
                    case "SensorDoor":
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
        //act.startService(itSensorCO);
        super.onDestroy();
    }
}
