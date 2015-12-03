package com.yckir.cyclebattledemo;

import android.util.Log;

/**
 * A coordinate that lies on an x-y plane.
 */
public class Point {
    public static final String TAG = "POINT";
    private double X;
    private double Y;


    /**
     * Initializes the position of the <Code>Point</Code>
     *
     * @param x The position on the x axis.
     * @param y The position on the Y axis.
     */
    public Point(double x, double y){
        X=x;
        Y=y;
    }


    /**
     * @return The current x axis position.
     */
    public double getPositionX() {
        return X;
    }


    /**
     * @return The current y axis position.
     */
    public double getPositionY() {
        return Y;
    }


    /**
     * @param x  The new x axis position.
     */
    public void setPositionX(double x) {
        X = x;
    }


    /**
     * @param y The new y axis position.
     */
    public void setPositionY(double y) {
        Y = y;
    }


    /**
     * Set the new x,y position of current <Code>Point</Code>.
     *
     * @param x The new X axis position
     * @param y The new y axis position
     */
    public void setPosition(double x, double y){
        X= x;
        Y= y;
    }


    /**
     * Set the x,y coordinate to be the same as another <Code>Point</Code>.
     *
     * @param point The <Code>Point</Code> that will have its x,y position copied.
     */
    public void setPosition(Point point){
        X= point.getPositionX();
        Y=point.getPositionY();
    }


    /**
     * Create a  copy of the <Code>Point</Code>.
     *
     * @return A new instance of the current <Code>Point</Code>.
     */
    public Point makeCopy(){
        return new Point(X,Y);
    }


    /**
     * Returns the larger value between the absolute value of dx and dy.
     *
     * @param p1 point 1
     * @param p2 point 2
     * @return the larger of the distance between the two points in x or y direction
     */
    public static double delta(Point p1, Point p2){
        double x1 = p1.getPositionX();
        double x2 = p2.getPositionX();
        double y1 = p1.getPositionY();
        double y2 = p2.getPositionY();
        return  Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }


    public static Point centerOfLine(Point p1, Point p2){
        double x1 = p1.getPositionX();
        double x2 = p2.getPositionX();
        double y1 = p1.getPositionY();
        double y2 = p2.getPositionY();


        double cx = (x1 + x2)/2;
        double cy = (y1 + y2)/2;
        return  new Point(cx,cy);
    }



    public static double distance(Point p1, Point p2){
        double x1 = p1.getPositionX();
        double x2 = p2.getPositionX();
        double y1 = p1.getPositionY();
        double y2 = p2.getPositionY();
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }


    /**
     * Logs the x and y position of the pwo points in integer form.
     *
     * @param p1 point 1
     * @param p2 point 2
     */
    public static void logPoints(Point p1, Point p2){
        double x1 = p1.getPositionX();
        double x2 = p2.getPositionX();
        double y1 = p1.getPositionY();
        double y2 = p2.getPositionY();
        Log.v(TAG, "p1: " + (int) x1 + ", " + (int) y1 + ",  p2: " + (int) x2 + ", " + (int) y2 + ", ");
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("X",X);
        description.addMember("Y",Y);
        return description.getString();
    }
}
