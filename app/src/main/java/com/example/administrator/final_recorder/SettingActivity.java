package com.example.administrator.final_recorder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    FloatingActionButton mSaveBtn;
    FloatingActionButton mLogOutBtn;

    TextView mAccountText;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        preferences = getSharedPreferences("emailPreferences",Context.MODE_PRIVATE);
        editor = preferences.edit();
        final EditText editText = findViewById(R.id.text_email);
        mSaveBtn = findViewById(R.id.SaveEmailBtn);
        mLogOutBtn = findViewById(R.id.LogOutBtn);
        mAccountText = findViewById(R.id.AccountText);

        Toolbar toolbal = findViewById(R.id.toolbar);
        setSupportActionBar(toolbal);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Setting");
            actionBar.setDisplayHomeAsUpEnabled(true);      //设置左上角的返回小箭头
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if(preferences.getString("email",null) != null){
            mAccountText.setTextColor(Color.GRAY);
            mAccountText.setText("Now your account is:"+preferences.getString("email",null)+"\n"+"Click 'x' button to logout");
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setTextColor(Color.GRAY);
        }


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editText.getText().toString();
                editor.putString("email",email);
                editor.apply();
                Toast.makeText(SettingActivity.this,"Email set succeed",Toast.LENGTH_LONG).show();
                mAccountText.setTextColor(Color.GRAY);
                mAccountText.setText("Now your account is:"+email+"\n"+"Click 'x' button to logout");
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
                editText.setTextColor(Color.GRAY);
            }
        });

        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccountText.setText("");
                editText.setTextColor(Color.BLACK);
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
                editText.requestFocus();
            }
        });

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
