package com.example.minge.hlc_smarthome.sensor;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.view.View;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by MingE on 2015/9/28.
 */
abstract public class Sensor extends Service {
    protected URL url;
    protected Activity act;
    protected View v;
    protected Thread bindThread = null;
    protected NotificationManager notificationManager;

    protected abstract JSONObject getJSON();

    protected abstract void setURL();

    public abstract void initUI(Activity act, View v);

    protected abstract void setUpNotification();

    public void bindServiceToDead() throws Exception{bindThread.interrupt(); bindThread = null;}

}
