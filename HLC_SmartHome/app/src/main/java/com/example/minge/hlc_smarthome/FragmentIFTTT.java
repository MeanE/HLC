package com.example.minge.hlc_smarthome;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FragmentIFTTT extends Fragment {
    private View v;
    private MainActivity act;

    ListView listView;
    CharSequence items[] = {"當溫度大於30度時，開啟電風扇"};
    CharSequence urlName[] = {"relay2"};
    int image[] = {R.drawable.ic_fan};

    IFTTT iftttTemperatureFan = null;
    private Intent itIftttTemperatureFan = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_ifttt, container, false);
        act = (MainActivity) getActivity();

        initIFTTT();
        setListView();

        return v;
    }

    private void initIFTTT() {
        iftttTemperatureFan = new IftttTemperatureFan();
        itIftttTemperatureFan = new Intent(act, IftttTemperatureFan.class);
    }

    private void setListView() {
        listView = (ListView) v.findViewById(R.id.listView2);
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
                View listInView = act.getLayoutInflater().inflate(R.layout.listview_ifttt, parent, false);

                TextView item = (TextView) listInView.findViewById(R.id.textView6);
                item.setText(items[position]);
                item.setCompoundDrawablesWithIntrinsicBounds(image[position], 0, 0, 0);

                final ToggleButton toggleButton =(ToggleButton) listInView.findViewById(R.id.toggleButton);
                toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            act.startService(itIftttTemperatureFan);
                        }
                        else{
                            act.stopService(itIftttTemperatureFan);
                        }
                    }
                });

                return listInView;
            }
        });
    }

}
