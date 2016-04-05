package com.qinglinyi.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qinglinyi.compiler.api.Arg;
import com.qinglinyi.compiler.api.ArgInjector;
import com.qinglinyi.compiler.api.UseArg;

import java.util.ArrayList;

/**
 * @author qinglinyi
 * @since 1.0.0
 */

@UseArg
public class MyFragment extends Fragment {

    @Arg
    public String title;

    @Arg
    ArrayList<Integer> ages;

    @Arg
    ArrayList<User> user;

    @Arg
    int age = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArgInjector.inject(this);

        Toast.makeText(getActivity(), user.get(0).name, Toast.LENGTH_LONG).show();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
