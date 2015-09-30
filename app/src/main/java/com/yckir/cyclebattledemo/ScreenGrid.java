package com.yckir.cyclebattledemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ScreenGrid {
    public static final String TAG = "SCREEN_GRID";

    private Grid mGrid;

    //length of a square tile on the screen
    private int mScreenTileLength;

    //the width and height that the screenGrid is allocated
    private int mWidth;
    private int mHeight;

    //the height and width of the screenGrid uses
    // always <= mWidth/mHeight
    private int mScreenGridWidth;
    private int mScreenGridHeight;

    //the bitmap where the grid,cycles, and paths are drawn,
    private Bitmap mBitmap;
    private Paint mGridLinePaint;

    public ScreenGrid(Grid grid,int width,int height) {
        mGridLinePaint =new Paint();
        mGridLinePaint.setColor(Color.BLUE);

        mGrid=grid;
        mWidth=width;
        mHeight=height;
        fitGridInScreen();
        makeScreen();
    }

    private void fitGridInScreen(){
        int numTilesX=mGrid.getNumTilesX();
        int numTilesY=mGrid.getNumTilesY();

        //since the screen is always in portrait mode, the largest possible grid that will
        //fit on the screen can be achieved by making the height of the grid as large as possible

        //it is possible that height1 is too big, so we check it with the actual screen height
        double height1 =  (mWidth * numTilesY) / (double) numTilesX;
        double height=Math.min( height1, mHeight);

        mScreenTileLength = (int)(height / numTilesY);

        mScreenGridWidth = mScreenTileLength * numTilesX;
        mScreenGridHeight= mScreenTileLength * numTilesY;
    }

    private void makeScreen(){
        mBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);

        int numTilesX=mGrid.getNumTilesX();
        int numTilesY=mGrid.getNumTilesY();

        int paddingX=mWidth-mScreenGridWidth;
        int paddingY=mHeight-mScreenGridHeight;


        //draw vertical lines
        int offset=paddingX/2;
        int top=paddingY/2;
        int bottom = mScreenGridHeight+top - 1;

        for (int tile = 0; tile < numTilesX; tile++) {
            mCanvas.drawLine(offset, top, offset,bottom, mGridLinePaint);
            offset += mScreenTileLength;
            mCanvas.drawLine(offset - 1, top, offset - 1,bottom, mGridLinePaint);
        }


        //draw horizontal lines
        offset=paddingY/2;
        int left=paddingX/2;
        int right = mScreenGridWidth+left - 1;

        for (int tile = 0; tile < numTilesY; tile++) {
            mCanvas.drawLine(left, offset, right, offset, mGridLinePaint);
            offset += mScreenTileLength;
            mCanvas.drawLine(left, offset - 1, right, offset - 1, mGridLinePaint);
        }

    }

    public void setGrid(Grid grid){
        mGrid=grid;
        fitGridInScreen();
        makeScreen();
    }

    public void setScreenSize(int width,int height){
        mWidth=width;
        mHeight=height;
        fitGridInScreen();
        makeScreen();
    }

    public Bitmap getScreen(){
        return mBitmap;
    }

    @Override
    public String toString() {
        String description =TAG;
        description+="\n\twidth: "+mWidth+", height: "+mHeight;
        description+="\n\tscreenWidth: "+mScreenGridWidth+", screenHeight: "+mScreenGridHeight;
        description+="\n\ttileLength: "+mScreenTileLength;
        description+="\n\t"+Grid.TAG;
        description+="\n\t\tnumTilesX: "+mGrid.getNumTilesX()+", numTilesY"+mGrid.getNumTilesY();
        return description;
    }
}
