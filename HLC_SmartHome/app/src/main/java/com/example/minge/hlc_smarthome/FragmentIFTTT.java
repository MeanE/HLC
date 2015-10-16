package com.example.minge.hlc_smarthome;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentIFTTT extends Fragment {
    private View v;
    private MainActivity act;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_ifttt, container, false);
        act = (MainActivity) getActivity();



        return v;
    }


}
