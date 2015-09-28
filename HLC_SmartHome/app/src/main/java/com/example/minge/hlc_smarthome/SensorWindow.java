package com.example.minge.hlc_smarthome;

import android.app.Activity;
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
 * Created by MingE on 2015/9/28.
 */
public class SensorWindow extends Sensor {
    TextView tv_window;

    SensorWindow(Activity act, View v) {
        this.act = act;
        this.v = v;
        initUI();
        //setURL
        String channelID = "55750"; //窗戶(門窗)
        String key = "CGKJRKARQD7SE8KR";
        setURL(channelID, key);
    }

    @Override
    protected void setURL(String channelID, String key) {
        String urlString = "http://api.thingspeak.com/channels/" + channelID + "/feed/last.json" +
                "?key=" + key;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        tv_window = (TextView) v.findViewById(R.id.tv_window);
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
        }

        return jsonObj;
    }

    @Override
    public void run() {
        int lastId = -1;
        while (true) {
            try {
                JSONObject jsonObj = getJSON();

                int status = Integer.parseInt(jsonObj.get("field1").toString());
                int id = Integer.parseInt(jsonObj.get("entry_id").toString());

                if (id != lastId && status == 1) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_window.setText("開啟");
                            tv_window.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_exclamation, 0);
                        }
                    });
                } else {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_window.setText("關閉");
                            tv_window.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                        }
                    });
                }
                lastId = id;

                if (status == 1) Thread.sleep(5000);
                else Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.i("Chat", e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
