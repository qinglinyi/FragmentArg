package com.qinglinyi.mylibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * @author qinglinyi
 * @since 1.0.0
 */
public class LibActivity extends AppCompatActivity {

    public LibFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = FragmentBuilder.builder(new LibFragment()).isLib(true).build();
    }
}
