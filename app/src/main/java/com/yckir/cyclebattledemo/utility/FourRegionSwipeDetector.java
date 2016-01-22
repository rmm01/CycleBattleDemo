package com.yckir.cyclebattledemo.utility;

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
    private final static double MIN_FLING_DISTANCE = 0;
    private ArrayList<Point> mEvents;
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
        mEvents = new ArrayList<>(10);
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
            Log.e(TAG,"number of regions is greater that 4, setting numRegions to 1");
            return;
        }
        mNumRegions =numRegions;
    }


    /**
     * Determine which region was swiped based on two points.
     *
     * @param p1 point 1
     * @param p2 point 2
     * @return the region that was swiped
     */
    private int determineRegion(Point p1, Point p2){
        Point centerPoint = Point.centerOfLine(p1, p2);

        int verticalRegion = (int)centerPoint.getPositionY()/(mDisplayMetrics.heightPixels/4);
        switch(verticalRegion){
            case 0:
                return 0;
            case 1:
            case 2:
                if(centerPoint.getPositionX()< mDisplayMetrics.widthPixels/2)
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
     * save the event.
     *
     * @param event to be stored
     */
    private void downAction(MotionEvent event) {
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        //int x = (int)event.getX(index);
        //int y = (int)event.getY(index);
        //Log.v(TAG, "finger pressed: index = " + index + ", id = " + id+ " at " + x + ", " + y);
        mEvents.add(id, new Point(event.getX(index), event.getY(index)));
    }


    /**
     * A swipe has happened at the two points, determine the region, direction, and time of the
     * swipe. Call the listeners onRegionSwipe method if the seipe happened at a valid region.
     *
     * @param p1 point 1
     * @param p2 point 2
     */
    private void onSwipe(Point p1, Point p2) {
        Compass swipeDirection = Compass.getDirection(p1, p2);
        int region = determineRegion(p1, p2);
        if(region>= mNumRegions)
            return;
        long swipeTime = System.currentTimeMillis();

        //Log.v(TAG, " swipe at region " + region + ", swipeDirection = "
        //        + swipeDirection + " at time " + swipeTime);
        if(mListener!=null)
            mListener.onRegionSwipe(region, swipeDirection, swipeTime);
    }


    /**
     * called when an DOWN has been received for an event. Determines if the event was a swipe
     * gesture.
     * @param event event with an action DOWN
     */
    private void upAction(MotionEvent event) {
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        //Log.v( TAG, "finger released: index " + index + " and id " + id );

        Point p1 = mEvents.get(id);
        Point p2 = new Point( event.getX(index), event.getY(index) );
        //Point.logPoints(p1, p2);

        if(Point.distance(p1, p2) < MIN_FLING_DISTANCE)
            return;

        onSwipe(p1, p2);
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
     * Receive a touch event and either save the event if it was a down, or check if it was a
     * swipe if it was an up action. Does nothing of is disabled.
     *
     * @param event a motion event
     */
    public void receiveTouchEvent(MotionEvent event){
        if(mDisabled)
            return;
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                downAction(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                upAction(event);
                break;
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
}