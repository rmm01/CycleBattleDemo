package com.yckir.cyclebattledemo.views.gameSurfaceView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;

import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Compass;
import com.yckir.cyclebattledemo.utility.Tile;


/**
 *      A two dimensional rectangle that is capable of moving its position, recording its path,
 *      and being drawn.
 *
 *      @author  Ricky Martinez
 *      @version 0.2
 */
public class Cycle extends GridRectangle {

    public  static final String     TAG             =   "Cycle";
    private static final String     CRASHED_KEY     =   TAG + ":CRASHED";
    private static final String     CRASH_TIME_KEY  =   TAG + ":CRASH_TIME";
    private static final String     DIRECTION_KEY   =   TAG + ":DIRECTION";
    private static final String     X_KEY           =   TAG + ":X";
    private static final String     Y_KEY           =   TAG + ":Y";
    private static final int        DEFAULT_SPEED   =   3;
    private final String TAG_ID;

    /**
     *  the id for this cycle
     */
    private int mCycleId;

    /**
     *  paint for this cycle
     */
    private Paint mLinePaint;

    /**
     * The speed that the cycle moves at. This is measured in terms of tiles per second
     */
    private int mSpeed;

    /**
     * If the Cycle crashed and can o longer move
     */
    private boolean mCrashed;

    /**
     * the time in milliseconds that indicates when the cycle crashed. use crashed(long time)
     * to set this value.
     */
    private long mCrashTime;

    /**
     * the direction that the cycle is traveling in
     */
    private Compass mDirection;

