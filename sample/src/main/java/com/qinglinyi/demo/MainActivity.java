package com.qinglinyi.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<User> users=new ArrayList<>();
        users.add(new User("Jim"));
        final MyFragment fragment = FragmentBuilder
                        .builder(new MyFragment())
                        .user(users)
                        .build();
//        AFragment fragment = FragmentBuilder.builder(new AFragment())
//                .title("hello")
//                .build();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFl, fragment)
                .commit();

    }
}
