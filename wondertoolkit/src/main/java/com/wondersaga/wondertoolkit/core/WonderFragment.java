package com.wondersaga.wondertoolkit.core;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;

/**
 * Created by Prophecy (Adawat Chanchua) on 10/6/2017 AD.
 */

public abstract class WonderFragment extends LifecycleFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getViewModel().setLifecycleOwnder(this);

        getViewModel().setApplicationContext(getActivity().getApplicationContext());
        getViewModel().setContext(getActivity());
    }

    abstract protected WonderViewModel getViewModel();
}

