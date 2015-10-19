package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class FragmentPlug extends Fragment {
    private View v;
    private Activity act;

    ListView listView;
    CharSequence items[] = {"插座1\n投影螢幕", "插座2\n電風扇"};
    CharSequence urlName[] = {"relay1", "relay2"};
    int image[] = {R.drawable.ic_projector_screen, R.drawable.ic_fan};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_plug, container, false);
        act = getActivity();

        setListView();

        return v;
    }

    private void setListView() {
        listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View listInView = act.getLayoutInflater().inflate(R.layout.listview_plug, parent, false);

                TextView item = (TextView) listInView.findViewById(R.id.textView8);
                item.setText(items[position]);
                item.setCompoundDrawablesWithIntrinsicBounds(image[position], 0, 0, 0);
                //item.setCompoundDrawablePadding(5);

                Button btnOn = (Button) listInView.findViewById(R.id.btn_on);
                btnOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = null;
                                    url = new URL("http://hlcsmarthome.ddns.net:8888/" + urlName[position] + "/?light=on");
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
                });

                Button btnOff = (Button) listInView.findViewById(R.id.btn_off);
                btnOff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = null;
                                    url = new URL("http://hlcsmarthome.ddns.net:8888/" + urlName[position] + "/?light=off");
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
                });

                return listInView;
            }
        });
    }
}
