package com.example.administrator.final_recorder.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.administrator.final_recorder.R;
import com.example.administrator.final_recorder.SettingActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    SharedPreferences preferences = null;
    SharedPreferences.Editor editor = null;


    FloatingActionButton mSaveBtn;
    EditText mEmailText;

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

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mSaveBtn = view.findViewById(R.id.SaveEmailBtn);
        mEmailText = view.findViewById(R.id.emailText);


        //use sharePreferences to store email address
        preferences = getActivity().getSharedPreferences("emailPreferences",Context.MODE_PRIVATE);
        editor = preferences.edit();


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_email = mEmailText.getText().toString();
                editor.putString("email",s_email);
                editor.commit();
            }
        });


        return view;
    }



}
