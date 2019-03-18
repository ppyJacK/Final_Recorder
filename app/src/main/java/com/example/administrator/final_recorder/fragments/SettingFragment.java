package com.example.administrator.final_recorder.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.final_recorder.R;
import com.example.administrator.final_recorder.SettingActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    public static SettingFragment newInstance(int id){
        SettingFragment f = new SettingFragment();
        Bundle b = new Bundle();
        b.putInt("Item",id);
        f.setArguments(b);
        return f;
    }

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }



}
