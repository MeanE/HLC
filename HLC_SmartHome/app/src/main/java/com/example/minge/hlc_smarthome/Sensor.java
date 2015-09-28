package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.view.View;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by MingE on 2015/9/28.
 */
abstract  class Sensor extends Thread {
    protected URL url;
    protected Activity act;
    protected View v;

    public Sensor() {}

    public Sensor(Activity act, View v){}

    protected abstract JSONObject getJSON();

    protected abstract void setURL(String channelID, String key);

    protected abstract void initUI();

}
