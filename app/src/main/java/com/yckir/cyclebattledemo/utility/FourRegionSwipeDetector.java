package com.yckir.cyclebattledemo.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.yckir.cyclebattledemo.R;

import java.util.ArrayList;

/**
 * Used to determine which region on the device was swiped on the touch screen. The Device is split
 * into 4 regions. Divide the Rectangle in half vertically and into four pieces horizontally resulting in 8 total.
 * Number the pieces clockwise starting at top left piece 0-7. Region 0 = (0,1), Region 1 = (4,5),
 * Region 2 = (6,7), and Region 3 = (2,3).
 */
public class FourRegionSwipeDetector {
    public static final String TAG = "4_REGION_SWIPE_DETECTOR";
    public static final int MAX_NUM_FINGERS = 5;

    private final static double MIN_SWIPE_DISTANCE = 0;
    private ArrayList<SwipeMotionEvent> mEvents;
    private OnRegionSwipeListener mListener;
    private DisplayMetrics mDisplayMetrics;

    private boolean mDisabled;
    private boolean mBoundariesDisabled;
    private int mNumRegions;

    /**
     * Create a swipe detector that determines what region on the device a swipe gesture occurred in.
     *
     * @param context app context
     * @param numRegions the number of regions that will be checked. This value must be between 1
     *                   and 4, otherwise an error will be logged and a default value of 1 and 4
     *                   will be used.
     * @param listener a swipe listener that will be notified when swipes occur
     */
    public FourRegionSwipeDetector(Context context, int numRegions, DisplayMetrics metrics, OnRegionSwipeListener listener){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        mBoundariesDisabled = pref.getBoolean(
                context.getResources().getString(R.string.pref_touch_indicators_key) , false);

        mDisplayMetrics = metrics;

        mEvents = new ArrayList<>(MAX_NUM_FINGERS);
        for (int i = 0; i < MAX_NUM_FINGERS; i++){
            mEvents.add(i,new SwipeMotionEvent());
        }

        mListener = listener;

        if(numRegions<1){
            mNumRegions=1;
            Log.e(TAG,"number of regions is less that 1, setting numRegions to 1");
            return;
        }
        if(numRegions>4){
            mNumRegions=4;
            Log.e(TAG,"number of regions is greater that 4, setting numRegions to 4");
            return;
        }
        mNumRegions =numRegions;
    }


    /**
     * Triggered as a result of ACTION_DOWN or ACTION_POINTER_DOWN on the motion event.
     * Stores the x,y,and id of the event as a SwipeMotionEvent.
     * The SwipeMotionEvent Id uses the Motion Events id.
     *
     * @param event the event that had .
     */
    private void downAction(MotionEvent event) {
        int index = MotionEventCompat.getActionIndex(event);
        int id = MotionEventCompat.getPointerId(event, index);
        int x = (int)event.getX(index);
        int y = (int)event.getY(index);

        SwipeMotionEvent sme = mEvents.get(id);

        sme.startSwipe(x,y);
    }


    /**
     * Triggered as a result of ACTION_MOVE on the motion event.
     * Stores the MotionEvent's position as the end position of a SwipeMotionEvent for each pointer
     * in the motion event.
     * The SwipeMotionEvent was created and saved in method downAction.
     * The SwipeMotionEvent uses the id of the MotionEvent.
     *
     * @param event the event to have its position stored.
     */
    private void moveAction(MotionEvent event){
        int pointerCount = event.getPointerCount();

        for(int p = 0; p < pointerCount; p++){
            int id = MotionEventCompat.getPointerId(event,p);
            int x = (int)event.getX(p);
            int y = (int)event.getY(p);

            if(id >= MAX_NUM_FINGERS)
                continue;

            mEvents.get(id).moveSwipe(x, y);
        }
    }


