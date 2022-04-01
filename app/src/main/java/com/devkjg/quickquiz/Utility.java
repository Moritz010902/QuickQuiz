package com.devkjg.quickquiz;

import android.view.View;
import android.view.ViewTreeObserver;
import androidx.gridlayout.widget.GridLayout;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

    //TODO: remove method
    public static int[] getMinCompCountInArea(View availableArea, ArrayList<View> views) {
        int columns= 1;
        int rows = 1;

        if(views.size() == 0)
            return new int[] {columns, rows};

        int count = 0;
        int dim = 0;
        int maxWidth = availableArea.getWidth();
        for (View v : views) {
            dim += v.getWidth();
            if(dim > maxWidth) {
                if (columns > count)
                    columns = count;
                count = 0;
                dim = 0;
            } else {
                count++;
            }
        }

        count = 0;
        dim = 0;
        int maxHeight = availableArea.getHeight();
        for (View v : views) {
            dim += v.getHeight();
            if(dim > maxHeight) {
                if (rows > count)
                    rows = count;
                count = 0;
                dim = 0;
            } else {
                count++;
            }
        }

        return new int[] {columns, rows};
    }

    public static boolean contains(Object obj, ArrayList list) {
        for (Object o : list) {
            if (o == obj)
                return true;
        }
        return false;
    }

}
