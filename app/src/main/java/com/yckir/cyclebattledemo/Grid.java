package com.yckir.cyclebattledemo;


public class Grid {
    private int mNumTilesX;
    private int mNumTilesY;
    public static final String TAG="GRID";
    public static final double GRID_TILE_LENGTH=1.0;

    public Grid(int shortLength, int longLength) {
        mNumTilesX = shortLength;
        mNumTilesY = longLength;
    }


    public int getNumTilesX() {return mNumTilesX;}
    public int getNumTilesY() {return mNumTilesY;}

    //TODO create out of bounds method
}
