package com.example.minge.hlc_smarthome;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentLock extends Fragment {
    private View v;
    private MainActivity act;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_lock, container, false);
        act = (MainActivity)getActivity();

        return v;
    }

    @Override
    public void onDestroy() {
        Log.i("FragmentLock", "onDestroy");
        super.onDestroy();
    }

}
