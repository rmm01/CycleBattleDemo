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


    /**
     * Converts a value that is measured in tile1 units, into tile2 units.
     *
     * @param tileA a unit of measure, the value is measured currently with this
     * @param tileB the unit of measure you want the value to be represented in.
     * @param valueA a number measured in tileA units
     * @return valueA measured in tileB units
     */
    public static double convert(Tile<Integer> tileA, Tile<Integer> tileB, double valueA){
        return valueA * tileB.getLength() / tileA.getLength();
    }

    /**
     * Converts a point that is measured in tile1 units, into tile2 units.
     *
     * @param tileA a unit of measure, the value is measured currently with this
     * @param tileB the unit of measure you want the value to be represented in.
     * @param point a point measured in tileA units
     * @return given point measured in tileB units
     */
    public static Point convert(Tile<Integer> tileA, Tile<Integer> tileB, Point point){
        double x = (point.getPositionX() * tileB.getLength() / tileA.getLength());
        double y = (point.getPositionY() * tileB.getLength() / tileA.getLength());
        return new Point(x,y);
    }
}
