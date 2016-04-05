package com.qinglinyi.mylibrary;

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

/**
 * @author qinglinyi
 * @since 1.0.0
 */

@UseArg
public class LibFragment extends Fragment {

    @Arg
    public boolean isLib;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArgInjector.inject(this);
        Toast.makeText(getActivity(), "" + isLib, Toast.LENGTH_SHORT).show();
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
