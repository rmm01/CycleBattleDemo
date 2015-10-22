package com.yckir.cyclebattledemo;

/**
 * Used to identify the four compass directions.
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
}