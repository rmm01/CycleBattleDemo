package com.yckir.cyclebattledemo;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import java.util.ArrayList;

/**
 * Keeps track of an object moving in 2d space. The moving object can only move parallel to the x
 * or y axis. The path is recorded by keeping track of the lines along the path. No two connected
 * lines can be parallel to each other. Time moves forward as the path moves.
 * Each line has a start time and an end time. The end time is the time in milliseconds when the
 * path was at the last point on the line. The start time is the time in milliseconds when the
 * path was at the first point on the line.
 */
public class LinePath {
    public static final String TAG = "LINE_PATH";
    private final double DEFAULT_THICKNESS = 0.1;
    private ArrayList<GridLine> mPathHistory;
    private int mLastLineIndex;
    private boolean mDirectionChanged;
    private long mStartTime;


    /**
     * Constructs a line with length zero at the specified position.
     *
     * @param x the x position of the start of the line
     * @param y the y position of the start of the line
     * @param startTime the start and end time of the line
     * @param direction the direction where the path will move towards
     */
    public LinePath(double x, double y,int startTime, Compass direction){
        mPathHistory = new ArrayList<>();
        mStartTime=0;
        GridLine line = new GridLine(x,y,0,DEFAULT_THICKNESS,startTime,direction);
        mPathHistory.add(line);
        mLastLineIndex=0;
        mDirectionChanged =true;
    }


    /**
     * Changes the length and endTime of the last line in the path.
     *
     * @param newLineLength The new length of the last line of the Path. The path will not change
     *                      if this value is less than zero.
     * @param endTime in milliseconds when the path was at the last point on the line. The path
     *                    will not change if the endTime is before the start time of the line
     */
    public void movePath(double newLineLength, long endTime){
        //check if distance is positive
        if(newLineLength<0){
            Log.e(TAG, "error updating the current position, distance is negative ");
            return ;
        }
        GridLine  lastLine = mPathHistory.get(mLastLineIndex);

        //check if time increased
        if(endTime < lastLine.getEndTime()) {
            Log.e(TAG, "error updating the current position,  the time did not increase. "
                    + endTime + ", is less than " + lastLine.getEndTime());
            return ;
        }

        mDirectionChanged =false;
        lastLine.changeLength(newLineLength, endTime);
    }


    /**
     * Makes a new line with zero length at the last point on the last line with a new direction.
     * The start and end time of the new line is the endTime of the last line. The direction will
     * not change if the path would have changed directions twice without moving.
     *
     * @param newDirection the new direction of the path. This direction must not be parallel to the
     *                     current last line or the direction will not change.
     */
    public void changePathDirection(Compass newDirection){
        if(mDirectionChanged){
            Log.e(TAG, "error changing direction: cannot change the direction twice in a row without moving");
            return ;
        }
        mDirectionChanged =true;
        GridLine previousLastLine = mPathHistory.get(mLastLineIndex);

        if(! Compass.isPerpendicular(previousLastLine.getDirection(), newDirection) ){
            Log.e(TAG, "error changing direction: directions are not perpendicular");
            return ;
        }

        GridLine newLastLine = new GridLine(previousLastLine.getEndPoint(),0,DEFAULT_THICKNESS,
                previousLastLine.getEndTime(), newDirection);

        mPathHistory.add(newLastLine);
        mLastLineIndex++;

    }


