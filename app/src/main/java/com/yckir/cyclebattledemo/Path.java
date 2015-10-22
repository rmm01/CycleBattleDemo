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
        PathVertex previousVertex = PathHistory.get(mEndIndex -1);

        //check if time increased
        if(previousVertex.getTime()>currentTime) {
            Log.e(TAG, "error updating the current position, the time did not increase ");
            return ;
        }

        //check if distance is positive
        if(distanceTraveled<0){
            Log.e(TAG,"error updating the current position, distance is negative ");
            return ;
        }

        endVertex.setTime(currentTime);

        //move in specified direction
        Point newPosition = Compass.moveIndirection(previousVertex.getX(),previousVertex.getY(),
                distanceTraveled,endVertex.getDirection());

        endVertex.setX(newPosition.getPositionX());
        endVertex.setY(newPosition.getPositionY());

    }


    /**
     * Change the direction of the end Vertex. This causes the current end vertex to become a pivot
     * vertex and a new end vertex is created at the same position, but with a different direction.
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
            Log.e(TAG,"error changing direction: cannot change the direction twice in a row");
            return;
        }
        directionChanged=true;

        PathVertex newCurrentPosition = PathHistory.get(mEndIndex).makeCopy();

        //check if direction changes
        if(newCurrentPosition.getDirection()==newDirection){
            Log.e(TAG,"error changing direction: new direction = current direction");
            return;
        }

        //check if direction is opposite of current direction
        if(Compass.oppositeDirection(newCurrentPosition.getDirection(),newDirection)){
            Log.e(TAG,"error changing direction:" +newDirection+ "is opposite of current direction");
            return;
        }

        newCurrentPosition.setDirection(newDirection);
        PathHistory.add(newCurrentPosition);
        mEndIndex++;
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
        if (i > 0 && i <= mEndIndex)
            return PathHistory.get(i).makeCopy();
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