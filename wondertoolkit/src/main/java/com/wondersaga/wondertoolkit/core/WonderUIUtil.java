package com.wondersaga.wondertoolkit.core;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by ProphecyX on 9/3/2017 AD.
 */

public class WonderUIUtil {

    // region Singleton

    private static WonderUIUtil instance = null;

    public static WonderUIUtil getInstance() {

        if (instance == null)
            instance = new WonderUIUtil();

        return instance;
    }

    // endregion

    // region UI system sugar functions

    public interface ViewTreeListener {

        void onViewAddedOnViewTree();
    }

    public void waitForViewAndChildrenAddedOnViewTree(final LinearLayout container, final ViewTreeListener viewTreeListener) {

        final ArrayList<View> viewList = new ArrayList<>();

        viewList.add(container);

        waitForViewListAddedOnViewTree(viewList, viewTreeListener);
    }

    public void waitForViewListAddedOnViewTree(final ArrayList<View> viewList, final ViewTreeListener viewTreeListener) {

        class Counter {
            public int counter;
        }

        final Counter c = new Counter();
        c.counter = 0;

        for (int i=0; i<viewList.size(); ++i) {

            waitForViewAddedOnViewTree(viewList.get(i), new ViewTreeListener() {
                @Override
                public void onViewAddedOnViewTree() {

                    ++c.counter;

                    if (c.counter >= viewList.size())
                        viewTreeListener.onViewAddedOnViewTree();
                }
            });
        }
    }

    public void waitForViewAddedOnViewTree(final View view, final ViewTreeListener viewTreeListener) {

        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            view.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }

                        viewTreeListener.onViewAddedOnViewTree();
                    }
                });
    }

    public void runOnUIThread(Context context, Runnable runnable) {

        Handler handler = new Handler(context.getMainLooper());
        handler.post(runnable);
    }

    // endregion

    // region UI util sugar functions

    public void showError(View rootView, String title, String message, final WonderEvent event) {

        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                event.dispatch("");
            }
        });
        snackbar.show();
    }

    // endregion
}
