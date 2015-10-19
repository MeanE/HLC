package com.example.minge.hlc_smarthome;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IftttTemperatureFan extends IFTTT {

    Thread thread = null;
    boolean RUN_THREAD = true;

    final int NOTIFICATION_ID = 0xa2;

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
        setURL();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (RUN_THREAD) {
                    try {
                        JSONObject jsonObj = getJSON(url);

                        String check = jsonObj.get("field3").toString();

                        URL fanUrl = null;
                        if(check.equals("1")){
                            JSONObject relay2JsonObj =getJSON(new URL("http://hlcsmarthome.ddns.net:8888/relay2/light.json"));
                            String ralay2Check = relay2JsonObj.get("light").toString();
                            if(ralay2Check.equals("off")) {
                                fanUrl = new URL("http://hlcsmarthome.ddns.net:8888/relay2/?light=on");
                                HttpURLConnection connection = (HttpURLConnection) fanUrl.openConnection();
                                connection.setRequestMethod("GET");
                                //current connect
                                connection.getInputStream();
                                connection.disconnect();

                                setUpOnNotification();
                            }
                        }
                        else{
                            JSONObject relay2JsonObj =getJSON(new URL("http://hlcsmarthome.ddns.net:8888/relay2/light.json"));
                            String ralay2Check = relay2JsonObj.get("light").toString();
                            if(ralay2Check.equals("on")) {
                                fanUrl = new URL("http://hlcsmarthome.ddns.net:8888/relay2/?light=off");
                                HttpURLConnection connection = (HttpURLConnection) fanUrl.openConnection();
                                connection.setRequestMethod("GET");
                                //current connect
                                connection.getInputStream();
                                connection.disconnect();

                                setUpOffNotification();
                            }
                        }

                        Thread.sleep(15000);
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
        Log.i("IFTTT Temperature Fan", "onDestroy");
        RUN_THREAD = false;
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }

    @Override
    protected void setUpOnNotification() {
        NotificationCompat.Builder notifcationCompatBuilder = new NotificationCompat.Builder(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_fan);
        notifcationCompatBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_ifttt)
                .setLargeIcon(bitmap)
                .setContentTitle("溫度超過30度")
                .setContentText("電風扇已開啟")
                .setDefaults(Notification.DEFAULT_ALL);

        Notification notification = notifcationCompatBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    @Override
    protected void setUpOffNotification() {
        NotificationCompat.Builder notifcationCompatBuilder = new NotificationCompat.Builder(this);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_fan);
        notifcationCompatBuilder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_ifttt)
                .setLargeIcon(bitmap)
                .setContentTitle("溫度低於30度")
                .setContentText("電風扇已關閉")
                .setDefaults(Notification.DEFAULT_ALL);

        Notification notification = notifcationCompatBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
