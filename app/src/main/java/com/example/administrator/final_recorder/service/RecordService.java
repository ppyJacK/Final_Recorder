package com.example.administrator.final_recorder.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.example.administrator.final_recorder.R;
import com.example.administrator.final_recorder.DBHelper;
import com.example.administrator.final_recorder.MainActivity;
import com.example.administrator.final_recorder.MySharedPreferences;

public class RecordService extends Service {
    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mediaRecorder = null;

    private DBHelper dbHelper;

    private long mStartingTimeMill = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer timer = null;
    private TimerTask timerTask = null;

    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        dbHelper = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        startRecording();
        return START_STICKY;
    }

    public void onDestroy(){
        if(mediaRecorder != null)
            stopRecording();
        super.onDestroy();
    }

    public void startRecording(){
        setFileNameAndPath();

        //setting of MediaRecorder
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(mFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        if(MySharedPreferences.getPrefHighQuality(this)){
            //set the sharpness
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setVideoEncodingBitRate(192000);
        }

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            mStartingTimeMill = System.currentTimeMillis();
        }catch (IOException e){
            Log.e(LOG_TAG,"prepare failed");
        }

    }

    public void stopRecording(){
        mediaRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMill);  //real time = current time - last current time
        mediaRecorder.release();
        Toast.makeText(this,"Audio saved to "+mFilePath,Toast.LENGTH_LONG).show();

        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }

        mediaRecorder = null;

        try{
            dbHelper.addRecording(mFileName,mFilePath,mElapsedMillis);
        }catch (Exception e){
            Log.e(LOG_TAG,"exception",e);
        }
    }

    public void setFileNameAndPath(){
        int count = 0;
        File f;
        do{
            count++;
            mFileName = "My Recording"+"_"+(dbHelper.getCount()+count)+".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/final_recorder/"+mFileName;
            f = new File(mFilePath);
        }while (f.exists() && !f.isDirectory());
    }

    private void startTimer(){
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mElapsedSeconds++;
                if(onTimerChangedListener != null)
                    onTimerChangedListener.onTimerChanged(mElapsedSeconds);
                NotificationManager ntfmgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                ntfmgr.notify(1,createNotification());
            }
        };
        timer.scheduleAtFixedRate(timerTask,1000,1000);
    }

    private Notification createNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(),"chanel_id")
                .setSmallIcon(R.drawable.ic_mic_white_36dp)
                .setContentTitle("Recording...")
                .setContentText(mTimerFormat.format(mElapsedSeconds*1000))
                .setOngoing(true);

        builder.setContentIntent(PendingIntent.getActivities(getApplicationContext(),0,
        new Intent[]{new Intent(getApplicationContext(),MainActivity.class)},0));

        return builder.build();
    }
}
