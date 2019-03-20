package com.example.administrator.final_recorder.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.final_recorder.R;
import com.example.administrator.final_recorder.RecordingItem;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayBackFragment extends DialogFragment {

    private static final String LOG_TAG = "PlaybackFragment";

    private static final String ARG_ITEM = "recording_item";
    private RecordingItem item;

    private Handler mHandler = new Handler();
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    private SeekBar mSeekBar = null;
    private FloatingActionButton mPlayBtn = null;
    private TextView mCurProgress = null;
    private TextView mFileName = null;
    private TextView mFileLength = null;

    private boolean if_playing = false;


    long minutes=0;
    long seconds=0;

    public PlayBackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        item = getArguments().getParcelable(ARG_ITEM);

        long itemDuration = item.getLength();
        minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(itemDuration);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance){
        super.onActivityCreated(savedInstance);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        Dialog dialog = super.onCreateDialog(savedInstance);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_play_back,null);

        mFileName = view.findViewById(R.id.file_name);
        mFileLength = view.findViewById(R.id.file_length);
        mCurProgress = view.findViewById(R.id.current_progress_text_view);

        mSeekBar = view.findViewById(R.id.seekbar);
        ColorFilter filter = new LightingColorFilter(getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorPrimary));
        mSeekBar.getProgressDrawable().setColorFilter(filter);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mMediaPlayer != null && fromUser){
                    mMediaPlayer.seekTo(progress);
                    mHandler.removeCallbacks(mRunnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                                    - TimeUnit.MINUTES.toSeconds(mMediaPlayer.getCurrentPosition());
                    mCurProgress.setText(String.format("%02d:%02d",minutes,seconds));

                    updateSeekBar();
                }else if(mMediaPlayer == null && fromUser){
                    prepareMediaFromPoint(progress);
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer != null){
                    // remove message Handler from updating progress bar
                    mHandler.removeCallbacks(mRunnable);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer != null){
                    mHandler.removeCallbacks(mRunnable);
                    mMediaPlayer.seekTo(seekBar.getProgress());

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    mCurProgress.setText(String.format("%02d:02d",minutes,seconds));
                    updateSeekBar();
                }

            }
        });

        mPlayBtn = view.findViewById(R.id.media_play_btn);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(if_playing);
                if_playing = !if_playing;
            }
        });

        mFileName.setText(item.getName());
        mFileLength.setText(String.format("%02d:02d",minutes,seconds));

        builder.setView(view);

        // window with no title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return builder.create();
    }

    @Override
    public void onStart(){
        super.onStart();

        //set background
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        //disable button from dialog
        AlertDialog alertDialog = (AlertDialog)getDialog();
        alertDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
        alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mMediaPlayer != null)
            stopPlaying();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(mMediaPlayer != null)
            stopPlaying();
    }

    public void onPlay(boolean isPlaying){
        if(!isPlaying){
            if(mMediaPlayer == null)
                startPlaying();
        }
    }

    private void startPlaying(){
        mPlayBtn.setImageResource(R.drawable.ic_pause_button);
        mMediaPlayer = new MediaPlayer();

        try{
            mMediaPlayer.setDataSource(item.getFilePath());
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });

        } catch (IOException e)
        {
            Log.e(LOG_TAG,"prepare failed");
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        updateSeekBar();

        //keep screen on while playing audio
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_back, container, false);
    }

    public PlayBackFragment newInstance(RecordingItem item){
        PlayBackFragment f = new PlayBackFragment();
        Bundle b = new Bundle();
        b.putParcelable(ARG_ITEM,item);
        f.setArguments(b);
        return f;
    }

    private void prepareMediaFromPoint(int progress){
        //set mediaPlayer to start from middle of the file
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(item.getFilePath());
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(progress);

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });

        }catch (IOException e){
            Log.e(LOG_TAG,"prepare() failed");
        }
        //keep screen on while playing audio
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void stopPlaying(){
        mPlayBtn.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.pause();
    }

    private void resumePlaying(){
        mPlayBtn.setImageResource(R.drawable.ic_pause_button);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        updateSeekBar();
    }

    private void finishPlaying(){
        mPlayBtn.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

        mSeekBar.setMax(mSeekBar.getMax());
        if_playing = !if_playing;

        mCurProgress.setText(mFileLength.getText());
        mSeekBar.setProgress(mSeekBar.getMax());

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //update seekbar
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int mCurPos = mMediaPlayer.getCurrentPosition();
            mSeekBar.setProgress(mCurPos);

            long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurPos);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurPos) - TimeUnit.MINUTES.toSeconds(minutes);

            mCurProgress.setText(String.format("%02d:%02d",minutes,seconds));

            updateSeekBar();
        }
    };

    private void updateSeekBar(){
        mHandler.postDelayed(mRunnable,1000);
    }
}
