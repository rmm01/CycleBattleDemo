package com.yckir.cyclebattledemo.views.gameSurfaceView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.yckir.cyclebattledemo.utility.ClassStateString;

/**
 * Given a canvas, you can specify a rectangle at its center and this class will draw a colored
 * border surrounding it.
 */
public class RectangleContainer {
    public static final String      TAG                     =   "RECTANGLE_CONTAINER";
    public static final int         DEFAULT_BORDER_LENGTH   =   10;

    //padding between the edge of the boarder and the inner content
    private int mVerticalPadding;
    private int mHorizontalPadding;

    private int mWidth;
    private int mHeight;
    private int mRectangleWidth;
    private int mRectangleHeight;

    private int mLeft;
    private int mRight;
    private int mTop;
    private int mBottom;

    private int mLeftOuter;
    private int mRightOuter;
    private int mTopOuter;
    private int mBottomOuter;
    private int mBorderLength;

    private Paint mOriginalPaint;
    private Paint mBorderPaint;
    private Paint mBlackPaint;
    private Paint mPaddingColor;

    /**
     * Sets the color of the canvas, the color of the border, and length of the border.
     * the default container size is (4*borderLength)x(4*borderLength).
     *
     * @param borderColor this will be the color of the border
     * @param paddingColor this will be the color for the padding
     * @param borderLength Length of the borders measured perpendicular from the inner rectangle
     *                     faces. The boarder is DEFAULT_BORDER_LENGTH if this value is negative.
     */
    public RectangleContainer(int borderColor,int paddingColor, int borderLength){
        if(borderLength < 0)
            mBorderLength = DEFAULT_BORDER_LENGTH;
        else
            mBorderLength = borderLength;

        mWidth = 4 * borderLength;
        mHeight = 4 * borderLength;

        mVerticalPadding = 0;
        mHorizontalPadding = 0;

        calculateDimensions();

        mOriginalPaint = new Paint();
        mOriginalPaint.setColor(borderColor);

        mBorderPaint = mOriginalPaint;

        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);

