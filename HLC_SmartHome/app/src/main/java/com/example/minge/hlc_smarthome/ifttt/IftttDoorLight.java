package com.example.minge.hlc_smarthome.ifttt;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.minge.hlc_smarthome.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MingE on 2015/10/23.
 */
public class IftttDoorLight extends IFTTT {
    private final int NOTIFICATION_ID = 0xa3;

    private int lastId = -1, id = 0;
    private boolean isError = false;

    private String relay = "relay2";

    @Override
    protected void setURL() {
        String channelID = "55752"; //大門(人體)
        String key = "RJ78ZHEKUT10RQC0";
        String urlString = "http://api.thingspeak.com/channels/" + channelID + "/feed/last.json" +
                "?key=" + key;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject getJSON(URL url) {
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        RUN_THREAD = true;
        notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        setURL();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (RUN_THREAD) {
                    try {
                        JSONObject jsonObj = getJSON(url);

                        id = Integer.parseInt(jsonObj.get("entry_id").toString());
                        if (lastId != id && lastId != -1) isError = true;
                        else isError = false;
                        lastId = id;

                        URL lightUrl = null;
                        if (isError) {
                            JSONObject relay3JsonObj = getJSON(new URL("http://hlcsmarthome.ddns.net:8888/" + relay + "/light.json"));
                            String ralay3Check = relay3JsonObj.get("light").toString();
                            if (ralay3Check.equals("off")) {
                                lightUrl = new URL("http://hlcsmarthome.ddns.net:8888/" + relay + "/?light=on");
                                HttpURLConnection connection = (HttpURLConnection) lightUrl.openConnection();
                                connection.setRequestMethod("GET");
                                //current connect
                                connection.getInputStream();
                                connection.disconnect();

                                setUpOnNotification();
                            }
                        } else {
                            JSONObject relay3JsonObj = getJSON(new URL("http://hlcsmarthome.ddns.net:8888/" + relay + "/light.json"));
                            String ralay3Check = relay3JsonObj.get("light").toString();
                            if (ralay3Check.equals("on")) {
                                lightUrl = new URL("http://hlcsmarthome.ddns.net:8888/" + relay + "/?light=off");
                                HttpURLConnection connection = (HttpURLConnection) lightUrl.openConnection();
                                connection.setRequestMethod("GET");
                                //current connect
                                connection.getInputStream();
                                connection.disconnect();

                                setUpOffNotification();
                            }
                        }

                        Thread.sleep(18000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("IFTTT Door Light", "onDestroy");
        RUN_THREAD = false;
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }

    @Override
    protected void setUpOnNotification() {
        NotificationCompat.Builder notifcationCompatBuilder = new NotificationCompat.Builder(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_light);
        notifcationCompatBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_ifttt)
                .setLargeIcon(bitmap)
                .setContentTitle("門口有人接近")
                .setContentText("電燈已開啟")
                .setDefaults(Notification.DEFAULT_ALL);

        Notification notification = notifcationCompatBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    protected void setUpOffNotification() {
        NotificationCompat.Builder notifcationCompatBuilder = new NotificationCompat.Builder(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_light);
        notifcationCompatBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_ifttt)
                .setLargeIcon(bitmap)
                .setContentTitle("門口淨空")
                .setContentText("電燈已關閉")
                .setDefaults(Notification.DEFAULT_ALL);

        Notification notification = notifcationCompatBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
