package com.yckir.cyclebattledemo;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


/**
 *      A two dimensional rectangle that is capable of moving its position, recording its path,
 *      and being drawn.
 *
 *      @author  Ricky Martinez
 *      @version 0.2
 */
public class Cycle extends GridRectangle{
    /**
     * Identifier for debugging.
     */
    public static final String TAG= "Cycle";

    /**
     *     bitmap of the drawn cycle, always square in size
     */
    private Bitmap mCycleBitmap;

    /**
     *  the id for this cycle
     */
    private int mCycleId;

    /**
     *  paint for this cycle
     */
    private Paint mPaint;

    /**
     * The speed that the cycle moves at. This is measured in terms of tiles per second
     */
    private int mSpeed;
    public static final int DEFAULT_SPEED=10;

    /**
     * the direction that the cycle is traveling in
     */
    private Compass mDirection;

    /**
     * the path that the cycle has traveled in
     */
    private Path mPath;


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
        mSpeed=DEFAULT_SPEED;
        mDirection=Compass.SOUTH;
        mPath=new Path(centerX,centerY,0,mDirection);
        setDefaultCycleColor();
        drawCycle(50, 50);
    }


    /**
     *  for debugging
     * @param centerX The center x position of the Cycle.
     * @param centerY The center y position of the Cycle.
     * @param width The width of the rectangle
     * @param height The height of the rectangle
     * @param cycleId An Id for the cycle
     * @param paint The color for the cycle
     */
    public Cycle(double centerX, double centerY,double width, double height,int cycleId, Paint paint) {
        super(centerX, centerY, width, height);
        mCycleId=cycleId;
        mPaint=paint;
        mSpeed=DEFAULT_SPEED;
        mDirection=Compass.SOUTH;
        mPath=new Path(centerX,centerY,0,mDirection);
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
     * move the cycles position in the current direction with its current speed
     * @param time the amount of time in milliseconds since the cycle started moving
     */
    public void move(long time){
        PathVertex lastPivot = mPath.getLastPivotVertex();

        //the elapsed time since the previous vertex on the path
        long elapsedTime = time-lastPivot.getTime();

        //the distance that the cycle travels in delta time
        double distance = elapsedTime/1000.0*mSpeed;

        //update the current position of the cycle on the path
         mPath.moveEndVertex(distance, time);

        setCenter(mPath.getLastPoint());
    }


    /**
     * Draws the cycle on a bitmap of the specified size. The cycle is a square that is
     * filled with a single color. The user is responsible for making sure the parameters are
     * non negative. Get the bitmap with getCycleBitmap();
     *
     * @param width  the width of the bitmap that the cycle should be drawn on
     * @param height the height of the bitmap that the cycle should be drawn on
     *
     */
    public void drawCycle(int width,int height){
       // Log.v(TAG,"left is "+getLeft()+", top is "+getTop());
        mCycleBitmap= Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(mCycleBitmap);
        c.drawColor(mPaint.getColor());
    }


    /**
     * Draw the cycle that fills the given canvas. THe cycle is a solid rectangle.
     *
     * @param canvas the canvas where the cycle will be drawn on
     */
    public void drawCycle(Canvas canvas){
        // Log.v(TAG,"left is "+getLeft()+", top is "+getTop());
        canvas.drawColor(mPaint.getColor());
    }


    /**
     * Get the bitmap of the cycle. The default size is 50x50. Calling drawCycle will change
     * the default size.
     * @return the cycle drawn onto a bitmap
     */
    public Bitmap getCycleBitmap(){return mCycleBitmap;}


    /**
     * get the speed of the cycle
     * @return the speed of the cycle in terms of tiles per second
     */
    public int getSpeed() {
        return mSpeed;
    }


    /**
     * @return the direction the cycle is traveling in
     * @see Compass
     */
    public Compass getDirection() {
        return mDirection;
    }


    /**
     * set the speed of the cycle
     * @param speed the new speed in terms of tiles per second
     */
    public void setSpeed(int speed) {
        mSpeed = speed;
    }


    /**
     * change the new direction the cycle will be traveling in
     * @param direction the new direction the cycle will be traveling in. If the is opposite of
     *                  the current direction, the direction will not change
     @see Compass
     */
    public void changeDirection(Compass direction) {
        if(Compass.isPerpendicular(direction,mDirection))
            return;
        mDirection = direction;
        mPath.changeEndVertexDirection(direction);
    }



    /**
     * change the new direction the cycle will be traveling in and set the time when this happens
     *
     * @param newDirection the new direction the cycle will be traveling in. If the direction is
     *                     perpendicular to the current direction, it wont be changed.
     * @param time the time in milliseconds when the direction changes. the time must also be after
     *             the previous direction change.
     * @see Compass
     */
    public void changeDirection(Compass newDirection,long time) {
        boolean success = mPath.delayedChangeEndVertexDirection(newDirection, time, mSpeed);
        if(!success)
        {
            Log.d(TAG, "direction change failed");
            return;
        }
        setCenter(mPath.getLastPoint());
        mDirection=newDirection;
    }

    @Override
    public String toString() {
        String details ="~\n"+TAG+":";
                details+=mCycleId;
        //details+="\n|\tdirecion: "+mCycleDirection;
        //details+="\n|\tmoving: "+isCycleMoving;

        return details;
    }
}
