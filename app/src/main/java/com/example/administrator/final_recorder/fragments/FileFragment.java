package com.example.administrator.final_recorder.fragments;


import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.final_recorder.R;
import com.example.administrator.final_recorder.adapter.FileViewerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = "FileViewerFragment";

    private int position;

    private FileViewerAdapter mFileViewerAdapter;

    public static FileFragment newInstance(int position){
        FileFragment f = new FileFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION,position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        fileObserver.startWatching();
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_file, container, false);
        RecyclerView mItemlist = view.findViewById(R.id.recyclerView);
        mItemlist.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //newest to oldest order
        llm.setReverseLayout(true);    //because files store in database from oldest to newest
        llm.setStackFromEnd(true);      //newest file will always be above the former one

        mItemlist.setLayoutManager(llm);
        mItemlist.setItemAnimator(new DefaultItemAnimator());

        mFileViewerAdapter = new FileViewerAdapter(getActivity(),llm);
        mItemlist.setAdapter(mFileViewerAdapter);

        return view;
    }

    FileObserver fileObserver = new FileObserver(android.os.Environment.getExternalStorageDirectory().toString()+"/Final_recorder") {
        // set up a file observer to watch this directory on sd card
        @Override
        public void onEvent(int event, String file) {
            if(event == FileObserver.DELETE){
                // a file be deleted
                String filePath = android.os.Environment.getExternalStorageDirectory().toString()+"/Final_recorder"+file+"]";

                Log.d(LOG_TAG,"File deleted ["+android.os.Environment.getExternalStorageDirectory().toString()+"/Final_recorder"+file+"]");

                //remove file from database and recycle view
                mFileViewerAdapter.removeOutOfApp(filePath);
            }

        }
    };

}
