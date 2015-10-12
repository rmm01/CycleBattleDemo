package com.yckir.cyclebattledemo;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


/**
 *      A two dimensional rectangle that is capable of moving its position, recording its path,
 *      and being drawn.
 *
 *      @author  Ricky Martinez
 *      @version 0.1
 */
public class Cycle extends GridRectangle{
    /**
     * Identifier for debugging.
     */
    public static final String TAG= "Cycle";

    //bitmap of the drawn cycle, always square in size
    private Bitmap mCycleBitmap;

    //aka player number
    private int mCycleId;

    //paint of cycle
    private Paint mPaint;


    /**
     * Constructs a cycle with its center at the specified position.
     * It is given a default color based on its Id.
     *
     * @param centerX The center x position of the Cycle.
     * @param centerY The center y position of the Cycle.
     * @param width The width of the rectangle
     * @param height the height of the rectangle
     * @param cycleId An Id for the cycle, this will also determine the color.
     *                0-3 are red, yellow, green, and cyan. Any other ID is white.
     */
    public Cycle(double centerX, double centerY,double width, double height,int cycleId) {
        super(centerX, centerY, width, height);
        mCycleId=cycleId;
        mPaint=new Paint();
        setDefaultCycleColor();
        drawCycle(50, 50);
    }


    /**
     * Determines the color of the cycle based on its id.
     */
    private void setDefaultCycleColor(){
        switch (mCycleId) {
            case 0:
                mPaint.setColor(Color.RED);
                break;
            case 1:
                mPaint.setColor(Color.YELLOW);
                break;
            case 2:
                mPaint.setColor(Color.GREEN);
                break;
            case 3:
                mPaint.setColor(Color.CYAN);
                break;
            default:
                mPaint.setColor(Color.WHITE);
                break;
        }
    }


    /**
     * Draws the cycle on a bitmap of the specified size. The cycle is a square that is
     * filled with a single color. The user is responsible for making sure the paramaters are
     * non negative.
     *
     * @param width  the width of the bitmap that the cycle should be drawn on
     * @param height the height of the bitmap that the cycle should be drawn on
     *
     */
    public void drawCycle(int width,int height){
        mCycleBitmap= Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(mCycleBitmap);
        c.drawColor(mPaint.getColor());
    }


    /**
     * Get the bitmap of the cycle. The default size is 50x50. Calling drawCycle will change
     * the default size.
     * @return the cycle drawn onto a bitmap
     */
    public Bitmap getCycleBitmap(){return mCycleBitmap;}

    @Override
    public String toString() {
        String details ="~\n"+TAG+":"+mCycleId;
        //details+="\n|\tdirecion: "+mCycleDirection;
        //details+="\n|\tmoving: "+isCycleMoving;

        return details;
    }
}
