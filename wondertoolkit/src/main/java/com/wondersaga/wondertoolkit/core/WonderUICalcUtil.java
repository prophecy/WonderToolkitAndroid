package com.wondersaga.wondertoolkit.core;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class WonderUICalcUtil {

    /***
        Keep all children aligned horizontally, all children views' aspect ratio are reserved.
     ***/
    public void fitWidthKeepAspectRatio(Activity activity, LinearLayout container) {

        // Vars
        int count = container.getChildCount();
        float rSum = 0f;

        // Get view ratio sum (Making h = 1 in the space)
        for (int i=0; i<count; ++i) {

            View child = container.getChildAt(i);
            int cw = child.getWidth();
            int ch = child.getHeight();

            float r = (float)cw / (float)ch;
            rSum += r;
        }

        // Get screen width
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point sz = new Point();
        display.getSize(sz);
        int screenW = sz.x;

        // Calculate h from the first child
        if (count <= 0)
            return;

        // Get agent view
        View av = container.getChildAt(0);
        float r = (float)av.getWidth() / (float)av.getHeight();
        float rNorm = r / rSum;

        // Get agent view width
        float avW = rNorm * screenW;

        // Get agent view height
        float avH = avW / r;

        // Apply height to the container
        ViewGroup.LayoutParams params = container.getLayoutParams();
        params.height = (int)avH;
        container.setLayoutParams(params);
    }
}