    /**
     * Triggered as a result of ACTION_UP of ACTION_POINTER_UP on a motion event.
     * The SwipeMotionEvent for the MotionEvent's id considered valid if
     * the swipe is was greater than MIN_SWIPE_DISTANCE and if a valid region was swiped.
     * If valid, the listeners onRegionSwipe method is called.
     *
     * @param event event with an ACTION_DOWN
     */
    private void upAction(MotionEvent event) {
        int index = MotionEventCompat.getActionIndex(event);
        int id = MotionEventCompat.getPointerId(event, index);

        SwipeMotionEvent sme = mEvents.get(id);

        //if distance between the points is too small or an invalid region was swiped, do nothing
        if(sme.getSwipeDistance() < MIN_SWIPE_DISTANCE || sme.getSwipedRegion() >= mNumRegions)
            return;

        if(mListener!=null)
            mListener.onRegionSwipe(sme.getSwipedRegion(), sme.getSwipeDirection(), System.currentTimeMillis());
    }


    /**
     * Triggered as a result of ACTION_CANCEL or ACTION_OUTSIDE on a motion event, or when a swipe has complected.
     * Uses the id of the MotionEvent to set its corresponding SwipeMotionEvent
     * Active field to false.
     *
     * @param event the event what was canceled or completed a SwipeMotionEvent
     */
    private void endSwipeMotionEvent(MotionEvent event){
        int index = MotionEventCompat.getActionIndex(event);
        int id = MotionEventCompat.getPointerId(event, index);
        mEvents.get(id).endSwipe();
    }


    /**
     * Disables the swipe detector so that it no longer reads input events. Clears the current event
     * entries.The swipe detector is enabled by default.
     */
    public void disable(){
        mDisabled = true;
        mEvents.clear();
    }


    /**
     * Disables the swipe detector so that it reads input events. The swipe detector is enabled
     * by default.
     */
    public void enable(){
        mDisabled = false;
    }


    /**
     * @return true is the detector is enabled, false otherwise.
     */
    public boolean isDisabled(){
        return mDisabled;
    }


    /**
     * @return the number of regions in the rectangle.
     */
    public int getNumRegions(){
        return mNumRegions;
    }


    /**
     * Set the number of region in the rectangle.
     *
     * @param numRegions the number of regions. Must be between 1 and 4 otherwise a default of
     *                   1 or 4 value will be used.
     */
    public void setNumRegions(int numRegions){
        if(numRegions<1){
            mNumRegions=1;
            Log.e(TAG,"number of regions is less that 1, setting numRegions to 1");
            return;
        }
        if(numRegions>4){
            mNumRegions=4;
            Log.e(TAG,"number of regions is greater that 4, setting numRegions to 1");
            return;
        }
        mNumRegions =numRegions;
        mNumRegions =numRegions;
    }


    /**
     * Receive a touch event and perform action bases on if the its action.
     * The actions chccked are down, up move outside and cancel.
     *
     * @param event a motion event
     */
    public void receiveTouchEvent(MotionEvent event){
        if(mDisabled)
            return;

        int id = MotionEventCompat.getPointerId(event,MotionEventCompat.getActionIndex(event));
        if(id >= MAX_NUM_FINGERS)
            return;

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                downAction(event);
                break;

            case MotionEvent.ACTION_MOVE:
                moveAction(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                upAction(event);
                endSwipeMotionEvent(event);
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                endSwipeMotionEvent(event);
                break;
        }
    }


