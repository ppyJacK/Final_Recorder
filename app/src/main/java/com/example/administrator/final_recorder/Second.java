package com.example.administrator.final_recorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Second extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Button mLogOutBtn;
    TextView mEmailText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mLogOutBtn = findViewById(R.id.LogOutBtn);
        mEmailText = findViewById(R.id.emailText);


        preferences = getSharedPreferences("emailPreferences",Context.MODE_PRIVATE);
        editor = preferences.edit();

        Toolbar toolbal = findViewById(R.id.toolbar);
        setSupportActionBar(toolbal);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Setting");
            actionBar.setDisplayHomeAsUpEnabled(true);      //设置左上角的返回小箭头
            actionBar.setDisplayShowHomeEnabled(true);
        }
        String Semail = preferences.getString("email",null);
        mEmailText.setText("Your Account:"+Semail);

    }

    public void LogOut(){
        Intent i = new Intent(this,SettingActivity.class);
        startActivity(i);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