        mPaddingColor = new Paint();
        mPaddingColor.setColor(paddingColor);
    }


    /**
     * Uses black as the boarder color from now on.
     */
    public void useBlackBoarder(){
        mBorderPaint = mBlackPaint;
    }


    /**
     * used the color given when the constructor was initialized as the boarder color.
     */
    public void useOriginalPaint(){
        mBorderPaint = mOriginalPaint;
    }


    /**
     * Calculates values that width and height of the inner rectangle along with teh position of the
     * border edges. This must be called before setContainerSize
     */
    private void calculateDimensions(){
        mRectangleWidth  = mWidth - mBorderLength * 2 - mHorizontalPadding * 2;
        mRectangleHeight = mHeight - mBorderLength * 2 - mVerticalPadding * 2;

        mLeft   = mBorderLength;
        mRight  = mWidth - mBorderLength;
        mTop    = mBorderLength;
        mBottom = mHeight - mBorderLength;

        mLeftOuter   = mLeft   - mBorderLength;
        mRightOuter  = mRight  + mBorderLength;
        mTopOuter    = mTop    - mBorderLength;
        mBottomOuter = mBottom + mBorderLength;
    }


    /**
     * Set the new size of the Container.
     *
     * @param width the new width of the canvas
     * @param height the new height of the canvas
     */
    public void setContainerSize(int width, int height){
        if(width == 0 ){
            throw new IllegalArgumentException("invalid parameter, width cant be zero ");
        }

        if(height == 0 ){
            throw new IllegalArgumentException("invalid parameter, width cant be zero ");
        }

        mWidth = width;
        mHeight = height;

        calculateDimensions();
    }


    /**
     * Set the vertical padding between the boarder and its inner content.
     *
     * @param verticalPadding horizontal padding percentage, IllegalArgumentException thrown if
     *                          value is not between 0 and 1;
     */
    public void setVerticalPadding(double verticalPadding){
        if(verticalPadding < 0 || verticalPadding >1 ){
            throw new IllegalArgumentException("invalid parameter, verticalPadding = " + verticalPadding);
        }

        mVerticalPadding = (int) ((mHeight - mBorderLength) * verticalPadding);

        calculateDimensions();
    }


    /**
     * Set the horizontal padding between the boarder and its inner content.
     *
     * @param horizontalPadding horizontal padding percentage, IllegalArgumentException thrown if
     *                          value is not between 0 and 1;
     */
    public void setHorizontalPadding(double horizontalPadding){
        if(horizontalPadding < 0 || horizontalPadding > 1){
            throw new IllegalArgumentException("invalid parameter, horizontalPadding = " + horizontalPadding);
        }

        mHorizontalPadding = (int) ((mWidth - mBorderLength) * horizontalPadding);

        calculateDimensions();
    }


    /**
     * @return the vertical padding between the boarder and its inner content, measured in pixels
     */
    public int getVerticalPadding(){
        return mVerticalPadding;
    }


    /**
     * @return the horizontal padding between the boarder and its inner content, measured in pixels
     */
    public int getHorizontalPadding(){
        return mHorizontalPadding;
    }


    /**
     * get the left side of the rectangle inside the container
     *
     * @return the x coordinate of the left face of the rectangle inside the container
     */
    public int getLeft() {
        return mLeft - mHorizontalPadding;
    }


    /**
     * get the right side of the  rectangle inside the container
     *
     * @return the x coordinate of the right face of the rectangle inside the container
     */
    public int getRight() {
        return mRight - mHorizontalPadding;
    }


    /**
     * get the top side of the rectangle inside the container
     *
     * @return the y coordinate of the top face of the rectangle inside the container
     */
    public int getTop() {
        return mTop + mVerticalPadding;
    }


    /**
     * get the bottom side of the  rectangle inside the container
     *
     * @return the y coordinate of the bottom face of the rectangle inside the container
     */
    public int getBottom() {
        return mBottom - mVerticalPadding;
    }


    /**
     * @return the height of the rectangle in the container
     */
    public int getRectangleHeight() {
        return mRectangleHeight;
    }


    /**
     * @return the width of the rectangle in the container
     */
    public int getRectangleWidth() {
        return mRectangleWidth;
    }


    /**
     * @return length of the boarder measured in pixels
     */
    public int getBorderLength() {
        return mBorderLength;
    }


    /**
     * Draw the rectangle and border inside the canvas.
     * clips not yet supported.
     *
     * @param c the canvas that will be drawn on.
     */
    public void drawBorder(Canvas c){
        c.drawColor(mPaddingColor.getColor());

        //left wall
        c.drawRect(
                mLeftOuter,
                mTopOuter,
                mLeft,
                mBottomOuter,
                mBorderPaint);

        //right wall
        c.drawRect(
                mRight,
                mTopOuter,
                mRightOuter,
                mBottomOuter,
                mBorderPaint);

        //top wall
        c.drawRect(
                mLeft,
                mTopOuter,
                mRight,
                mTop,
                mBorderPaint);

        //bottom wall
        c.drawRect(
                mLeft,
                mBottom,
                mRight,
                mBottomOuter,
                mBorderPaint);
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("mWidth", mWidth);
        description.addMember("mHeight", mHeight);
        description.addMember("mRectangleWidth", mRectangleWidth);
        description.addMember("mRectangleHeight", mRectangleHeight);
        description.addMember("mHorizontalPadding", mHorizontalPadding);
        description.addMember("mVerticalPadding", mVerticalPadding);
        description.addMember("mLeft", mLeft);
        description.addMember("mRight", mRight);
        description.addMember("mTop", mTop);
        description.addMember("mBottom", mBottom);
        description.addMember("mLeftOuter", mLeftOuter);
        description.addMember("mRightOuter", mRightOuter);
        description.addMember("mTopOuter", mTopOuter);
        description.addMember("mBottomOuter", mBottomOuter);
        description.addMember("mBorderLength", mBorderLength);

        return description.getString();
    }
}
