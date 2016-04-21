package com.yckir.cyclebattledemo.views.gameSurfaceView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.yckir.cyclebattledemo.R;
import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Compass;
import com.yckir.cyclebattledemo.utility.Point;
import com.yckir.cyclebattledemo.utility.Tile;


/**
 *      A two dimensional rectangle that is capable of moving its position, recording its path,
 *      and being drawn. The with and height of the cycle will be swapped as the rectangular
 *      cycle rotates.
 *
 *      @author  Ricky Martinez
 */
public class Cycle extends GridRectangle {

    public  static final String     TAG             =   "Cycle";
    public  static final int        DEFAULT_PLACE   =   -1;
    public  static final int        DEFAULT_TIME    =   -1;
    private static final String     CRASHED_KEY     =   TAG + ":CRASHED";
    private static final String     CRASH_TIME_KEY  =   TAG + ":CRASH_TIME";
    private static final String     DIRECTION_KEY   =   TAG + ":DIRECTION";
    private static final String     PLACE_KEY       =   TAG + ":PLACE";
    private static final String     X_KEY           =   TAG + ":X";
    private static final String     Y_KEY           =   TAG + ":Y";
    private static final String     WIDTH_KEY       =   TAG + ":WIDTH";
    private static final String     HEIGHT_KEY      =   TAG + ":HEIGHT";
    private final String TAG_ID;
    private Drawable mCycleImageN;
    private Drawable mCycleImageS;
    private Drawable mCycleImageE;
    private Drawable mCycleImageW;

    /**
     *  the id for this cycle
     */
    private int mCycleId;

    /**
     *  paint for this cycle
     */
    private Paint mLinePaint;


    /**
     * the name of the cycle, this is currently its color.
     */
    private String mName;


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
     * The place that the cycle finished in, 1 = first etc.
     */
    private int mPlace;

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
     * @param width The vertical length of the cycle, length from front to rear.
     * @param height the horizontal length of the cycle, length from door to door.
     * @param cycleId An Id for the cycle, this will also determine the color.
     *                0-3 are red, yellow, green, and purple. Any other ID is blue.
     */
    public Cycle(Context context, double centerX, double centerY, double width, double height, int cycleId) {
        super(centerX, centerY, width, height);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String speed = pref.getString(
                context.getResources().getString(R.string.pref_speed_key) ,
                context.getResources().getString(R.string.pref_speed_default));

        mSpeed = Integer.parseInt(speed);

        mCycleId = cycleId;
        TAG_ID = "-Cycle_" + mCycleId;
        mLinePaint = new Paint();
        mDirection = Compass.SOUTH;
        mCrashed = false;
        mCrashTime = DEFAULT_TIME;
        mPlace = DEFAULT_PLACE;
        setIdAttributes(context);
        mPath=new LinePath(getRearX(), getRearY(), 0, mDirection);
    }


    /**
     * @return the x coordinate of the cycles rear.
     */
    private double getRearX(){
        switch (mDirection) {
            case NORTH:
                return getX();

            case SOUTH:
                return getX();

            case EAST:
                return getX() - getWidth() / 2;

            case WEST:
                return getX() + getWidth() / 2;

            default:
                Log.e(TAG, "could not determine direction");
                return 0;
        }
    }


    /**
     * @return the y coordinate of the cycles rear.
     */
    private double getRearY(){
        switch (mDirection) {
            case NORTH:
                return getY() + getHeight() / 2;

            case SOUTH:
                return getY() - getHeight() / 2;

            case EAST:
                return getY();

            case WEST:
                return getY();

            default:
                Log.e(TAG, "could not determine direction");
                return 0;
        }
    }


