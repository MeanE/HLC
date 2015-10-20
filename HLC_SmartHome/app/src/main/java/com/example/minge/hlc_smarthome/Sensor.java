package com.example.minge.hlc_smarthome;

import android.app.Activity;
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

    protected abstract JSONObject getJSON();

    protected abstract void setURL();

    protected abstract void initUI(Activity act, View v);

    protected abstract void setUpNotification();

}
