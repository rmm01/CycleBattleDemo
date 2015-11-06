package com.yckir.cyclebattledemo;

/**
 * Used to identify the four compass directions. Java has the origin at the top left corner
 * and y increases as you go downward. Because of this, South and east are traveling on the
 * positive x axis, North and west are traveling on the negative x and y axis.
 *
 */
public enum Compass {
    NORTH,
    SOUTH,
    EAST,
    WEST;


    /**
     *  Compares two direction to check if they point in opposite direction.
     *
     * @param direction1 direction 1
     * @param direction2 direction 2
     * @return true if they point in opposite directions, false otherwise
     */
    public static boolean oppositeDirection(Compass direction1,Compass direction2 ){
        //check if direction is opposite of current direction
        switch (direction1){
            case NORTH:
                return direction2 == Compass.SOUTH;
            case SOUTH:
                return direction2==Compass.NORTH;
            case WEST:
                return direction2==Compass.EAST;
            case EAST:
                return direction2==Compass.WEST;
        }
        return false;
    }


    /**
     * Moves a coordinate in the specified direction.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param distanceTraveled the distance to move
     * @param direction the direction to move the coordinate. EAST and SOUTH are on the positive axis,
     *                  WEST and NORTH are on the negative axis
     * @return the coordinate point the has moved in the specified direction
     */
    public static Point moveIndirection(double x, double y, double distanceTraveled,Compass direction){
        switch (direction) {
            case NORTH:
                y -= distanceTraveled;
                break;
            case SOUTH:
                y += distanceTraveled;
                break;
            case EAST:
                x += distanceTraveled;
                break;
            case WEST:
                x -= distanceTraveled;
                break;
        }
        return new Point(x,y);
    }


    /**
     * Given two point p1 and p2, determines the direction that point p2 is relative to p1
     *
     * @param x1 x coordinate of point 1
     * @param y1 y coordinate of point 1
     * @param x2 x coordinate of point 2
     * @param y2 y coordinate of point 2
     * @return the direction that p2 is relative to p1
     */
    public static Compass getDirection(double x1, double y1, double x2, double y2){
        double dx= x1-x2;
        double dy= y1-y2;

        if( dx > 0 && dx >= Math.abs(dy) )
            return Compass.WEST;
        else if( dx < 0 && Math.abs(dx) >= Math.abs(dy) )
            return Compass.EAST;
        else if( dy > 0 && dy >= Math.abs( dx ) )
            return Compass.NORTH;
        else
            return Compass.SOUTH;
    }


    /**
     * Given two point p1 and p2, determines the direction that point p2 is relative to p1
     *
     * @param p1 point 1
     * @param p2 point 2
     * @return the direction that p2 is relative to p1
     */
    public static Compass getDirection(Point p1, Point p2){
        return getDirection( p1.getPositionX(), p1.getPositionY(), p2.getPositionX(), p2.getPositionY());
    }


    /**
     * Determines if two direction are perpendicular, meaning one points east/west and the other is
     * north/south.
     *
     * @param direction1 direction to be tested
     * @param direction2 direction to be tested
     * @return true if two directions point in perpendicular directions
     */
    public static boolean isPerpendicular(Compass direction1, Compass direction2){
        return (direction1 != direction2) &&
                ( ! oppositeDirection(direction1,direction2));
    }
}