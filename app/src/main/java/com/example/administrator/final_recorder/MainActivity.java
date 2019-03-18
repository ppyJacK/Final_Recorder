package com.example.administrator.final_recorder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.example.administrator.final_recorder.fragments.FileFragment;
import com.example.administrator.final_recorder.fragments.RecordFragment;
import com.example.administrator.final_recorder.fragments.SettingFragment;


public class MainActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager paper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if(toolbar != null) setSupportActionBar(toolbar);
        paper = findViewById(R.id.pager);
        paper.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tabs = findViewById(R.id.tabs);

        //设置tab的属性（在.xml文件中设置不了，sdk版本问题，只能在这设置)
        tabs.setShouldExpand(true);
        tabs.setViewPager(paper);
        tabs.setIndicatorColor(0xffffffff);
        tabs.setIndicatorHeight(4);
        tabs.setTextColor(0xffffffff);
        tabs.setTextSize(42);
        if(toolbar != null){
            setSupportActionBar(toolbar);
        }
    }

    public class MyAdapter extends FragmentPagerAdapter {
        private String[] titles = { "Record",
                "Saved Recordings" };

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {         // 设置导航栏
            switch(position){
                case 0:{
                    return RecordFragment.newInstance(position);
                }
                case 1:{
                    return FileFragment.newInstance(position);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent i = new Intent(this,SettingActivity.class);
                startActivity(i);
                //SettingFragment.newInstance(R.id.action_settings);
                return true;
                default: return super.onOptionsItemSelected(item);
        }
    }

    public MainActivity(){

    }
}
