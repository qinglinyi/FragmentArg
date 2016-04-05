package com.qinglinyi.demo;

import com.qinglinyi.compiler.api.Arg;
import com.qinglinyi.compiler.api.UseArg;

import java.util.ArrayList;

/**
 * @author qinglinyi
 * @since 1.0.0
 */
@UseArg
public class AFragment extends MyFragment {

    private int age = 5;

    @Arg
    String [] ary;

    @Arg
    ArrayList<Boolean> isCheckList;
}
