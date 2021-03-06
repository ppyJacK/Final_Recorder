package com.example.administrator.final_recorder.fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.final_recorder.R;
import com.example.administrator.final_recorder.service.RecordService;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private int position;

    //Recording tools
    private FloatingActionButton mRecordbtn = null;

    private TextView mRecording_status;
    private FloatingActionButton mBtnPause = null;

    private boolean if_record = true;
    private boolean if_pause = true;

    private Chronometer mChronometer = null;
    private int mRecordPromptCount = 0;
    long timeWhenPaused = 0;

    public static final int RECORD_AUDIO = 0;
    private boolean isNeedCheck = true;

    //(not)granted permission
    private Map<Integer,Runnable> allowablePermission = new HashMap<>();
    private Map<Integer,Runnable> disallowablePermission = new HashMap<>();

    // the permissions we need
    String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    // the permissions which aren't granted
    List<String> mPermissionList = new ArrayList<>();

    private final int mRequestCode = 100;//permission code

    private boolean permission_sdcard = false;
    private boolean permission_record = false;

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
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        position = getArguments().getInt(ARG_POSITION);
        initPermission();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View recordView =  inflater.inflate(R.layout.fragment_record, container, false);

        mChronometer = recordView.findViewById(R.id.chronometer);
        mRecording_status = recordView.findViewById(R.id.recording_status);

        mBtnPause = recordView.findViewById(R.id.btnPause);
        mRecordbtn = recordView.findViewById(R.id.recordbtn);
        mRecordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(2, Manifest.permission.WRITE_EXTERNAL_STORAGE, new Thread( new Runnable() {
                    @Override
                    public void run() {
                        permission_sdcard = true;
                    }
                }), new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }));

                requestPermission(1, Manifest.permission.RECORD_AUDIO, new Thread(new Runnable() {
                    @Override
                    public void run() {
                        permission_record = true;
                        onRecord(if_record);
                        if_record = !if_record;
                        //Log.d("click","click button");
                    }
                }), new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }));

            }
        });

        mBtnPause.hide();
        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause(if_pause);
                if_pause = !if_pause;
            }
        });

        return recordView;
    }

    private void onRecord(boolean start){
        //initiate recording service intent
        Intent intent = new Intent(getActivity(),RecordService.class);

        //Log.d("recording","start recording:"+String.valueOf(start)+String.valueOf(permission_record)+String.valueOf(permission_sdcard));
        if(start && permission_record && permission_sdcard){
            mRecordbtn.setImageResource(R.drawable.ic_finish_button);
            //Toast.makeText(getActivity(),"Recording start...",Toast.LENGTH_LONG).show();
            File folder = new File(Environment.getExternalStorageDirectory()+"/Final_recorder");
            if(!folder.exists()){
                folder.mkdir();
            }
            Log.d("onrecord","true");
            //start clocking
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if(mRecordPromptCount == 0){
                        mRecording_status.setText("Recording.");
                    }else if (mRecordPromptCount == 1){
                        mRecording_status.setText("Recording..");
                    }else if (mRecordPromptCount == 2){
                        mRecording_status.setText("Recording...");
                        mRecordPromptCount = -1;
                    }
                    mRecordPromptCount ++;
                }
            });
            mBtnPause.setImageResource(R.drawable.ic_pause_button);
            if_pause = true;
            mBtnPause.show();

            // start recording service
            getActivity().startService(intent);
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mRecording_status.setText("Recording.");
            mRecordPromptCount++;
        }
        else{
            if(permission_sdcard){
                //stop recording
                mRecordbtn.setImageResource(R.drawable.ic_rec_button);
                mChronometer.stop();
                mChronometer.setBase(SystemClock.elapsedRealtime());
                timeWhenPaused = 0;
                mRecording_status.setText("Click the button to start");
                mBtnPause.hide();
                getActivity().stopService(intent);
                //allow the screen to turn off again once recording is finished
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }


    private void onPause(boolean pause){
        Log.d("topause",String.valueOf(pause));
        if(pause){
            //pause recording
            //Log.d("ifset","not yet");
            mBtnPause.setImageResource(R.drawable.ic_resume_button);
            //must hide and show again,or it will miss the image
            mBtnPause.hide();
            mBtnPause.show();
            //Log.d("ifset","set");
            timeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
            mChronometer.stop();
        }else{
            //resume recording
            mBtnPause.setImageResource(R.drawable.ic_pause_button);
            mBtnPause.hide();
            mBtnPause.show();
            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            mChronometer.start();
        }
    }

    protected void requestPermission(int id,String permission,Thread allow,Thread disallow){
        if(allow == null) throw new IllegalArgumentException("allow == null");
        allowablePermission.put(id,allow);
        if(disallow != null)disallowablePermission.put(id,disallow);

        //check the targetsdk version
        if(Build.VERSION.SDK_INT >= 23){
            int checkPermission = ContextCompat.checkSelfPermission(getContext(),permission);
            if(checkPermission != PackageManager.PERMISSION_GRANTED){
                // create a dialog to grant the permission
                ActivityCompat.requestPermissions(getActivity(),new String[]{permission},id);
                return;
            }else{
                allow.run();

            }
        }else {

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantRes){
        super.onRequestPermissionsResult(requestCode,permissions,grantRes);
        if(grantRes[0] == PackageManager.PERMISSION_GRANTED){
            Runnable allowRun = allowablePermission.get(requestCode);
            allowRun.run();
        }else{
            Runnable disallowRun = disallowablePermission.get(requestCode);
            disallowRun.run();
        }
    }

    //权限判断和申请
    private void initPermission() {

        mPermissionList.clear();//clear the permissions which aren't granted

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(getContext(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        if (mPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(getActivity(), permissions, mRequestCode);
        }else{

        }
    }

}

