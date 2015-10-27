package com.example.minge.hlc_smarthome.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.minge.hlc_smarthome.MainActivity;
import com.example.minge.hlc_smarthome.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class FragmentLock extends Fragment {
    private View v;
    private MainActivity act;
    private ImageButton ibtn_lock;
    private TextView tv_lockState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lock, container, false);
        act = (MainActivity) getActivity();

        initUI();
        setLockImage();

        return v;
    }

    private void initUI() {
        ibtn_lock = (ImageButton) v.findViewById(R.id.imageButton2);
        ibtn_lock.setBackgroundResource(0);
        tv_lockState = (TextView) v.findViewById(R.id.tv_lockState);
    }

    private void setLockImage() {
        ibtn_lock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (tv_lockState.getText().equals("關閉")) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ibtn_lock.getDrawable().setAlpha(100);
                        ibtn_lock.invalidate();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Your action here on button click
                        tv_lockState.setText("開啟");
                        tv_lockState.setTextColor(Color.parseColor("#FF9800"));
                        ibtn_lock.setImageResource(R.drawable.ic_lock_open);
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

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tv_lockState.setText("關閉");
                                tv_lockState.setTextColor(Color.parseColor("#4CAF50"));
                                ibtn_lock.setImageResource(R.drawable.ic_lock);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
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
                                        }
                                    }
                                }).start();
                            }
                        }, 10000);

                        ibtn_lock.getDrawable().setAlpha(255);
                        ibtn_lock.invalidate();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.i("FragmentLock", "onDestroy");
        super.onDestroy();
    }
}
