package com.yckir.cyclebattledemo;

/**
 * A coordinate that lies on an x-y plane.
 */
public class Point {
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
}
