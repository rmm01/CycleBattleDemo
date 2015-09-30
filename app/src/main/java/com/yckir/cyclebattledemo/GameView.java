package com.yckir.cyclebattledemo;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class GameView extends View {

    public static final String TAG = "GAME_VIEW";
    //private DrawingThread mDrawingThread;
    private Bitmap mBitmap;
    private ScreenGrid mScreenGrid;
    private Grid mGrid;
    private int mGridPaddingX;
    private int mGridPaddingY;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.v(TAG, "initializing game view");

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GameView, 0, 0);

        int length1 = a.getInt(R.styleable.GameView_grid_long_length, 6);
        int length2 = a.getInt(R.styleable.GameView_grid_short_length, 6);

        a.recycle();

        setGridDimensions(length1, length2);
        mScreenGrid= new ScreenGrid(mGrid, 100,100);

    }

    /*
     *       create a padding between the edge of the view and the grid
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        Log.v(TAG,"w="+width+", h="+height);

        mGridPaddingX=width/10;
        mGridPaddingY=height/10;

        mScreenGrid.setScreenSize(width,height);

        mBitmap=mScreenGrid.getScreen();

        //since we didn't alter the width and height, we can call the super instead of setDimension
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.v(TAG, "OnDraw:");
        canvas.drawBitmap(mBitmap,mGridPaddingX/2,mGridPaddingY/2,null);
        super.onDraw(canvas);
    }

    public void setGridDimensions(int shortLength, int longLength){
        //corrects the user if they did not specify parameters properly
        if(shortLength>longLength){
            Log.d(TAG,"invalid parameters, setGridDimensions("+shortLength+", "+longLength+"), swapping the values");
            int temp = longLength;
            longLength=shortLength;
            shortLength=temp;
        }
        mGrid = new Grid(shortLength,longLength);
    }

    public static String getThreadSignature() {
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();
        long p = t.getPriority();
        String gName = t.getThreadGroup().getName();
        return ("Thread Info: "+name + ":(id)" + l + ":(priority)" + p
                + ":(group)" + gName);
    }

    public void updateBitmap(Bitmap b){
        mBitmap=b;
        invalidate();
    }

}
