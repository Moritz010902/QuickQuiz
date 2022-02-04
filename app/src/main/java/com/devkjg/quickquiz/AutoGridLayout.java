package com.devkjg.quickquiz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/* A labor-saving layout
/  dynamically places all children on an equally-spaced grid.
   All children get the width/height of the largest child - so they all look similar
 */
public class AutoGridLayout extends ViewGroup {
//TODO: aktuell nutzlos
    private int mMaxHeight;
    private int mMaxWidth;

    public AutoGridLayout(Context context) {
        super(context);
    }

    public AutoGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoGridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();

        mMaxHeight = 0;
        mMaxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {

            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec,  heightMeasureSpec);

                mMaxWidth = Math.max(mMaxWidth, child.getMeasuredWidth());
                mMaxHeight = Math.max(mMaxHeight, child.getMeasuredHeight());
            }
        }

        mMaxHeight = Math.max(mMaxHeight, getSuggestedMinimumHeight());
        mMaxWidth = Math.max(mMaxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(mMaxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(mMaxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        final float pxWidth = (right - left + 1);
        final float pxHeight  = (1 + bottom - top);
        final int totalChildCount = getChildCount();
        int count = 0;

        for (int i = 0; i < totalChildCount; i++) {
            if (getChildAt(i).getVisibility() != GONE)
                count++;
        }

        final float minSpacing = pxWidth / 20;
        final int maxPossibleColumns = (int) (pxWidth / (mMaxWidth + minSpacing));
        final int rows = (int)Math.ceil((float)count / maxPossibleColumns);
        final int voidsAtLastRow = (rows * maxPossibleColumns - count);
        // distribute the voids to get an even distribution is possible
        final int nColumns = maxPossibleColumns - voidsAtLastRow / rows;

        // equal spaces as margins and between childs (#spaces = #child + 1)
        final int xSpace = (int)Math.max(0,(pxWidth - nColumns * mMaxWidth) / (nColumns + 1));
        final int ySpace = (int)Math.max(0,(pxHeight - rows * mMaxHeight) / (rows + 1));

        int n = 0;
        for (int i = 0; i < totalChildCount; i++) {

            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int col = n % nColumns;
                final int x = xSpace + (xSpace + mMaxWidth) * col;

                final int row = n / nColumns;
                final int y = ySpace + (ySpace + mMaxHeight) * row;

                // Place the child.
                child.layout(x, y,x+mMaxWidth,y+mMaxHeight);
                n++;
            }
        }
    }

}