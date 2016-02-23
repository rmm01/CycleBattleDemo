package com.yckir.cyclebattledemo.views.gameSurfaceView;

import android.os.Bundle;

import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Compass;
import com.yckir.cyclebattledemo.utility.Point;

/**
 * A line that exists on a 2d grid. Records the time when this line was created. The line has a
 * thickness. Imagine a  horizontal line on a paper and a square pencil. You trace the line on the
 * paper by placing the center of the pencil on the start of the line and trace until the end of
 * the line. This causes the height to be thickness and width to be thickness + lineLength. Width
 * does not equal lineLength unless the thickness is zero
 */
public class GridLine implements Grid.GridObject {
    public  static final String   TAG               =   "GRID_LINE";
    private static final String   DIRECTION_KEY     =   TAG + ":DIRECTION";
    private static final String   LINE_LENGTH_KEY   =   TAG + ":LINE_LENGTH";
    private static final String   END_TIME_KEY      =   TAG + ":END_TIME";
    private static final String   X_KEY             =   TAG + ":X";
    private static final String   Y_KEY             =   TAG + ":Y";

    private Point mStartPoint;
    private Point mCenterPoint;
    private Point mEndPoint;
    private Compass mDirection;
    private double mLineLength;
    private double mLineThickness;
    private double mWidth;
    private double mHeight;
    private double mLeft;
    private double mRight;
    private double mTop;
    private double mBottom;
    private long mEndTime;


    /**
     * Constructs a GridLine at the specified xy point.
     *
     * @param startX the x position of the start of the line.
     * @param startY the y position of the start of the line.
     * @param length the length of the line.
     * @param thickness how thick the line is
     * @param endTime the time in milliseconds for when the line is created
     * @param direction the direction the line is traveling in
     */
    public GridLine(double startX, double startY, double length ,double thickness,long endTime, Compass direction){
        mStartPoint = new Point(startX,startY);
        mLineLength = length;
        if(thickness!=0)
            mLineThickness = thickness/2;
        else
            mLineLength=0;
        mDirection = direction;
        mEndTime=endTime;
        init();
    }


    /**
     * Constructs a GridLine at the specified point.
     *
     * @param start the start point of the line
     * @param length the length of the line.
     * @param thickness how thick the line is
     * @param endTime the time in milliseconds for when the line is created
     * @param direction the direction the line is traveling in
     */
    public GridLine(Point start, double length ,double thickness, long endTime, Compass direction){
        mStartPoint = start.makeCopy();
        mLineLength = length;
        if(thickness!=0)
            mLineThickness = thickness/2;
        else
            mLineLength=0;
        mDirection = direction;
        mEndTime=endTime;
        init();
    }


    /**
     * Determines the end and center point of the line. Also determines teh width and height of
     * the line, this include into accounts the thickness. Sets the left, right top and bottom
     * coordinates of the line.
     */
    private void init(){
        mEndPoint = Compass.moveIndirection(mStartPoint.getPositionX(), mStartPoint.getPositionY(), mLineLength, mDirection);
        mCenterPoint = Compass.moveIndirection(mStartPoint.getPositionX(), mStartPoint.getPositionY(), mLineLength /2,mDirection);
        mWidth = Math.abs(mStartPoint.getPositionX() - mEndPoint.getPositionX()) + 2 * mLineThickness;
        mHeight = Math.abs(mStartPoint.getPositionY() - mEndPoint.getPositionY() ) + 2 * mLineThickness;


        if(mDirection == Compass.SOUTH || mDirection == Compass.EAST  ) {
            mLeft = mStartPoint.getPositionX() - mLineThickness;
            mBottom = mStartPoint.getPositionY() - mLineThickness;
            mRight = mEndPoint.getPositionX() + mLineThickness;
            mTop = mEndPoint.getPositionY() + mLineThickness;
        } else{
            mLeft = mEndPoint.getPositionX() - mLineThickness;
            mBottom = mEndPoint.getPositionY() - mLineThickness;
            mRight = mStartPoint.getPositionX() + mLineThickness;
            mTop = mStartPoint.getPositionY() + mLineThickness;
        }
    }


    @Override
    public double getLeft() {
        return mLeft;
    }



    @Override
    public double getTop() {
        return mTop;
    }



    @Override
    public double getRight() {
        return mRight;
    }



