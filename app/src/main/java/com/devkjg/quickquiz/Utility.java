package com.devkjg.quickquiz;

import android.view.View;
import android.view.ViewTreeObserver;
import androidx.gridlayout.widget.GridLayout;
import org.jetbrains.annotations.NotNull;

public class Utility {

    public static void autoScaleLayoutChildren(@NotNull GridLayout layout) {

        runJustBeforeBeingDrawn(layout, new Runnable() {
            @Override
            public void run() {

                int[] dim = {0,0};
                GridLayout.LayoutParams lp_child = (GridLayout.LayoutParams) layout.getChildAt(0).getLayoutParams();

                dim[0] = layout.getWidth()-layout.getPaddingLeft()-layout.getPaddingRight();
                dim[0] = (dim[0]/layout.getColumnCount())-lp_child.leftMargin-lp_child.rightMargin;

                dim[1] = layout.getHeight()-layout.getPaddingTop()-layout.getPaddingBottom();
                dim[1] = (dim[1]/layout.getRowCount())-lp_child.topMargin-lp_child.bottomMargin;

                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    child.setMinimumWidth(dim[0]);
                    child.setMinimumHeight(dim[1]);
                }

            }
        });
    }

    public static void runJustBeforeBeingDrawn(final View view, final Runnable runnable) {
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                runnable.run();
                return true;
            }
        };
        view.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

}
