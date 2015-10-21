package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.view.View;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by MingE on 2015/9/28.
 */
abstract  class Sensor extends Service {
    protected URL url;
    protected Activity act;
    protected View v;
    protected Thread bindThread = null;
    protected NotificationManager notificationManager;

    protected abstract JSONObject getJSON();

    protected abstract void setURL();

    protected abstract void initUI(Activity act, View v);

    protected abstract void setUpNotification();

    protected void bindServiceToDead() throws InterruptedException{bindThread.interrupt(); bindThread = null;}

}
