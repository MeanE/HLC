package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.app.Service;
import android.view.View;

import org.json.JSONObject;

import java.net.URL;

/**
 * Created by MingE on 2015/10/19.
 */
abstract class IFTTT extends Service{
    protected URL url;
    protected Activity act;
    protected View v;

    protected abstract JSONObject getJSON(URL a);

    protected abstract void setURL();

    protected abstract void setUpOnNotification();

    protected abstract void setUpOffNotification();
}
