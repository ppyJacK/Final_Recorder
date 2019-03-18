package com.example.administrator.final_recorder.fragments;


import android.content.Intent;
import android.media.tv.TvInputService;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.final_recorder.R;
import com.example.administrator.final_recorder.service.RecordService;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    //录音部件
    private FloatingActionButton mRecordbtn = null;

    private boolean if_record = true;

    public static RecordFragment newInstance(int position){
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION,position);
        f.setArguments(b);
        return f;
    }

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View recordView =  inflater.inflate(R.layout.fragment_record, container, false);

        mRecordbtn = recordView.findViewById(R.id.recordbtn);
        mRecordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(if_record);
                if_record =! if_record;
            }
        });

        return recordView;
    }

    private void onRecord(boolean start){
        Intent intent = new Intent(getActivity(),RecordService.class);
        if(start){
            mRecordbtn.setImageResource(R.drawable.ic_pause_button);
            Toast.makeText(getActivity(),"Recording start...",Toast.LENGTH_LONG).show();
            File folder = new File(Environment.getExternalStorageDirectory()+"/final_recorder");
            if(!folder.exists()){
                folder.mkdir();
            }

        }
    }

}

