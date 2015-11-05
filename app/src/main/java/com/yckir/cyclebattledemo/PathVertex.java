package com.yckir.cyclebattledemo;


/**
 *  A coordinate in x y plane that points in a direction at a specific time. The Vertex acts as the
 *  center of a square with length 2 * DEFAULT_WIDTH. Also contains drawing edges that are measured
 *  with the GameFrame static SCREEN_TILE.
 */
public class PathVertex {
    private double X;
    private double Y;

    private long Time;
    private Compass Direction;
    private final double DEFAULT_WIDTH=0.1;
    private double mWidth;
    private int mDrawingLeft;
    private int mDrawingRight;
    private int mDrawingTop;
    private int mDrawingBottom;

    /**
     * Constructs a vertex at the specified time, location, and orientation.
     *
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param time the time in milliseconds that this point was at this location
     * @param direction the direction that the vertex is pointing towards
     * @see Compass
     */
    public PathVertex(double x, double y, long time, Compass direction){
        X=x;
        Y=y;
        Time=time;
        Direction=direction;
        mWidth=DEFAULT_WIDTH;
        mDrawingLeft=(int)Tile.convert(Grid.GAME_GRID_TILE, GameFrame.SCREEN_GRID_TILE, X - mWidth);
        mDrawingTop=(int)Tile.convert(Grid.GAME_GRID_TILE, GameFrame.SCREEN_GRID_TILE, Y - mWidth);
        mDrawingRight=(int)Tile.convert(Grid.GAME_GRID_TILE, GameFrame.SCREEN_GRID_TILE, X + mWidth);
        mDrawingBottom=(int)Tile.convert(Grid.GAME_GRID_TILE, GameFrame.SCREEN_GRID_TILE, Y + mWidth);
    }


    /**
     * @return the x y position of the current PathVertex as a Point.
     */
    public Point getPoint(){
        return new Point(X,Y);
    }


    /**
     * @return the current x coordinate
     */
    public double getX() {
        return X;
    }


    /**
     * @return the current y coordinate
     */
    public double getY() {
        return Y;
    }


    /**
     * gets the direction the vertex is pointing towards
     *
     * @return the current direction
     */
    public Compass getDirection() {
        return Direction;
    }


    /**
     * gets the time that this vertex was at its current location
     *
     * @return the time in milliseconds for the vertex
     */
    public long getTime() {
        return Time;
    }


    /**
     * @param x the new x coordinate
     */
    public void setX(double x) {
        X = x;
        mDrawingLeft=(int)Tile.convert(Grid.GAME_GRID_TILE, GameFrame.SCREEN_GRID_TILE, X - mWidth);
        mDrawingRight=(int)Tile.convert(Grid.GAME_GRID_TILE, GameFrame.SCREEN_GRID_TILE, X + mWidth);
    }


    /**
     * @param y the new y coordinate
     */
    public void setY(double y) {
        Y = y;
        mDrawingTop=(int)Tile.convert(Grid.GAME_GRID_TILE, GameFrame.SCREEN_GRID_TILE, Y - mWidth);
        mDrawingBottom=(int)Tile.convert(Grid.GAME_GRID_TILE, GameFrame.SCREEN_GRID_TILE, Y + mWidth);
    }


    /**
     * @param direction the new direction the vertex is pointing towards
     */
    public void setDirection(Compass direction) {
        Direction = direction;
    }


    /**
     * sets the time that this vertex is at its current location
     *
     * @param time time in milliseconds
     */
    public void setTime(long time) {
        Time = time;
    }



    /**
     * @return a copy of the current PathVertex, the time does not change.
     */
    public PathVertex makeCopy(){
        return new PathVertex(X,Y,Time,Direction);
    }


    /**
     * @param pathVertex the vertex whose attributes that will be copied
     */
    public void copyVertex(PathVertex pathVertex){
        X=pathVertex.getX();
        Y=pathVertex.getY();
        Time=pathVertex.getTime();
        Direction=pathVertex.getDirection();
        mDrawingLeft=pathVertex.getDrawingLeft();
        mDrawingTop=pathVertex.getDrawingTop();
        mDrawingRight=pathVertex.getDrawingRight();
        mDrawingBottom=pathVertex.getDrawingBottom();
    }


    /**
     * @return the value of the left edge of the path measured with the GameFrame tile
     */
    public int getDrawingLeft(){return mDrawingLeft;}


    /**
     * @return the value of the right edge of the path measured with the GameFrame tile
     */
    public int getDrawingRight(){return mDrawingRight;}


    /**
     * @return the value of the top edge of the path measured with the GameFrame tile
     */
    public int getDrawingTop(){return mDrawingTop;}


    /**
     * @return the value of the bottom edge of the path measured with the GameFrame tile
     */
    public int getDrawingBottom(){return mDrawingBottom;}
}