    /**
     * the path that the cycle has traveled in
     */
    private LinePath mPath;


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
        TAG_ID = "-Cycle_"+mCycleId;
        mLinePaint =new Paint();
        mSpeed=DEFAULT_SPEED;
        mDirection=Compass.SOUTH;
        mCrashed=false;
        mCrashTime=-1;
        setIdAttributes();
        mPath=new LinePath(centerX,centerY,0,mDirection);
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
        TAG_ID = "-Cycle_"+mCycleId;
        mLinePaint =paint;
        mSpeed=DEFAULT_SPEED;
        mCrashed=false;
        mCrashTime=-1;
        mPath=new LinePath(centerX,centerY,0,mDirection);
    }


    /**
     * Determines the color and direction of the cycle based on its id.
     */
    private void setIdAttributes(){
        Log.v(TAG,"setting color for id = " +mCycleId);
        switch (mCycleId) {
            case 0:
                mLinePaint.setColor(Color.RED);
                mDirection=Compass.SOUTH;
                break;
            case 1:
                mLinePaint.setColor(Color.GREEN);
                mDirection=Compass.NORTH;
                break;
            case 2:
                mLinePaint.setColor(Color.WHITE);
                mDirection=Compass.EAST;
                break;
            case 3:
                mLinePaint.setColor(Color.MAGENTA);
                mDirection=Compass.WEST;
                break;
            default:
                mLinePaint.setColor(Color.GRAY);
                mDirection=Compass.SOUTH;
                break;
        }
    }


    /**
     * move the cycles position in the current direction with its current speed. Will not move
     * if crashed.
     *
     * @param currentTime the amount of time in milliseconds since the cycle started moving
     */
    public void move(long currentTime){
        if(mCrashed)
            return;
        long previousLineTime = mPath.getLineStartTime(mPath.getNumLines());
        //the elapsed time since the previous vertex on the path
        long elapsedTime = currentTime-previousLineTime;

        //the distance that the cycle travels in delta time
        double distance = elapsedTime/1000.0*mSpeed;

        //update the current position of the cycle on the path
         mPath.movePath(distance, currentTime);

        setCenter(mPath.getLastPoint());
    }


    /**
     * Draw the cycle that fills the given canvas. THe cycle is a solid rectangle.
     *
     * @param canvas the canvas where the cycle will be drawn on
     */
    public void drawCycle(Canvas canvas){
        // Log.v(TAG,"left is "+getLeft()+", top is "+getTop());

        int paddingX = canvas.getClipBounds().left;
        int paddingY = canvas.getClipBounds().top;

        float w = (float) Tile.convert(Grid.GAME_GRID_TILE, GameManager.SCREEN_GRID_TILE, getWidth());
        float h = (float)Tile.convert(Grid.GAME_GRID_TILE, GameManager.SCREEN_GRID_TILE,getHeight());

        Paint insidePaint = new Paint();
        insidePaint.setColor(Color.BLUE);
        Paint boarderPaint = new Paint();
        boarderPaint.setColor(Color.GRAY);

        //draw edge of cycle
        canvas.drawColor(boarderPaint.getColor());
        //draw inside of cycle
        canvas.drawRect(
                paddingX + w / 10,
                paddingY + h / 10,
                paddingX + w * 9 / 10,
                paddingY + h * 9 / 10,
                insidePaint
        );



        //draw Line "engine" based on position
        switch (mDirection){
            case SOUTH:
                canvas.drawRect(
                        paddingX + w/4,
                        paddingY,
                        paddingX + w*3/4 ,
                        paddingY + h/2, mLinePaint);
                break;
            case NORTH:
                canvas.drawRect(
                        paddingX + w/4,
                        paddingY + h/2,
                        paddingX + w*3/4 ,
                        paddingY + h, mLinePaint);
                break;
            case WEST:
                canvas.drawRect(
                        paddingX + w/2,
                        paddingY + h/4,
                        paddingX + w ,
                        paddingY + h*3/4, mLinePaint);
                break;
            case EAST:
                canvas.drawRect(
                        paddingX,
                        paddingY + h/4,
                        paddingX + w/2 ,
                        paddingY + h*3/4, mLinePaint);
                break;
        }
    }


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
        mPath.changePathDirection(direction);
    }


    /**
     * Change the new direction the cycle will be traveling in and set the time when this happens.
     * Will not move if crashed.
     *
     * @param newDirection the new direction the cycle will be traveling in. If the direction is
     *                     perpendicular to the current direction, it wont be changed.
     * @param time the time in milliseconds when the direction changes. the time must also be after
     *             the previous direction change.
     * @see Compass
     */
    public void changeDirection(Compass newDirection,long time) {
        if(mCrashed)
            return;
        long lastTimeStartTime = mPath.getLineStartTime(mPath.getNumLines());
        double lineLength = (time - lastTimeStartTime )/1000.0*mSpeed;
        boolean success;
        if( ( mPath.getLine( mPath.getNumLines( ) ) ).getEndTime() < time )
            success = mPath.moveAndChangeDirection(newDirection, time, lineLength);
        else
            success = mPath.bendLastLine(newDirection, time, lineLength);

        if(!success)
        {
            Log.d(TAG, "direction change failed");
            return;
        }
        setCenter(mPath.getLastPoint());
        mDirection=newDirection;
    }


    /**
     * Draws the path onto the canvas with the cycles color.
     *
     * @param canvas the canvas that the path should be drawn on
     */
    public void drawPath(Canvas canvas){
        mPath.drawPath(canvas, mLinePaint);
    }


    /**
     * Set the cycle to be in a crashed state.
     *
     * @param crashTime the time in milliseconds when the cycle crashed.
     */
    public void crashed(long crashTime){
        mCrashed=true;
        mCrashTime = crashTime;
    }


    /**
     * set the cycle to the not be crashed. The crash time is reset ot the default of -1.
     */
    public void uncrash(){
        mCrashed = false;
        mCrashTime = -1;
    }


    /**
     * @return true if the cycle has crashed and cant move, false otherwise
     */
    public boolean hasCrashed(){return mCrashed;}


    /**
     * Determine if the cycle crashed with its own path.
     *
     * @return true if the cycle crashed with its own path, false otherwise
     */
    public boolean selfCrashed(){
        for(int lineNumber  = 1; lineNumber <= mPath.getNumLines()-3; lineNumber++){
            if(Grid.overlap(this, mPath.getLine(lineNumber))){
                return true;
            }
        }
        return false;
    }


    /**
     * Determine if a rectangle intersects with the cycles path of the cycle itself
     * @param rectangle the rectangle to be tested
     * @return true if the given rectangle intersects with the cycle or its path, false otherwise
     */
    public boolean intersectsWithPath(Grid.GridObject rectangle){
        for(int lineNumber  = 1; lineNumber <= mPath.getNumLines(); lineNumber++){
            if(Grid.overlap(rectangle, mPath.getLine(lineNumber))){
                return true;
            }
        }
        return Grid.overlap(rectangle, this);
    }


    /**
     * Save the state of the cycle onto a bundle.
     *
     * @param bundle the bundle to save the state onto
     */
    public void saveState(Bundle bundle) {
        bundle.putBoolean( CRASHED_KEY + TAG_ID, mCrashed );
        bundle.putLong( CRASH_TIME_KEY + TAG_ID, mCrashTime );
        bundle.putSerializable( DIRECTION_KEY + TAG_ID, mDirection );
        bundle.putDouble( X_KEY + TAG_ID, getX() );
        bundle.putDouble( Y_KEY + TAG_ID, getY() );
        mPath.saveState( bundle, TAG_ID );
    }


    /**
     * Restore the previous state of the cycle from a bundle.
     *
     * @param bundle the bundle that has the previous state saved
     */
    public void restoreState(Bundle bundle){
        mCrashed = bundle.getBoolean( CRASHED_KEY +TAG_ID, false );
        mCrashTime = bundle.getLong(CRASH_TIME_KEY + TAG_ID, 0);
        mDirection = (Compass)bundle.getSerializable( DIRECTION_KEY+ TAG_ID );
        double x = bundle.getDouble( X_KEY + TAG_ID, 0 );
        double y = bundle.getDouble( Y_KEY + TAG_ID, 0 );
        setCenter(x, y);
        mPath.restoreState( bundle, TAG_ID );
    }


    @Override
    public String toString() {

        ClassStateString description = new ClassStateString(TAG);
        description.incrementTabs();
        description.concat(super.toString());
        description.decrementTabs();
        description.addMember("mCycleId", mCycleId);
        description.addMember("mSpeed", mSpeed);
        description.addMember("mCrashed", mCrashed);
        description.addMember("mCrashTIme", mCrashTime);
        description.addMember("mDirection", mDirection);
        description.addClassMember("mPath", mPath);
        return description.getString();
    }
}
