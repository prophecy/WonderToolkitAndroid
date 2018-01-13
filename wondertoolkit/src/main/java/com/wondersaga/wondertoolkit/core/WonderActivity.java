package com.wondersaga.wondertoolkit.core;

import android.arch.lifecycle.LifecycleActivity;
import android.os.Bundle;

import com.wondersaga.wondertoolkit.core.WonderViewModel;

/**
 * Created by Prophecy (Adawat Chanchua) on 10/6/2017 AD.
 */

public abstract class WonderActivity extends LifecycleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getViewModel().setLifecycleOwnder(this);

        getViewModel().setApplicationContext(getApplicationContext());
        getViewModel().setContext(this);
    }

    abstract protected WonderViewModel getViewModel();
}

