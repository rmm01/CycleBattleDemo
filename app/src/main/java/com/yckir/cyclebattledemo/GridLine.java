package com.yckir.cyclebattledemo;

/**
 * A line that exists on a 2d grid. Records the time when this line was created. The line has a
 * thickness. Imagine a  horizontal line on a paper and a square pencil. You trace the line on the
 * paper by placing the center of the pencil on the start of the line and trace until the end of
 * the line. This causes the height to be thickness and width to be thickness + lineLength. Width
 * does not equal lineLength unless the thickness is zero
 */
public class GridLine implements Grid.GridObject {

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
}
