package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MingE on 2015/9/28.
 */
public class SensorDHT11 extends Sensor {
    private TextView tv_wet, tv_temperature;

    private String wet = "", temperature = "";
    private double temp;

    private final int NOTIFICATION_ID = 0xa1;

    private SharedPreferences sharedPreferences = null;
    private int temperatureUnits = 0;
    private String[] temperatureSign = null;
    private boolean isNotify = true;
    private OnSharedPreferenceChangeListener listener = null;

    @Override
    protected void setURL() {
        String channelID = "51197"; //DHT11(溫溼度)
        String key = "4536EDXSQ5KJKHM3";
        String urlString = "http://api.thingspeak.com/channels/" + channelID + "/feed/last.json" +
                "?key=" + key;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI(Activity act, View v) {
        this.act = act;
        this.v = v;

        tv_wet = (TextView) v.findViewById(R.id.tv_wet);
        tv_temperature = (TextView) v.findViewById(R.id.tv_temperature);
    }

    @Override
    protected JSONObject getJSON() {
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

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        listener = new OnSharedPreferenceChangeListener();
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        temperatureUnits = Integer.parseInt(sharedPreferences.getString("temperatureUnits", "0"));
        temperatureSign = getResources().getStringArray(R.array.temperatureSign);
        isNotify = sharedPreferences.getBoolean("dht11Notification", true);
    }

    private class OnSharedPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
            Log.i("preferenceChanged", "changed");
            switch (key) {
                case "dht11Notification":
                    isNotify = sharedPreferences.getBoolean("dht11Notification", true);
                    if (!isNotify) {
                        //stopForeground(true);

                        //NotificationManager notificationManager = (NotificationManager) getSystemService(act.NOTIFICATION_SERVICE);
                        notificationManager.cancel(NOTIFICATION_ID);
                    } else {
                        setUpNotification();
                    }
                    break;
                case "temperatureUnits":
                    temperatureUnits = Integer.parseInt(sharedPreferences.getString("temperatureUnits", "0"));
                    reSetTemperature();
                    if (isNotify) setUpNotification();
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_temperature.setText(temperature);
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        initSharedPreferences();
        setURL();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        JSONObject jsonObj = getJSON();

                        //setSharedPreferencesListener();
                        wet = String.format("%.1f%%", Double.parseDouble(jsonObj.get("field1").toString()));

                        temp = Double.parseDouble(jsonObj.get("field2").toString());
                        if (temperatureUnits == 1) temp = temp * (9. / 5.) + 32;
                        temperature = String.format("%.1f ", temp) + temperatureSign[temperatureUnits];

                        if (isNotify) setUpNotification();

                        Thread.sleep(22000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void reSetTemperature() {
        if (temperatureUnits == 1) temp = temp * (9. / 5.) + 32;
        else temp = (temp - 32) * 5. / 9.;
        temperature = String.format("%.1f ", temp) + temperatureSign[temperatureUnits];
    }

    @Override
    protected void setUpNotification() {
        NotificationCompat.Builder notifcationCompatBuilder = new NotificationCompat.Builder(this);
        notifcationCompatBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_house);

        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification_sensordht11);
        remoteViews.setTextColor(R.id.notification_wet_text, Color.BLACK);
        remoteViews.setTextViewText(R.id.notification_wet, wet);
        remoteViews.setTextColor(R.id.notification_wet, Color.BLACK);
        remoteViews.setTextColor(R.id.notification_temperature_text, Color.BLACK);
        remoteViews.setTextViewText(R.id.notification_temperature, temperature);
        remoteViews.setTextColor(R.id.notification_temperature, Color.BLACK);

        Notification notification = notifcationCompatBuilder.build();
        notification.contentView = remoteViews;
        notification.flags = Notification.FLAG_NO_CLEAR;

        //startForeground(NOTIFICATION_ID, notification);

        //NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder {
        SensorDHT11 getService() {
            return SensorDHT11.this;
        }

        void start() {
            bindThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!act.isDestroyed()) {
                        try {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_wet.setText(wet);
                                    tv_temperature.setText(temperature);
                                }
                            });

                            if (wet != "" && temperature != "") Thread.sleep(22000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            bindThread.start();
        }
    }
}