    /**
     * Moves the cycles center coordinate so that its rear point is at the the given point.
     *
     * @param point a coordinate where the cycles rear will be placed
     */
    public void setRear(Point point){
        double x = 0;
        double y = 0;

        switch (mDirection) {
            case NORTH:
                x = point.getPositionX();
                y = point.getPositionY() - getHeight()/2;
                break;
            case SOUTH:
                x = point.getPositionX();
                y = point.getPositionY() + getHeight()/2;
                break;
            case EAST:
                x = point.getPositionX() + getWidth()/2;
                y = point.getPositionY();
                break;
            case WEST:
                x = point.getPositionX() - getWidth()/2;
                y = point.getPositionY();
                break;
        }
        setCenter(x,y);
    }


    /**
     * Rotates the cycle dimensions so that its with and height are swapped.
     * This should be called when a cycle successfully changes directions.
     * This is necessary if the cycles shape is a rectangle.
     */
    private void rotateCycle(){
        double w = getWidth();
        double h = getHeight();

        setWidth(h);
        setHeight(w);
    }


    /**
     * Determines the color and direction of the cycle based on its id.
     */
    private void setIdAttributes(Context context){
        Log.v(TAG,"setting color for id = " +mCycleId);
        switch (mCycleId) {
            case 0:
                mLinePaint.setColor(Color.RED);
                mName = "Red";
                mCycleImageN = ResourcesCompat.getDrawable(context.getResources(), R.drawable.red_cycle_n, null);
                mCycleImageE = ResourcesCompat.getDrawable(context.getResources(), R.drawable.red_cycle_e, null);
                mCycleImageS = ResourcesCompat.getDrawable(context.getResources(), R.drawable.red_cycle_s, null);
                mCycleImageW = ResourcesCompat.getDrawable(context.getResources(), R.drawable.red_cycle_w, null);
                mDirection=Compass.SOUTH;
                break;
            case 1:
                mLinePaint.setColor(Color.GREEN);
                mName = "Green";
                mCycleImageN = ResourcesCompat.getDrawable(context.getResources(), R.drawable.green_cycle_n, null);
                mCycleImageE = ResourcesCompat.getDrawable(context.getResources(), R.drawable.green_cycle_e, null);
                mCycleImageS = ResourcesCompat.getDrawable(context.getResources(), R.drawable.green_cycle_s, null);
                mCycleImageW = ResourcesCompat.getDrawable(context.getResources(), R.drawable.green_cycle_w, null);
                mDirection=Compass.NORTH;
                break;
            case 2:
                mLinePaint.setColor(Color.YELLOW);
                mName = "Yellow";
                mCycleImageN = ResourcesCompat.getDrawable(context.getResources(), R.drawable.yellow_cycle_n, null);
                mCycleImageE = ResourcesCompat.getDrawable(context.getResources(), R.drawable.yellow_cycle_e, null);
                mCycleImageS = ResourcesCompat.getDrawable(context.getResources(), R.drawable.yellow_cycle_s, null);
                mCycleImageW = ResourcesCompat.getDrawable(context.getResources(), R.drawable.yellow_cycle_w, null);
                mDirection=Compass.EAST;
                rotateCycle();
                break;
            case 3:
                mLinePaint.setColor(Color.MAGENTA);
                mName = "Purple";
                mCycleImageN = ResourcesCompat.getDrawable(context.getResources(), R.drawable.purple_cycle_n, null);
                mCycleImageE = ResourcesCompat.getDrawable(context.getResources(), R.drawable.purple_cycle_e, null);
                mCycleImageS = ResourcesCompat.getDrawable(context.getResources(), R.drawable.purple_cycle_s, null);
                mCycleImageW = ResourcesCompat.getDrawable(context.getResources(), R.drawable.purple_cycle_w, null);
                mDirection=Compass.WEST;
                rotateCycle();
                break;
            default:
                mLinePaint.setColor(Color.BLUE);
                mCycleImageN = ResourcesCompat.getDrawable(context.getResources(), R.drawable.blue_cycle_n, null);
                mCycleImageE = ResourcesCompat.getDrawable(context.getResources(), R.drawable.blue_cycle_e, null);
                mCycleImageS = ResourcesCompat.getDrawable(context.getResources(), R.drawable.blue_cycle_s, null);
                mCycleImageW = ResourcesCompat.getDrawable(context.getResources(), R.drawable.blue_cycle_w, null);
                mName = "Blue";
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

        setRear(mPath.getLastPoint());
    }


    /**
     * Draw the cycle that fills the given canvas. The cycle is drawn using only rectangles.
     *
     * @param canvas the canvas where the cycle will be drawn on
     */
    public void drawCycleRetro(Canvas canvas){
        // Log.v(TAG,"left is "+getLeft()+", top is "+getTop());

        int paddingX = canvas.getClipBounds().left;
        int paddingY = canvas.getClipBounds().top;

        float w = (float) Tile.convert(Grid.GAME_GRID_TILE, GameManager.SCREEN_GRID_TILE, getWidth());
        float h = (float) Tile.convert(Grid.GAME_GRID_TILE, GameManager.SCREEN_GRID_TILE,getHeight());

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
     * Draw the cycle that fills the given canvas. The cycle are drawn from drawable resources.
     *
     * @param canvas the canvas where the cycle will be drawn on
     */
    public void drawCycle(Canvas canvas){

        Rect imageBounds = canvas.getClipBounds();

        switch (mDirection) {
            case NORTH:
                mCycleImageN.setBounds(imageBounds);
                mCycleImageN.draw(canvas);
                break;
            case SOUTH:
                mCycleImageS.setBounds(imageBounds);
                mCycleImageS.draw(canvas);
                break;
            case EAST:
                mCycleImageE.setBounds(imageBounds);
                mCycleImageE.draw(canvas);
                break;
            case WEST:
                mCycleImageW.setBounds(imageBounds);
                mCycleImageW.draw(canvas);
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


    public long getCrashTime(){ return mCrashTime; }


    /**
     * @return the direction the cycle is traveling in
     * @see Compass
     */
    public Compass getDirection() {
        return mDirection;
    }


    /**
     * get the name of the cycle. This is currently its color
     * @return the color of the cycle.
     */
    public String getName(){
        return mName;
    }


    /**
     * @return current place that the cycle finished in. Default is one
     */
    public int getPlace(){return mPlace;}


    /**
     * @return the cycles id
     */
    public int getId(){return mCycleId;}


    /**
     * set the speed of the cycle
     * @param speed the new speed in terms of tiles per second
     */
    public void setSpeed(int speed) {
        mSpeed = speed;
    }


    /**
     * set the place that the cycle finished in.
     * @param place the place of the cycle
     */
    public void setPlace(int place){
        mPlace = place;
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
     * @return true if the direction change was valid, false otherwise
     * @see Compass
     */
    public boolean changeDirection(Compass newDirection,long time) {
        if(mCrashed)
            return false;
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
            return false;
        }
        setRear(mPath.getLastPoint());
        mDirection=newDirection;
        rotateCycle();
        return true;
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
        bundle.putLong( PLACE_KEY + TAG_ID, mPlace );
        bundle.putSerializable( DIRECTION_KEY + TAG_ID, mDirection );
        bundle.putDouble( X_KEY + TAG_ID, getX() );
        bundle.putDouble( Y_KEY + TAG_ID, getY() );
        bundle.putDouble( WIDTH_KEY + TAG_ID, getWidth() );
        bundle.putDouble( HEIGHT_KEY + TAG_ID, getHeight() );
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
        mPlace = bundle.getInt(PLACE_KEY + TAG_ID, 0);
        mDirection = (Compass)bundle.getSerializable( DIRECTION_KEY+ TAG_ID );
        double x = bundle.getDouble( X_KEY + TAG_ID, 0 );
        double y = bundle.getDouble( Y_KEY + TAG_ID, 0 );
        setWidth( bundle.getDouble( WIDTH_KEY + TAG_ID) );
        setHeight( bundle.getDouble( HEIGHT_KEY + TAG_ID) );
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
        description.addMember("mPlace", mPlace);
        description.addMember("mDirection", mDirection);
        description.addClassMember("mPath", mPath);
        return description.getString();
    }
}
