package com.yckir.cyclebattledemo;


import android.util.Log;

import java.util.ArrayList;


/**
 * Keeps record of the path of a moving object.
 * A path is recorded using vertex points.
 * Their are 3 types of vertex points.
 * The start vertex is the first point in the path.
 * The end vertex is the last point in the path.
 * A pivot vertex is a point on the path where the direction changes.
 * A path contains a start vertex, a end vertex and zero or more pivot vertex points.
 * Time is recorded at each path point and must be increasing along the path vertex points.
 * The path is built by moving and changing its direction of the end vertex.
 *
 */
public class Path {

    public static final String TAG = "PATH";
    private ArrayList<PathVertex> PathHistory;
    private int mEndIndex;
    private boolean directionChanged;


    /**
     * Creates the start and end vertex.
     * The path size is two after the constructor completes.
     * After creation, the End vertex must move before it can change direction.
     *
     * @param x         the x position of the start and end vertex
     * @param y         the y position of the start and end vertex
     * @param direction the direction the start and end vertex
     */
    public Path(double x, double y,int startTime, Compass direction) {
        PathHistory = new ArrayList<>();
        PathVertex pathVertex = new PathVertex(x, y, startTime, direction);
        PathHistory.add(pathVertex);
        PathHistory.add(pathVertex.makeCopy());
        mEndIndex = 1;
        directionChanged=true;
    }


    /**
     * Move the end Vertex of the path in its current direction and update
     * the current time. The path size remains the same.
     *
     * @param distanceTraveled the distance between the end vertex and its previous vertex.
     *                         If the value is negative, the Vertex will not be moved
     *                         and an error will be logged.
     * @param currentTime The current time in milliseconds for the end vertex. If this
     *                    value is less than the previous vertex, the Vertex will not be moved
     *                         and an error will be logged.
     *
     */
    public void moveEndVertex(double distanceTraveled, long currentTime) {
        directionChanged=false;

        PathVertex endVertex = PathHistory.get(mEndIndex);
        PathVertex lastPivotVertex = PathHistory.get(mEndIndex - 1);

        //check if time increased
        if(currentTime < lastPivotVertex.getTime()) {
            Log.e(TAG, "error updating the current position,  the time is before the last direction change. "
                    + currentTime + ", is less than " + lastPivotVertex.getTime());
            return ;
        }

        //check if distance is positive
        if(distanceTraveled<0){
            Log.e(TAG,"error updating the current position, distance is negative ");
            return ;
        }

        endVertex.setTime(currentTime);

        //move in specified direction
        Point newPosition = Compass.moveIndirection(lastPivotVertex.getX(), lastPivotVertex.getY(),
                distanceTraveled, endVertex.getDirection());

        endVertex.setX(newPosition.getPositionX());
        endVertex.setY(newPosition.getPositionY());

    }


    /**
     * Change the direction of the end Vertex. This causes the current end vertex to become a pivot
     * vertex and a new end vertex is created at the same position, but both with the new direction.
     * This results in the path increasing its size by one. If changing the direction would result
     * ib the direction being changed twice in a row without any movement, an error would be logged
     * and the direction will not change.
     *
     * @param newDirection the new direction that you want the current position to be traveling in.
     *                     If it is invalid to change in this direction(traveling south and
     *                     want to travel north, or traveling south and want to travel south),
     *                     then the direction will not be changed and an error will be logged.
     */
    public void changeEndVertexDirection(Compass newDirection) {
        //if the direction has changed previously without moving, an error will be logged because
        //this will cause the path two travel in an invalid direction
        if(directionChanged){
            Log.e(TAG,"error changing direction: cannot change the direction twice in a row without moving");
            return;
        }
        directionChanged=true;

        PathVertex newCurrentPosition = PathHistory.get(mEndIndex).makeCopy();

        //check if direction changes
        if(!Compass.isPerpendicular(newDirection,newCurrentPosition.getDirection())){
            Log.e(TAG,"error changing direction: directions are not perpendicular");
            return;
        }

        newCurrentPosition.setDirection(newDirection);
        PathHistory.add(newCurrentPosition);
        mEndIndex++;
        PathHistory.get(mEndIndex-1).setDirection(newDirection);
    }