    @Override
    public double getBottom() {
        return mBottom;
    }



    @Override
    public double getWidth() {
        return mWidth;
    }



    @Override
    public double getHeight() {
        return mHeight;
    }



    @Override
    public Point getCenter() {
        return mCenterPoint;
    }


    /**
     * @return the time the line was created.
     */
    public long getEndTime() {
        return mEndTime;
    }


    /**
     * Gets the length of the line. This does not include the thickness
     *
     * @return the length of the line
     */
    public double getLineLength() {
        return mLineLength;
    }


    /**
     * @return the direction the line is traveling
     */
    public Compass getDirection(){return mDirection;}


    /**
     * Gets the point furthest away form the start point. this does not include thickness
     * @return the point furthest away form the start point.
     */
    public Point getEndPoint(){return mEndPoint.makeCopy();}


    /**
     * Gets the start point of the line. this does not include thickness
     *
     * @return the start point of the line
     */
    public Point getStartPoint(){return mStartPoint.makeCopy();}


    /**
     * @param newDirection the new direction that the line will be facing
     */
    public void changeDirection(Compass newDirection){
        mDirection=newDirection;
        init();
    }


    /**
     *
     * @param newDirection the new direction that the line will be facing
     * @param newLength the new length of the line
     */
    public void changeDirection(Compass newDirection,double newLength){
        mDirection=newDirection;
        mLineLength =newLength;
        init();
    }


    /**
     *
     * @param newDirection the new direction that the line will be facing
     * @param newLength the new length of the line
     * @param newEndTime the new creation time of the line
     */
    public void changeDirection(Compass newDirection,double newLength,long newEndTime) {
        mLineLength =newLength;
        mDirection=newDirection;
        mEndTime=newEndTime;
        init();
    }


    /**
     *
     * @param newLength the new length of the line
     */
    public void changeLength(double newLength){
        mLineLength =newLength;
        init();
    }


    /**
     *
     * @param newLength the new length of the line
     * @param newEndTime the new creation time of the line
     */
    public void changeLength(double newLength,long newEndTime){
        mEndTime=newEndTime;
        mLineLength = newLength;
        init();
    }


    /**
     *
     * @param newEndTime the new creation time of the line
     */
    public void changeEndTime(long newEndTime){
        mEndTime=newEndTime;
    }


    /**
     *
     * @return a copy of the line
     */
    public GridLine makeCopy(){
        return new GridLine(mStartPoint,mLineLength,mLineThickness,mEndTime,mDirection);
    }


    /**
     * Save the state of the GridLine onto a bundle.
     *
     * @param bundle the bundle to save the state onto
     */
    public void saveState(Bundle bundle, String id) {
        bundle.putSerializable(DIRECTION_KEY + id, mDirection);
        bundle.putDouble(LINE_LENGTH_KEY + id, mLineLength);
        bundle.putLong(END_TIME_KEY + id, mEndTime);
        bundle.putDouble(X_KEY + id, mStartPoint.getPositionX());
        bundle.putDouble(Y_KEY + id, mStartPoint.getPositionY());
    }


    /**
     * Restore the previous state of the GridLine from a bundle.
     *
     * @param bundle the bundle that has the previous state saved
     */
    public static GridLine restoreState(Bundle bundle, String id, double thickness){
        Compass direction = (Compass) bundle.getSerializable(DIRECTION_KEY + id);
        double lineLength = bundle.getDouble(LINE_LENGTH_KEY + id);
        long endTime = bundle.getLong(END_TIME_KEY + id);
        double x = bundle.getDouble(X_KEY + id);
        double y = bundle.getDouble(Y_KEY + id);
        return new GridLine(x,y,lineLength,thickness,endTime,direction);
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("mDirection",mDirection );
        description.addMember("mLineLength",mLineLength );
        description.addMember("mLineThickness", mLineThickness);
        description.addMember("mWidth", mWidth);
        description.addMember("mHeight", mHeight);
        description.addMember("mLeft", mLeft);
        description.addMember("mRight",mRight );
        description.addMember("mTop",mTop );
        description.addMember("mBottom", mBottom);
        description.addMember("mEndTime", mEndTime);
        description.addClassMember("mStartPoint",mStartPoint);
        description.addClassMember("mCenterPoint",mCenterPoint);
        description.addClassMember("mEndPoint",mEndPoint);

        return description.getString();
    }
}
