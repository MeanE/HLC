package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
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
 * Created by MingE on 2015/9/29.
 */
public class SensorDoor extends Sensor {
    TextView tv_door;

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
    protected void initUI(Activity act, View v) {
        this.act = act;
        this.v = v;

        tv_door = (TextView) v.findViewById(R.id.tv_door);
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
        setURL();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void setUpNotification() {

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
        SensorDoor getService() {
            return SensorDoor.this;
        }

        void start() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int lastId = -1;
                    while (true) {
                        try {
                            JSONObject jsonObj = getJSON();

                            //int status = Integer.parseInt(jsonObj.get("field1").toString());
                            int id = Integer.parseInt(jsonObj.get("entry_id").toString());

                            if (lastId == -1) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_door.setText("淨空");
                                        tv_door.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                                    }
                                });
                            } else {
                                if (id != lastId) {
                                    act.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_door.setText("有異物");
                                            tv_door.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_exclamation, 0);
                                        }
                                    });
                                    Thread.sleep(18000);
                                } else {
                                    act.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_door.setText("淨空");
                                            tv_door.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                                        }
                                    });
                                    Thread.sleep(1000);
                                }
                            }
                            lastId = id;
                        } catch (InterruptedException e) {
                            Log.i("Chat", e.getMessage());
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
}