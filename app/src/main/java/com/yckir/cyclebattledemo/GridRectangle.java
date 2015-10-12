package com.yckir.cyclebattledemo;


/**
 *   A rectangle that exists on the xy plane.
 *
 */
public class GridRectangle {
    public static final String TAG = "RECTANGLE";
    private Point mCenter;
    private double mWidth;
    private double mHeight;
    private double mTop;
    private double mBottom;
    private double mLeft;
    private double mRight;


    /**
     *  Initializes the rectangle size and position on the xy plane.
     *  It is the users responsibility to make sure that the height and width are non negative.
     *
     * @param centerPoint The point that will be the center of the rectangle.
     * @param w The width of the rectangle.
     * @param h The height of the rectangle.
     */
    public GridRectangle(Point centerPoint, double w, double h){
        mWidth =w;
        mHeight=h;
        mCenter= centerPoint.makeCopy();
        mTop=mCenter.getPositionY()+h/2;
        mBottom=mCenter.getPositionY()-h/2;
        mLeft=mCenter.getPositionX()-w/2;
        mRight=mCenter.getPositionX()+w/2;
    }


    /**
     * Initializes the rectangle size and position on the x-y plane.
     * It is the users responsibility to make sure that the height and width are non negative.
     *
     * @param centerX The center of the rectangle on the x axis.
     * @param centerY The center of the rectangle on the Y axis.
     * @param w The width of the rectangle.
     * @param h The height of the rectangle.
     */
    public GridRectangle(double centerX,double centerY, double w, double h){
        mWidth =w;
        mHeight=h;
        mCenter= new Point(centerX, centerY);
        mTop=mCenter.getPositionY()+mHeight/2;
        mBottom=mCenter.getPositionY()-mHeight/2;
        mLeft=mCenter.getPositionX()-mWidth/2;
        mRight=mCenter.getPositionX()+mWidth/2;
    }


    /**
     * gets the center point of the rectangle
     * @return A newly created copy of the center of the rectangle.
     */
    public Point getCenter(){
        return mCenter.makeCopy();
    }


    /**
     * @return The point at the top left corner of the rectangle.
     */
    public Point getTopLeftCoordinate(){
        return new Point(mCenter.getPositionX()-mWidth/2,mCenter.getPositionY()+mHeight/2);
    }


    /**
     * @return The point at the top right corner of the rectangle.
     */
    public Point getTopRightCoordinate(){
        return new Point(mCenter.getPositionX()+Math.ceil(mWidth / 2),mCenter.getPositionY()+mHeight/2);
    }


    /**
     * @return The point at the bottom left corner of the rectangle.
     */
    public Point getBottomLeftCoordinate(){
        return new Point(mCenter.getPositionX()-mWidth/2,mCenter.getPositionY()-Math.ceil(mHeight / 2));
    }


    /**
     * @return The point at the bottom left corner of the rectangle.
     */
    public Point getBottomRightCoordinate(){
        return new Point(mCenter.getPositionX()+Math.ceil(mWidth / 2),mCenter.getPositionY()-Math.ceil(mHeight / 2));
    }


    /**
     * @return The width of the rectangle.
     */
    public double getWidth() {
        return mWidth;
    }


    /**
     * @return The height of the rectangle.
     */
    public double getHeight() {
        return mHeight;
    }


    /**
     *
     * @return The x coordinate of the left side of the rectangle.
     */
    public double getLeft() {
        return mLeft;
    }


    /**
     *
     * @return The x coordinate of the right side of the rectangle.
     */
    public double getRight() {
        return mRight;
    }


    /**
     *
     * @return The Y coordinate of the Top side of the rectangle.
     */
    public double getTop() {
        return mTop;
    }


    /**
     *
     * @return The Y coordinate of the bottom side of the rectangle.
     */
    public double getBottom() {
        return mBottom;
    }


    /**
     * Sets the center of the rectangle to be at a new <Code>Point</Code>.
     * @param point the position that will be the center of the rectangle
     */
    public void setCenter(Point point){
        mCenter=point.makeCopy();
    }


    /**
     * Sets the x and y coordinate of the center of the rectangle.
     *
     * @param x the y coordinate of the center of the rectangle.
     * @param y the x coordinate of the center of the rectangle.
     */
    public void setCenter(double x, double y){
        mCenter.setPositionY(y);
        mCenter.setPositionX(x);
    }



    /**
     * @param width The new width of the rectangle.
     */
    public void setWidth(double width) {
        mWidth = width;
    }


    /**
     * @param height The new height of the rectangle.
     */
    public void setHeight(double height) {
        mHeight = height;
    }


}
