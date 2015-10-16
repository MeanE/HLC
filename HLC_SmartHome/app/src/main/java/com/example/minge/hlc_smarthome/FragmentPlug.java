package com.example.minge.hlc_smarthome;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import java.util.ArrayList;
import java.util.List;

public class FragmentPlug extends Fragment {
    private View v, listInView;
    private Activity act;

    ListView listView;
    CharSequence items[] = {"投影螢幕", "插座"};
    List<View> list = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_plug, container, false);
        act = getActivity();

        listInView = inflater.inflate(R.layout.listview_plug, container, false);
        setListView();

        return v;
    }

    private void setListView() {
        listView = (ListView) v.findViewById(R.id.listView);
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) view;
                listView.getItemAtPosition(position).;
            }
        });*/

        for (int i = 0; i < items.length; i++) {

            LayoutInflater inflater = (LayoutInflater) act.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listInView = inflater.inflate(R.layout.listview_plug, null);
            //listInView = getActivity().getLayoutInflater().inflate(R.layout.listview_plug, null);
            //RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.listLayout);
            /*
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(act, "list click" + items[index], Toast.LENGTH_SHORT);
                }
            });*/

            TextView item = (TextView) listInView.findViewById(R.id.textView8);
            item.setText(items[i]);

            Button btnOn, btnOff;
            btnOn = (Button) listInView.findViewById(R.id.btn_on);

            final int finalI = i;
            btnOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = null;
                                url = new URL("http://hlcsmarthome.ddns.net:8888/relay" + (finalI +1) + "/?light=on");
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
            btnOff = (Button) listInView.findViewById(R.id.btn_off);
            btnOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = null;
                                url = new URL("http://hlcsmarthome.ddns.net:8888/relay" + (finalI + 1) + "/?light=off");
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

            list.add(listInView);
        }

        listView.setAdapter(new BaseAdapter() {
            /*
            @Override
            public boolean isEnabled(int position) {
                return false;
            }*/

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
            public View getView(int position, View convertView, ViewGroup parent) {
                return list.get(position);
            }
        });
    }
}
