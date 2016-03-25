package com.yckir.cyclebattledemo.utility;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

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
    public boolean mDisabled;
    private int mNumRegions;

    /**
     * Create a swipe detector that determines what region on the device a swipe gesture occurred in.
     *
     * @param numRegions the number of regions that will be checked. This value must be between 1
     *                   and 4, otherwise an error will be logged and a default value of 1 and 4
     *                   will be used.
     * @param listener a swipe listener that will be notified when swipes occur
     */
    public FourRegionSwipeDetector(int numRegions, DisplayMetrics metrics, OnRegionSwipeListener listener){
        mDisplayMetrics = metrics;

        mEvents = new ArrayList<>(MAX_NUM_FINGERS);
        for (int i = 0; i < MAX_NUM_FINGERS; i++){
            mEvents.add(i,new SwipeMotionEvent(i,new Point(0,0),false));
        }

        if(listener==null)
            mListener = new OnRegionSwipeListener() {
                @Override
                public void onRegionSwipe(int regionNumber, Compass direction, long swipeTime) {
                }
            };
        else
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
     * Determine which region was swiped based on the given point
     *
     * @param point the point being tested
     * @return an integer value in the range 0-3 that represents identifies region that was swiped
     */
    private int determineRegion(Point point){
        int verticalRegion = (int)point.getPositionY()/(mDisplayMetrics.heightPixels/4);
        switch(verticalRegion){
            case 0:
                return 0;
            case 1:
            case 2:
                if(point.getPositionX()< mDisplayMetrics.widthPixels/2)
                    return 2;
                else
                    return 3;
            case 3:
                return 1;
            default:
                return 100;
        }
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
        sme.Active = true;
        sme.StartPoint.setPosition(x,y);
        sme.EndPoint.setPosition(x,y);
        sme.Region = determineRegion(sme.StartPoint);
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
            mEvents.get(id).EndPoint.setPosition(x, y);
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
        if(Point.distance(sme.StartPoint, sme.EndPoint) < MIN_SWIPE_DISTANCE || sme.Region>= mNumRegions)
            return;

        Compass swipeDirection = Compass.getDirection(sme.StartPoint, sme.EndPoint);

        long swipeTime = System.currentTimeMillis();

        if(mListener!=null)
            mListener.onRegionSwipe(sme.Region, swipeDirection, swipeTime);

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
        mEvents.get(id).Active=false;
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
     *
     * @param canvas Canvas to be draw. The canvas should be the as large as the touvh bounds of the device.
     */
    public void drawOnCanvas(Canvas canvas){
        float x1,x2,y1,y2;
        float circleRadius = 15 *mDisplayMetrics.density;
        float margins = 5 * mDisplayMetrics.density;
        int width = mDisplayMetrics.widthPixels;
        int height = mDisplayMetrics.heightPixels;

        Paint black = new Paint();
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);


        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStrokeWidth(5 * mDisplayMetrics.density);

        Paint gray = new Paint();
        gray.setColor(Color.GRAY);
        gray.setAlpha(100);

        for(int i = 0; i< mEvents.size(); i++){
            SwipeMotionEvent event = mEvents.get(i);
            if(event!= null && event.Active) {

                x1 = (float) event.StartPoint.getPositionX();
                y1 = (float) event.StartPoint.getPositionY();
                x2 = (float) event.EndPoint.getPositionX();
                y2 = (float) event.EndPoint.getPositionY();

                Compass direction = Compass.getDirection(x1,y1,x2,y2);

                canvas.drawCircle(x1, y1, circleRadius, black);
                canvas.drawCircle(x2, y2, circleRadius, black);

                switch (direction) {
                    case NORTH:
                    case SOUTH:
                        canvas.drawLine(x1, y1, x1, y2, blue);
                        break;
                    case EAST:
                    case WEST:
                        canvas.drawLine(x1, y1, x2, y1, blue);
                        break;
                }

                switch (event.Region){
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
     * Container class that stores information for detecting swiping motions.
     */
    private class SwipeMotionEvent{

        public Point StartPoint;
        public Point EndPoint;
        public boolean Active;
        public int Id;
        public int Region;

        /**
         * Create a SwipeMotionEvent. the EndPosition is given a copy of Start position for initialization.
         *
         * @param id          the unique id for the motionEvent. Their will never bee two active motion
         *                    events with the same id
         * @param startPoint  the initial position of the swipe.
         * @param active      determines if the swipe is in progress, meaning it has not completed yet.
         */
        SwipeMotionEvent(int id, Point startPoint, boolean active){
            Id = id;
            StartPoint = startPoint;
            EndPoint = startPoint.makeCopy();
            Active = active;
            Region = 0;
        }

        @Override
        public String toString() {
            return  "id = " + Id+" \n " + "Active = " + Active +" \n " + StartPoint.toString() +" \n " + EndPoint.toString();
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