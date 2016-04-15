package com.qinglinyi.arg.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class MyActivity extends AppCompatActivity {

    private MyFragment mFragment0, mFragment1;
    private boolean isShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initView();
        initFragment();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shiftFragment();
            }
        });
    }

    private void initFragment() {
        ArrayList<String> list = new ArrayList<>();
        list.add("唱歌");
        list.add("跳舞");

        mFragment0 = FragmentBuilder.builder(new MyFragment())
                .age(15)
                .name("张三")
                .friendNames(new String[]{"李四", "王五"})
                .interests(list)
                .build();

        mFragment1 = new MyFragmentBuilder().age(15)
                .name("李四")
                .friendNames(new String[]{"张三", "王五"})
                .interests(list)
                .build();

        addFragment(mFragment0, mFragment1);
        shiftFragment();
    }

    private void addFragment(Fragment... fragments) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        for (Fragment fragment : fragments) {
            transaction.add(R.id.contentFl, fragment);
        }
        transaction.commit();
    }

    private void shiftFragment() {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (isShow) {
            transaction.hide(mFragment0).show(mFragment1);
        } else {
            transaction.hide(mFragment1).show(mFragment0);
        }
        transaction.commit();
        isShow = !isShow;
    }

}
