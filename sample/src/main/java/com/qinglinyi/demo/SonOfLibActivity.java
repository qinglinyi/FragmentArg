package com.qinglinyi.demo;

import android.os.Bundle;

import com.qinglinyi.mylibrary.LibActivity;

/**
 * @author qinglinyi
 * @since 1.0.0
 */
public class SonOfLibActivity extends LibActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.contentFl, fragment).commit();
    }
}
