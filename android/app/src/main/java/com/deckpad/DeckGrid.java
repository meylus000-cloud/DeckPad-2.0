package com.deckpad;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Lays out the buttons. Each child carries its cell index as an Integer tag;
 * a child without a tag fills the whole area (used for the hint text).
 *
 * fit    - cells stretch to fill the whole screen
 * square - 1:1 buttons of medium size with a small gap, centered on the screen
 */
public class DeckGrid extends ViewGroup {
    private static final int SQUARE_MAX_DP = 120;
    private static final int SQUARE_GAP_DP = 10;
    private static final int FIT_PAD_DP = 6;
    private static final int FIT_INSET_DP = 5;

    private final float dp;
    private int cols = 1;
    private int rows = 1;
    private boolean square;

    public DeckGrid(Context context, float dp) {
        super(context);
        this.dp = dp;
    }

    public void setSpec(int cols, int rows, boolean square) {
        this.cols = Math.max(1, cols);
        this.rows = Math.max(1, rows);
        this.square = square;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        setMeasuredDimension(getDefaultSize(0, widthSpec), getDefaultSize(0, heightSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = r - l;
        int h = b - t;

        int x0 = 0, y0 = 0, stepX = 0, stepY = 0, cw = 0, ch = 0;
        if (square) {
            int gap = (int) (SQUARE_GAP_DP * dp);
            int fitW = (w - gap * (cols - 1)) / cols;
            int fitH = (h - gap * (rows - 1)) / rows;
            int s = Math.max(1, Math.min((int) (SQUARE_MAX_DP * dp), Math.min(fitW, fitH)));
            cw = s;
            ch = s;
            stepX = s + gap;
            stepY = s + gap;
            x0 = (w - (cols * s + (cols - 1) * gap)) / 2;
            y0 = (h - (rows * s + (rows - 1) * gap)) / 2;
        } else {
            int pad = (int) (FIT_PAD_DP * dp);
            int inset = (int) (FIT_INSET_DP * dp);
            int cellW = (w - 2 * pad) / cols;
            int cellH = (h - 2 * pad) / rows;
            cw = Math.max(1, cellW - 2 * inset);
            ch = Math.max(1, cellH - 2 * inset);
            stepX = cellW;
            stepY = cellH;
            x0 = pad + inset;
            y0 = pad + inset;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            Object tag = v.getTag();
            int x, y, vw, vh;
            if (tag instanceof Integer) {
                int idx = (Integer) tag;
                int c = idx % cols;
                int rr = idx / cols;
                x = x0 + c * stepX;
                y = y0 + rr * stepY;
                vw = cw;
                vh = ch;
            } else {
                x = 0;
                y = 0;
                vw = w;
                vh = h;
            }
            v.measure(MeasureSpec.makeMeasureSpec(vw, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(vh, MeasureSpec.EXACTLY));
            v.layout(x, y, x + vw, y + vh);
        }
    }
}
