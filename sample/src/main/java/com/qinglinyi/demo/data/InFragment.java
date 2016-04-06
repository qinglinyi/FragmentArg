package com.qinglinyi.demo.data;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qinglinyi.arg.api.Arg;
import com.qinglinyi.arg.api.ArgInjector;
import com.qinglinyi.arg.api.UseArg;
import com.qinglinyi.demo.MyFragment;

import java.util.Arrays;

/**
 * @author qinglinyi
 * @since 1.0.0
 */
@UseArg
public class InFragment extends MyFragment {

    @Arg
    String[] names;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArgInjector.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getActivity(), Arrays.toString(names), Toast.LENGTH_SHORT).show();
        return super.onCreateView(inflater, container, savedInstanceState);

    }
}
