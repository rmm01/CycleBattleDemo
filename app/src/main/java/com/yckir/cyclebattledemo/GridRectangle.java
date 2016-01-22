package com.yckir.cyclebattledemo;


import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Point;

/**
 *   A rectangle that exists on the xy plane.
 *
 */
public class GridRectangle implements Grid.GridObject{
    public static final String TAG = "RECTANGLE";
    private Point mCenter;
    private double mWidth;
    private double mHeight;

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
    }


    @Override
    public Point getCenter(){
        return mCenter.makeCopy();
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
    public double getLeft() {
        return mCenter.getPositionX()-mWidth/2;
    }


    @Override
    public double getRight() {
        return mCenter.getPositionX()+mWidth/2;
    }


    @Override
    public double getTop() {
        return mCenter.getPositionY()+mHeight/2;
    }


    @Override
    public double getBottom() {
        return mCenter.getPositionY()-mHeight/2;
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
     * @return  The  center y coordinate.
     */
    public double getX() {
        return mCenter.getPositionX();
    }


    /**
     * @return The  center x coordinate.
     */
    public double getY(){
        return mCenter.getPositionY();
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

    /**
     * @param x The new center y coordinate.
     */
    public void setX(double x) {
        mCenter.setPositionX(x);
    }


    /**
     * @param y The new center x coordinate.
     */
    public void setY(double y){
        mCenter.setPositionY(y);
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("mWidth",mWidth );
        description.addMember("mHeight",mHeight );
        description.addClassMember("mCenter", mCenter);
        return description.getString();

    }
}
