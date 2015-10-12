package com.yckir.cyclebattledemo;

/**
 * A square tile with a length that cant be changed once initialized.
 *
 * @param <N> a generic for a number data type, Double,Integer, etc.
 */
public class Tile<N> {

    private final N mLength;

    /**
     * constructs an immutable Tile
     * @param length the length of the square tile
     */
    public Tile(N length){
        mLength=length;
    }

    /**
     * @return the length of the tile
     */
    public N getLength() {
        return mLength;
    }

}
