package com.yckir.cyclebattledemo;

import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Point;
import com.yckir.cyclebattledemo.utility.Tile;

/**
 * A 2D Grid composed of square Tiles. The origin is located
 * at the bottom left corner of the bottom left Tile. It capable
 * of determining if simple shapes are out of bounds inside
 * the Grid.
 */
public class Grid {
    public static final String TAG  =   "GRID";

    public static Tile<Integer> GAME_GRID_TILE =new Tile<>(1);
    private int mNumTilesX;
    private int mNumTilesY;
    private double mWidth;
    private double mHeight;


    /**
     * Sets the number of tiles in the x and y direcrion.
     *
     * @param numTilesX number of tiles in the X direction
     * @param numTilesY number of tiles in the Y direction
     * @param tileLength the length of a grid tile
     */
    public Grid(int numTilesX, int numTilesY,int tileLength) {
        mNumTilesX = numTilesX;
        mNumTilesY = numTilesY;
        mWidth=tileLength*numTilesX;
        mHeight=tileLength*numTilesY;
        GAME_GRID_TILE =new Tile<>(tileLength);
    }


    /**
     * @return The number of tiles in the X direction.
     */
    public int getNumTilesX() {return mNumTilesX;}


    /**
     * @return The number of tiles in the Y direction.
     */
    public int getNumTilesY() {return mNumTilesY;}


    /**
     * @return The combined length of all the tiles in the X direction.
     */
    public double getWidth(){
        return mWidth;
    }


    /**
     * @return The combined length of all the tiles in the Y direction.
     */
    public double getHeight(){
        return mHeight;
    }


    /**
     * returns a reference to the Tile that a grid is composed of
     * @return an immutable reference the Grids Tile
     */
    public Tile<Integer> getTile(){
        return GAME_GRID_TILE;
    }


    /**
     * @return the length a Grid Tile
     */
    public int getTileLength(){return GAME_GRID_TILE.getLength();}


    /**
     * Determines if the rectangle is completely inside the Grid.
     *
     * @param rect The rectangle that is being checked.
     * @return true if the rectangle is completely inside the grid, false otherwise.
     */
    public boolean OutOfBounds(GridObject rect){
        boolean result = false;
        if(rect.getTop()<0||rect.getRight()<0||rect.getLeft()<0||rect.getBottom()<0)
            result = true;
        if(rect.getTop()>mHeight||rect.getRight()>mWidth||
                rect.getLeft()>mWidth||rect.getBottom()>mHeight)
            result = true;
        return result;
    }


    /**
     * Determines if a Point is inside the Grid.
     *
     * @param p The point that is being checked.
     * @return true if the point is inside the grid, false otherwise.
     */
    public boolean outOfBounds(Point p){
        boolean result = true;
        if( p.getPositionX()<0||p.getPositionX()>mWidth||
                p.getPositionY()<0||p.getPositionY()>mHeight)
            result =  false;
        return result;

    }


    /**
     * Determines if two rectangular objects overlap
     *
     * @param rect1 object 1
     * @param rect2 object 2
     * @return true if the objects overlap, false otherwise.
     */
    public static boolean overlap(GridObject rect1, GridObject rect2){
        // code based on http://www.geeksforgeeks.org/find-two-rectangles-overlap/
        // If one rectangle is on left side of other
        if (rect1.getLeft() > rect2.getRight() || rect2.getLeft() > rect1.getRight())
            return false;

        // If one rectangle is above other
        if (rect1.getTop() < rect2.getBottom() || rect2.getTop() < rect1.getBottom())
            return false;

        return true;
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("mNumTilesX", mNumTilesX);
        description.addMember("mNumTilesY", mNumTilesY);
        description.addMember("mWidth", mWidth);
        description.addMember("mHeight", mHeight);
        description.addClassMember("GAME_GRID_TILE", GAME_GRID_TILE);
        return description.getString();
    }

    /**
     * a object with a rectangular area.
     */
    public interface GridObject{

        /**
         *
         * @return The x coordinate of the left side of the rectangle.
         */
        double getLeft();


        /**
         *
         * @return The x coordinate of the right side of the rectangle.
         */
        double getRight();
        

        /**
         *
         * @return The Y coordinate of the Top side of the rectangle.
         */
        double getTop();


        /**
         *
         * @return The Y coordinate of the bottom side of the rectangle.
         */
        double getBottom();


        /**
         * @return The width of the rectangle.
         */
        double getWidth();


        /**
         * @return The height of the rectangle.
         */
        double getHeight();


        /**
         * gets the center point of the rectangle
         * @return A newly created copy of the center of the rectangle.
         */
        Point getCenter();
    }
}