    /**
     * Bends the last line at the specified position. Imagine you arm is
     * a line and want to bend at the wrist. this method will bend the arm at the
     * wrist 90 degrees, resulting in two lines.
     *
     * @param newDirection The new direction of the path. This direction must not be parallel to the
     *                     current last line or false will be returned.
     * @param bendTime The time in milliseconds when the line should have bended. If this time is
     *                less than the start time of the last line, false is returned.
     * @param bendLength The position on the current line when the direction should have changed. If
     *              this is less than zero, false if returned.
     * @return true if the parameters were proper, false otherwise.
     */
    public boolean bendLastLine(Compass newDirection, long bendTime, double bendLength){
        GridLine  oldLastLine = mPathHistory.get(mLastLineIndex);
        long endTime = oldLastLine.getEndTime();
        double oldLineLength=oldLastLine.getLineLength();
        long startTime = getLineStartTime(mLastLineIndex + 1);
        double excessDistance = oldLineLength-bendLength;

        Log.d(TAG,"bendLastLine should not be called, dir = " + newDirection + ", t = " + bendTime +
                ", l = " + bendLength);

        Log.d(TAG,"bendLastLine should not be called, endTime = " + endTime + ", oldLineLength = " +
                oldLineLength + ", startTIme = " + startTime + ", excessDistance = " + excessDistance);

        if( !Compass.isPerpendicular( newDirection, oldLastLine.getDirection() ) ){
            Log.d(TAG, "error in bendLastLine: direction are not perpendicular");
            return false;
        }
        if( bendTime < startTime || bendTime > endTime ){
            if(bendTime<startTime)
            Log.d(TAG, "error in bendLastLine: you are attempting to change the " +
                    "direction before the line was created, bendTime = " + bendTime + "< " + startTime );
            if(bendTime>endTime)
                Log.d(TAG, "error in bendLastLine: you are attempting to change the " +
                        "direction in future, bendTime = " + bendTime + "> " + endTime );
            return false;
        }
        if(bendLength <= 0 || bendLength > oldLineLength) {
            Log.d(TAG, "error in bendLastLine: bendLength is less or greater than the current line");
            return false;
        }

        oldLastLine.changeLength(bendLength,bendTime);
        changePathDirection(newDirection);
        if(excessDistance>0)
            movePath(excessDistance, endTime);
        mDirectionChanged =false;
        return true;
    }


    /**
     * Moves the end line and changes the direction.
     *
     * @param newDirection The new direction of the path. This direction must not be parallel to the
     *                     current last line or false will be returned.
     * @param endTime The time in milliseconds when last line turns If this time is
     *                less than the start time of the last line, false is returned.
     * @param length the length of the last line before it changes directions. If
     *              this is less than zero, false if returned.
     * @return true if the parameters were proper, false otherwise.
     */
    public boolean moveAndChangeDirection(Compass newDirection, long endTime, double length){
        GridLine  oldLastLine = mPathHistory.get(mLastLineIndex);
        long startTime = getLineStartTime(mLastLineIndex + 1);

        if( !Compass.isPerpendicular( newDirection, oldLastLine.getDirection() ) ){
            Log.d(TAG, "error in moveAndChangeDirection: direction are not perpendicular");
            return false;
        }
        if( endTime < startTime  ){
            Log.d(TAG, "error in moveAndChangeDirection: you are attempting to change the " +
                    "direction before the line was created, bendTime = " + endTime + "< " + startTime );
            return false;
        }
        if(length <= 0 ) {
            Log.d(TAG, "error in moveAndChangeDirection: length is less or greater than the current line");
            return false;
        }

        oldLastLine.changeLength(length,endTime);
        changePathDirection(newDirection);
        mDirectionChanged =true;
        return true;
    }


    /**
     * get the specified line number
     * @param lineNumber the number of the line in the path
     * @return the Grid line specified, null if line number does not exist.
     */
    public GridLine getLine(int lineNumber){
        if(lineNumber < 1 && lineNumber > mLastLineIndex+1)
            return null;
        return mPathHistory.get(lineNumber-1).makeCopy();

    }


    /**
     * @return the number of lines in the path
     */
    public int getNumLines(){
        return mLastLineIndex+1;
    }


    /**
     * get the start time of the specified line
     * @param lineNumber the number of the line in the path
     * @return the starttime of the Grid line specified, -1 if line number does not exist.
     */
    public long getLineStartTime(int lineNumber){
        if(lineNumber < 1 && lineNumber > mLastLineIndex+1)
            return -1;
        if(lineNumber==1)
            return mStartTime;
        return mPathHistory.get(lineNumber-2).getEndTime();
    }


    /**
     * empty
     * @param canvas null
     * @param paint null
     */
    public void drawPath(Canvas canvas,Paint paint){}


    /**
     * @return the coordinate of the last point on the path
     */
    public Point getLastPoint(){
        return mPathHistory.get(mLastLineIndex).getEndPoint();
    }
}