    /**
     * This method is called if their was a delay between when the direction was suppose to change
     * and when the path was notified. In this scenario, you rewind back to the point in time when
     * the direction was suppose to change, you change the direction, and you move forward according
     * to the current time. It is important to note that this method assumes that the delay happened
     * between the last PivotVertex and the EndVertex.
     *
     * @param newDirection the direction that you want to change to
     * @param time the time in milliseconds when the direction change happens
     * @param speed how fast you were traveling
     * @return
     */
    public boolean delayedChangeEndVertexDirection(Compass newDirection, long time,double speed) {



        PathVertex lastPivot = getLastPivotVertex();
        PathVertex currentPosition = PathHistory.get(mEndIndex);

        double dx = currentPosition.getX() - lastPivot.getX();
        double dy = currentPosition.getY() - lastPivot.getY();


        if(!Compass.isPerpendicular(newDirection,currentPosition.getDirection())){
            Log.d(TAG,"error in delayed changing direction: direction are not perpendicular");
            return false;
        }
        if(time<lastPivot.getTime()){
            Log.d(TAG,"error in delayed changing direction: you are attempting to change the " +
                    "direction prior to the last Pivot Vertex");
            return false;
        }
        if (dx != 0 && dy != 0) {
            Log.d(TAG, "error in delayed changing direction: internal logic error");
            return false;
        }

        double oldDisplacement = Math.abs( (dy == 0) ? dx : dy );
        double newDisplacement = ( ( time - lastPivot.getTime() ) ) / 1000.0 * speed;

        double excessDistance = oldDisplacement-newDisplacement;
        long currentTime = currentPosition.getTime();

        //move the current position back to the last pivot
        double oldX=currentPosition.getX();
        double oldY=currentPosition.getY();
        currentPosition.setTime(lastPivot.getTime());
        currentPosition.setX(lastPivot.getX());
        currentPosition.setY(lastPivot.getY());
        Log.v(TAG, "backtrack position " + oldX + "," + oldY + " to " + currentPosition.getX() + "," + currentPosition.getY());

        //move the current position to when the direction change happened
        Log.v(TAG, "moving with d = " + newDisplacement + " and t = " + time);
        moveEndVertex(newDisplacement, time);

        //change the direction
        Log.v(TAG,"Changing direction = " + newDirection );
        changeEndVertexDirection(newDirection);

        //move the current position to account for the delay

        if(excessDistance>0) {
            Log.v(TAG, "moving with excess d = "  + excessDistance + " and t = " + currentTime);
            moveEndVertex(excessDistance, currentTime);
        }

        return true;
    }


    /**
     * @return The number of vertex points in the path. This includes the start and end vertex.
     */
    public int getLength() {
        return mEndIndex + 1;
    }


    /**
     * gets a copy of the path point at the current specified index
     *
     * @param i the index of the desired vertex
     * @return the desired path point, null if index is out of range
     */
    public PathVertex getVertex(int i) {
        if (i >= 0 && i <= mEndIndex)
            return PathHistory.get( i ).makeCopy();
        return null;
    }


    /**
     * gets a copy of the last pivot vertex.
     *
     * @return the last point in the path(currentPosition - 1)
     */
    public PathVertex getLastPivotVertex() {
        return PathHistory.get(mEndIndex - 1).makeCopy();
    }


    /**
     * gets a copy of the the end vertex of the path
     *
     * @return the last vertex in the path
     */
    public PathVertex getEndVertex() {
        return PathHistory.get(mEndIndex).makeCopy();
    }


    /**
     * @return a copy of the point of the end vertex in the path
     */
    public Point getLastPoint() {
        return PathHistory.get(mEndIndex).getPoint();
    }
}