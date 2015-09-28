package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    TextView tv_wet, tv_temperature;
    Spinner sp_temperatureSign;

    SensorDHT11(Activity act, View v) {
        this.act = act;
        this.v = v;
        initUI();
        //setURL
        String channelID = "51197"; //DHT11(溫溼度)
        String key = "4536EDXSQ5KJKHM3";
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
        tv_wet = (TextView) v.findViewById(R.id.tv_wet);
        tv_temperature = (TextView) v.findViewById(R.id.tv_temperature);
        sp_temperatureSign = (Spinner) v.findViewById(R.id.sp_temperatureSign);
        sp_temperatureSign = (Spinner) v.findViewById(R.id.sp_temperatureSign);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(act,
                R.array.temperature, R.layout.spinner_temperature_sign);
        adapter.setDropDownViewResource(R.layout.spinner_temperature_sign);
        sp_temperatureSign.setAdapter(adapter);
        sp_temperatureSign.setOnItemSelectedListener(new SpinnerActivity());
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
        while (true) {
            try {
                JSONObject jsonObj = getJSON();

                final String wet = jsonObj.get("field1").toString() + "%";

                int spSelect = sp_temperatureSign.getSelectedItemPosition();
                double temp = Double.parseDouble(jsonObj.get("field2").toString());
                if (spSelect == 1) temp = temp * (9. / 5.) + 32;
                final String temperature = String.format("%.1f", temp);

                /*
                String dateUntransform = jsonObj.get("created_at").toString();
                int d = dateUntransform.indexOf("T");
                int t = dateUntransform.indexOf("+");
                final String date = String.format("%s %s", dateUntransform.substring(0, d), dateUntransform.substring(d + 1, t));
                */
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_wet.setText(wet);
                        tv_temperature.setText(temperature);
                        //tv_date.setText(date);
                        //test message
                        //Toast.makeText(act.getApplicationContext(), "Received!!!", Toast.LENGTH_LONG).show();
                    }
                });

                Thread.sleep(30000);
            } catch (InterruptedException e) {
                Log.i("Chat", e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class SpinnerActivity implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String tempString = tv_temperature.getText().toString();
            Double temp = Double.parseDouble(tempString);
            if (position == 0) temp = (temp - 32) * 5. / 9.;
            else temp = temp * (9. / 5.) + 32;
            tv_temperature.setText(String.format("%.1f", temp));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
