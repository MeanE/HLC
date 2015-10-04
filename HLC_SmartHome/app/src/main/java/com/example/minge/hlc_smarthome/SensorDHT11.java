package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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

    MyBinder myBinder = new MyBinder();

    String wet="", temperature="";
    double temp;

    boolean firstSpinner=true;

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
        sp_temperatureSign = (Spinner) v.findViewById(R.id.sp_temperatureSign);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(act,
                R.array.temperature, R.layout.spinner_temperature_sign);
        adapter.setDropDownViewResource(R.layout.spinner_temperature_sign);
        sp_temperatureSign.setAdapter(adapter);
        sp_temperatureSign.setOnItemSelectedListener(new SpinnerActivity());
    }

    public class SpinnerActivity implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(!firstSpinner) {
                String tempString = tv_temperature.getText().toString();
                Double temp = Double.parseDouble(tempString);
                if (position == 0) temp = (temp - 32) * 5. / 9.;
                else temp = temp * (9. / 5.) + 32;
                tv_temperature.setText(String.format("%.1f", temp));
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setURL();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        JSONObject jsonObj = getJSON();

                        wet = String.format("%.1f%%", Double.parseDouble(jsonObj.get("field1").toString()));

                        //spSelect = sp_temperatureSign.getSelectedItemPosition();
                        temp = Double.parseDouble(jsonObj.get("field2").toString());
                        //if (spSelect == 1) temp = temp * (9. / 5.) + 32;
                        //temperature = String.format("%.1f", temp);
                        /*
                            String dateUntransform = jsonObj.get("created_at").toString();
                            int d = dateUntransform.indexOf("T");
                            int t = dateUntransform.indexOf("+");
                            final String date = String.format("%s %s", dateUntransform.substring(0, d), dateUntransform.substring(d + 1, t));
                            */
                        //Toast.makeText(getApplication(),"dht11",Toast.LENGTH_LONG);
                        Thread.sleep(30000);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    class MyBinder extends Binder {
        SensorDHT11 getService() {
            return SensorDHT11.this;
        }

        void start() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_wet.setText(wet);

                                    //int spSelect = sp_temperatureSign.getSelectedItemPosition();
                                    //if (spSelect == 1) temp = temp * (9. / 5.) + 32;
                                    temperature = String.format("%.1f", temp);
                                    tv_temperature.setText(temperature);
                                }
                            });

                            firstSpinner=false;

                            if(wet!="" && temperature!="") Thread.sleep(30000);
                        } catch (InterruptedException e) {
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
