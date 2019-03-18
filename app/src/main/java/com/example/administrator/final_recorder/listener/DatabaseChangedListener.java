package com.example.administrator.final_recorder.listener;

public interface DatabaseChangedListener {
    void onNewDatabaseEntryAdded();
    void onDatabaseEntryRenamed();
}
