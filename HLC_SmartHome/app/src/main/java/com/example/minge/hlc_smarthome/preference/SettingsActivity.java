package com.example.minge.hlc_smarthome.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.minge.hlc_smarthome.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private SharedPreferences sharedPreferences = null;
    private boolean isOpenSmartLock = true;
    private OnSharedPreferenceChangeListener listener = null;

    private Thread smartLockThread = null;
    private String smartLockSSID = "ESP8266";
    private double distance = -1;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        addPreferencesFromResource(R.xml.preferences);
        initThread();
        initSharedPreferences();
    }

    private void initThread() {
        smartLockThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isOpenSmartLock) {
                    try {
                        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                        for (ScanResult scanResult : wifiManager.getScanResults()) {
                            if (scanResult.SSID.equals(smartLockSSID)) {
                                distance = Math.round((Math.pow(10., (27.55 - (20 * Math.log10(scanResult.frequency)) + Math.abs(scanResult.level)) / 20.))*100.)/100.;
                                break;
                            }
                            distance = -1;
                        }
                        Log.i("distance" , String.valueOf(distance));
                        if (distance > 0 && distance <= 0.5) {
                            //Log.i("distance" , String.valueOf(distance));
                            openSmartLock();
                        }

                        Thread.sleep(7000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private JSONObject getJSON(URL url) {
        JSONObject jsonObj = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String jsonString = reader.readLine();
            reader.close();

            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    private void openSmartLock() {
        JSONObject lockJsonObj = null;
        String lockCheck = null;
        try {
            lockJsonObj = getJSON(new URL("http://hlcsmarthome.ddns.net:8888/door/light.json"));
            lockCheck = lockJsonObj.get("light").toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (lockCheck.equals("off")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        URL url = new URL("http://hlcsmarthome.ddns.net:8888/door/?light=on");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        //current connect
                        connection.getInputStream();
                        connection.disconnect();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);

                        URL url = new URL("http://hlcsmarthome.ddns.net:8888/door/?light=off");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        //current connect
                        connection.getInputStream();
                        connection.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        listener = new OnSharedPreferenceChangeListener();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        isOpenSmartLock = sharedPreferences.getBoolean("smarLockEnable", true);
    }

    private class OnSharedPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged (final SharedPreferences sharedPreferences, final String key) {
            Log.i("preferenceChanged", "changed");
            switch (key) {
                case "smarLockEnable":
                    isOpenSmartLock = sharedPreferences.getBoolean("smarLockEnable", false);
                    if (isOpenSmartLock) {
                        if(!smartLockThread.isAlive()) smartLockThread.start();
                    }
                    else {
                        URL url = null;
                        try {
                            url = new URL("http://hlcsmarthome.ddns.net:8888/door/?light=off");
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            //current connect
                            connection.getInputStream();
                            connection.disconnect();

                            smartLockThread.interrupt();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    private void initToolbar() {
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

