package com.yckir.cyclebattledemo.views.gameSurfaceView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.yckir.cyclebattledemo.utility.ClassStateString;

/**
 * Given a canvas, you can specify a rectangle at its center and this class will draw a colored
 * border surrounding it. Can also have padding in between the rectangle and boarder. If vertical
 * padding exists, text can be drawn within the upper padding region.
 */
public class RectangleContainer {
    public static final String      TAG                     =   "RECTANGLE_CONTAINER";
    public static final int         DEFAULT_BORDER_LENGTH   =   10;

    private String mText;

    private int mTextPositionX;
    private int mTextPositionY;

    //padding between the edge of the boarder and the inner content
    private int mVerticalPadding;
    private int mHorizontalPadding;
    private double mVerticalPaddingPercent;
    private double mHorizontalPaddingPercent;

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
    private Paint mTextPaint;

    /**
     * Sets the color of the canvas, the color of the border, and length of the border.
     * the default container size is (4*borderLength)x(4*borderLength).
     *
     * @param borderColor this will be the color of the border
     * @param paddingColor this will be the color for the padding
     * @param textColor this will be the color for the text
     * @param borderLength Length of the borders measured perpendicular from the inner rectangle
     *                     faces. The boarder is DEFAULT_BORDER_LENGTH if this value is negative.
     */
    public RectangleContainer(int borderColor,int paddingColor,int textColor, int borderLength){
        if(borderLength < 0)
            mBorderLength = DEFAULT_BORDER_LENGTH;
        else
            mBorderLength = borderLength;

        mWidth = 4 * borderLength;
        mHeight = 4 * borderLength;

        mVerticalPadding = 0;
        mHorizontalPadding = 0;
        mVerticalPaddingPercent = 0;
        mVerticalPaddingPercent = 0;

        mOriginalPaint = new Paint();
        mOriginalPaint.setColor(borderColor);

        mBorderPaint = mOriginalPaint;

        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);

        mPaddingColor = new Paint();
        mPaddingColor.setColor(paddingColor);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(1);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mText = "";

        mTextPositionX = 0;
        mTextPositionY = 0;

        calculateDimensions();
    }


    /**
     * Use black as the boarder color from now on. This is meant for debugging.
     */
    public void useBlackBoarder(){
        mBorderPaint = mBlackPaint;
    }


    /**
     * Use the color given when the constructor was initialized as the boarder color.
     */
    public void useOriginalPaint(){
        mBorderPaint = mOriginalPaint;
    }


    /**
     * Calculates the dimensions for the inner rectangle, padding, boarder positions, and text
     * position and text size.
     */
    private void calculateDimensions(){
        mVerticalPadding = (int) ( (mHeight - mBorderLength) * mVerticalPaddingPercent);
        mHorizontalPadding = (int) ( (mWidth - mBorderLength) * mHorizontalPaddingPercent);

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

        mTextPaint.setTextSize( mVerticalPadding / 2 );

        int centerPadding = mVerticalPadding / 2 ;

        int baseLineOffset = (int) ( ( mTextPaint.descent() + mTextPaint.ascent() ) / 2 ) ;

        mTextPositionY = mBorderLength + centerPadding - baseLineOffset;

        mTextPositionX = mWidth / 2;

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
     * @param verticalPaddingPercent horizontal padding percentage, IllegalArgumentException thrown if
     *                          value is not between 0 and 1;
     */
    public void setVerticalPadding(double verticalPaddingPercent){
        if(verticalPaddingPercent < 0 || verticalPaddingPercent >1 ){
            throw new IllegalArgumentException("invalid parameter, verticalPaddingPercent = " + verticalPaddingPercent);
        }
        mVerticalPaddingPercent = verticalPaddingPercent;
        calculateDimensions();
    }


    /**
     * Set the horizontal padding between the boarder and its inner content.
     *
     * @param horizontalPaddingPercent horizontal padding percentage, IllegalArgumentException thrown if
     *                          value is not between 0 and 1;
     */
    public void setHorizontalPadding(double horizontalPaddingPercent){
        if(horizontalPaddingPercent < 0 || horizontalPaddingPercent > 1){
            throw new IllegalArgumentException("invalid parameter, horizontalPaddingPercent = " + horizontalPaddingPercent);
        }

        mHorizontalPaddingPercent = horizontalPaddingPercent;

        calculateDimensions();
    }


    /**
     * @param text the text to be displayed if drawText is called
     */
    public void setText(String text){
        mText = text;
    }


    /**
     * removes all text.
     */
    public void removeText(){
        mText = "";
    }


    /**
     * @return the text that will be displayed if drawText is called
     */
    public String getText(){
        return mText;
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
        return mLeft + mHorizontalPadding;
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
     * @param canvas the canvas that will be drawn on.
     */
    public void drawBorder(Canvas canvas){
        canvas.drawColor(mPaddingColor.getColor());

        //left wall
        canvas.drawRect(
                mLeftOuter,
                mTopOuter,
                mLeft,
                mBottomOuter,
                mBorderPaint);

        //right wall
        canvas.drawRect(
                mRight,
                mTopOuter,
                mRightOuter,
                mBottomOuter,
                mBorderPaint);

        //top wall
        canvas.drawRect(
                mLeft,
                mTopOuter,
                mRight,
                mTop,
                mBorderPaint);

        //bottom wall
        canvas.drawRect(
                mLeft,
                mBottom,
                mRight,
                mBottomOuter,
                mBorderPaint);
    }


    /**
     * draw text onto canvas. The position of the text will be centered on the top vertical
     * padding of the view.
     *
     * @param canvas the canvas that will be drawn on.
     */
    public void drawText(Canvas canvas){
        if(mText.compareTo("") == 0)
            return;
        Log.v(TAG, "Drawing text \"" + mText + "\", x = " + mTextPositionX + ", y = " + mTextPositionY + "at time " + System.currentTimeMillis() );
        canvas.drawText(mText, mTextPositionX, mTextPositionY, mTextPaint);
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
