package com.qinglinyi.arg.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qinglinyi.arg.api.Arg;
import com.qinglinyi.arg.api.ArgInjector;
import com.qinglinyi.arg.api.UseArg;

import java.util.ArrayList;
import java.util.Arrays;

@UseArg
public class MyFragment extends Fragment {

    @Arg
    String name;

    @Arg
    int age;

    @Arg
    ArrayList<String> interests;

    @Arg
    String[] friendNames;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArgInjector.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
        TextView mTextView = (TextView) rootView.findViewById(R.id.mTextView);
        mTextView.append(name + "\n");
        mTextView.append(age + "\n");
        mTextView.append(interests + "\n");
        mTextView.append(Arrays.toString(friendNames) + "\n");
        return rootView;
    }
}