    /**
     * Draw the current swipes on the canvas.
     * This method can be safely called in separate threads
     *
     * @param canvas Canvas to be draw. The canvas should be the size of the device.
     */
    public void drawTouch(Canvas canvas){
        float x1,x2,y1,y2;
        float circleRadius = 15 *mDisplayMetrics.density;

        Paint circlePaint = new Paint();
        circlePaint.setColor(Color.GRAY);
        circlePaint.setAlpha(100);
        circlePaint.setStyle(Paint.Style.STROKE);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setAlpha(100);
        linePaint.setStrokeWidth(5 * mDisplayMetrics.density);

        for(int i = 0; i< mEvents.size(); i++){
            SwipeMotionEvent event = mEvents.get(i);
            if(event != null && event.isSwiping()) {
                Point startPoint = event.getStartPoint();
                Point endPoint = event.getLastPoint();

                x1 = (float) startPoint.getPositionX();
                y1 = (float) startPoint.getPositionY();
                x2 = (float) endPoint.getPositionX();
                y2 = (float) endPoint.getPositionY();

                Compass direction = Compass.getDirection(x1,y1,x2,y2);

                canvas.drawCircle(x1, y1, circleRadius, circlePaint);
                canvas.drawCircle(x2, y2, circleRadius, circlePaint);

                switch (direction) {
                    case NORTH:
                    case SOUTH:
                        canvas.drawLine(x1, y1, x1, y2, linePaint);
                        break;
                    case EAST:
                    case WEST:
                        canvas.drawLine(x1, y1, x2, y1, linePaint);
                        break;
                }
            }
        }
    }


    /**
     * Draw the boundaries for touch region. This method will do nothing if the preference
     * pref_touch_indicators_key is true;
     *
     * @param canvas canvas to draw on
     */
    public void drawTouchBoundaries(Canvas canvas){
        if(mBoundariesDisabled)
            return;

        float lineWidth = 5 * mDisplayMetrics.density;
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;

        Paint regionPaint = new Paint();
        regionPaint.setColor(Color.GRAY);
        regionPaint.setAlpha(100);
        regionPaint.setStrokeWidth( lineWidth );

        canvas.drawLine(
                0,
                height / 4,
                width,
                height / 4,
                regionPaint);
        canvas.drawLine(
                0,
                height * 3 / 4,
                width,
                height * 3 / 4,
                regionPaint);
        canvas.drawLine(
                width  / 2,
                height / 4,
                width  / 2,
                height * 3 / 4,
                regionPaint);

    }


    /**
     * Draw the the regions swiped on the canvas.
     * This method can be safely called in separate threads
     *
     * @param canvas Canvas to be draw. The canvas should be the size of the device.
     */
    public void drawRegions(Canvas canvas){
        float margins = 5 * mDisplayMetrics.density;
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;

        Paint gray = new Paint();
        gray.setColor(Color.GRAY);
        gray.setAlpha(100);

        for(int i = 0; i< mEvents.size(); i++){
            SwipeMotionEvent event = mEvents.get(i);
            if(event != null && event.isSwiping()) {

                switch (event.getSwipedRegion()){
                    case 0:
                        canvas.drawRect(
                                0,
                                0,
                                width      - margins,
                                height / 4 - margins,
                                gray);

                        break;

                    case 1:
                        canvas.drawRect(
                                0,
                                height * 3/4 + margins,
                                width        - margins,
                                height       - margins,
                                gray);

                        break;

                    case 2:
                        canvas.drawRect(
                                0,
                                height / 4    + margins,
                                width  / 2    - margins,
                                height * 3/4  - margins,
                                gray);
                        break;

                    case 3:
                        canvas.drawRect(
                                width  / 2    + margins,
                                height / 4    + margins,
                                width         - margins,
                                height * 3/4  - margins,
                                gray);
                        break;
                }

            }
        }
    }


    /**
     * Listener that has its methods called when a valid swipe on the screen occurred.
     */
    public interface OnRegionSwipeListener {

        /**
         *
         * @param regionNumber the region that was swiped
         * @param direction the direction of the swipe
         * @param swipeTime the time that the swipe occurred. This is when the finger was released.
         */
        void onRegionSwipe(int regionNumber, Compass direction, long swipeTime);
    }


    /**
     * Tracks information related to swiping motions. The initial position of the swipe is given in
     * startSwipe, the last position updated at moveSwipe, and finished with endSwipe.
     * This class is updated in the ui thread as motion events are received and drawn on
     * SurfaceDrawingTask's thread. Because of this, the methods are synchronized
     *
     */
    private class SwipeMotionEvent{

        private Point mStartPoint;
        private Point mEndPoint;
        private boolean mActive;
        private int mRegion;

