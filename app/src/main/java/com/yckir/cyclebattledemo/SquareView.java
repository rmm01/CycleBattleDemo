package com.yckir.cyclebattledemo;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * A square ImageView with identical width and height
 */
public class SquareView extends ImageView{


    public SquareView(Context context) {
        super(context);
    }

    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int l = Math.min(getMeasuredHeight(),getMeasuredWidth());
        setMeasuredDimension(l,l);
    }
}
