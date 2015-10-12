package com.yckir.cyclebattledemo;

/**
 * A 2D Grid composed of square Tiles. The origin is located
 * at the bottom left corner of the bottom left Tile. It capable
 * of determining if simple shapes are out of bounds inside
 * the Grid.
 */
public class Grid {
    private int mNumTilesX;
    private int mNumTilesY;
    private double mWidth;
    private double mHeight;

    public static final String TAG="GRID";

    private Tile<Integer> mGridTile;


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
        mGridTile=new Tile<>(tileLength);
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
    public Tile getTile(){
        return mGridTile;
    }

    /**
     * @return the length a Grid Tile
     */
    public int getTileLength(){return mGridTile.getLength();}


    /**
     * Determines if the rectangle is completely inside the Grid.
     *
     * @param rect The rectangle that is being checked.
     * @return true if the rectangle is completely inside the grid, false otherwise.
     */
    public boolean OutOfBounds(GridRectangle rect){
        if(rect.getTop()<0||rect.getRight()<0||rect.getLeft()<0||rect.getBottom()<0)
            return false;
        if(rect.getTop()>mHeight||rect.getRight()>mWidth||
                rect.getLeft()>mWidth||rect.getBottom()>mHeight)
            return false;
        return true;
    }


    /**
     * Determines if a Point is inside the Grid.
     *
     * @param p The point that is being checked.
     * @return true if the point is inside the grid, false otherwise.
     */
    public boolean outOfBounds(Point p){
        if( p.getPositionX()<0||p.getPositionX()>mWidth||
                p.getPositionY()<0||p.getPositionY()>mHeight)
            return false;
        return true;

    }
}