        /**
         * Create an inactive SwipeMotionEvent.
         */
        SwipeMotionEvent(){
            mStartPoint = new Point(0,0);
            mEndPoint = new Point(0,0);
            mActive = false;
            mRegion = -1;
        }


        /**
         * Determines what region of the screen was swiped
         */
        private void determineRegion(){
            int verticalRegion = (int)mStartPoint.getPositionY() /(mDisplayMetrics.heightPixels/4);
            switch(verticalRegion){
                case 0:
                    mRegion = 0;
                    break;
                case 1:
                case 2:
                    if((int)mStartPoint.getPositionX() < mDisplayMetrics.widthPixels/2)
                        mRegion = 2;
                    else
                        mRegion = 3;
                    break;
                case 3:
                    mRegion = 1;
                    break;
                default:
                    mRegion = 0;
                    break;
            }
        }


        /**
         * starts a swipe at the specified position
         *
         * @param x x coordinate
         * @param y y coordinate
         */
        public synchronized void startSwipe(int x, int y){
            mStartPoint.setPosition(x,y);
            mEndPoint.setPosition(x,y);
            mActive = true;
            determineRegion();
        }


        /**
         * Move the swipe to the specified direction.
         *
         * @param x x coordinate
         * @param y y coordinate
         */
        public synchronized void moveSwipe(int x, int y){
            mEndPoint.setPosition(x, y);
        }


        /**
         * The swipe is no longer considered active
         */
        public synchronized void endSwipe(){
            mActive = false;
            mRegion = -1;
        }


        /**
         * Get the distance between the start and end position of the swipe.
         *
         * @return the distance the swipe has traveled across the screen.
         *         Measured in density pixels(dp).
         */
        public synchronized double getSwipeDistance(){
            return Point.distance(mStartPoint,mEndPoint) * mDisplayMetrics.density;
        }


        /**
         * @return the id for the region that was swiped.
         */
        public synchronized int getSwipedRegion(){
            return mRegion;
        }


        /**
         * @return the direction of the swipe
         */
        public synchronized Compass getSwipeDirection(){
            return Compass.getDirection(mStartPoint, mEndPoint);
        }


        /**
         * get the starting position of the swipe
         *
         * @return a copy of start position
         */
        public synchronized Point getStartPoint(){
            return mStartPoint.makeCopy();
        }


        /**
         * get the last position of the swipe.
         *
         * @return a copy of last position
         */
        public synchronized Point getLastPoint(){
            return mEndPoint.makeCopy();
        }


        /**
         * check if currently tracking a swipe
         *
         * @return true if currently tracking an active swipe, false otherwise
         */
        public synchronized boolean isSwiping(){ return mActive; }


        @Override
        public String toString() {
            return "Active = " + mActive +" \n " + mStartPoint.toString() +" \n " + mEndPoint.toString();
        }
    }


    /**
     * Debugging method to log details about a motion event
     * @param event the event to have its details logged
     */
    private void logMotionEvent(MotionEvent event){
        Log.v(TAG, "received motion event " + event.toString());

        int pointerCount = event.getPointerCount();
        int historySize = event.getHistorySize();

        Log.v(TAG,"hs = "+historySize +", pc = " +pointerCount);
        for (int h = 0; h < historySize; h++) {
            Log.v( TAG, "\tAt time " + event.getHistoricalEventTime(h) + ":" );
            for (int p = 0; p < pointerCount; p++) {
                Log.v(TAG, "\t\t  pointer " +  event.getPointerId(p)
                        + ": ("+event.getHistoricalX(p, h)+","+event.getHistoricalY(p, h)+")");
            }
        }
        Log.v(TAG,"+");
        Log.v(TAG,"\tAt time "+ event.getEventTime()+":");
        for (int p = 0; p < pointerCount; p++) {
            Log.v(TAG, "\t\t  pointer " +  event.getPointerId(p)
                    + ": ("+ event.getX(p)+","+ event.getY(p)+")");
        }
        Log.v(TAG,"---------");
    }
}