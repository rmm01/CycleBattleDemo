package com.yckir.cyclebattledemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * Maintains the Cycles, Grid, and Path for a cycle Game and translates this data into
 * the animation frames.
 */
public class GameFrame {
    public static final String TAG = "GAME_FRAME";

    // This grid will be the same for each player in the same game. All movement, collision
    // detection, etc will be done on this grid and later drawn to fit the users device screen.
    private Grid mGameGrid;

    private int mNumCycles;
    private Cycle[] mCycles;

    // a data for a tile that appears on the animation frame, this will depend upon the
    // users device screen
    private Tile<Integer> mFrameTile;

    //the width and height that the Game must fit into
    private int mWidth;
    private int mHeight;

    //the height and width of the Grid when displayed on the animation frame
    private int mFrameGridWidth;
    private int mFrameGridHeight;

    //the spacing between the edges of the animation frame and the grid
    private int mGridPaddingX;
    private int mGridPaddingY;

    //since the Grid that appears on the Frame will remain the same unless the device screen size
    //changes, we draw it in its own bitmap so that we don't have to keep redrawing it
    private Bitmap mGridBitmap;

    //the bitmap where the grid, cycle, and paths will be drawn to
    private Bitmap mFrameBitmap;

    private Paint mGridLinePaint;
    private Paint mGridLinePaint2;

    private static final int DEFAULT_FRAME_WIDTH =300;
    private static final int DEFAULT_FRAME_HEIGHT =300;
    private static final int GAME_GRID_TILE_LENGTH =1;



    /**
     * Initializes the Grid, animation frame size, and Cycles.
     *
     * @param width the width of an animation frame
     * @param height the height of an animation frame
     * @param numTilesX the number of tiles in the x direction
     * @param numTilesY the number of tiles in the y direction
     * @param numCycles the number of cycles that will be drawn
     */
    public GameFrame(int width, int height, int numTilesX, int numTilesY, int numCycles){
        mWidth=width;
        mHeight=height;

        mNumCycles=numCycles;

        mGameGrid = new Grid(numTilesX,numTilesY,1);

        //create bitmap
        mGridBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mFrameBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);

        //set the line colors
        mGridLinePaint =new Paint();
        mGridLinePaint.setColor(Color.BLUE);
        mGridLinePaint2 =new Paint();
        mGridLinePaint2.setColor(Color.GRAY);

        createFrame();
        createFrameGrid();
        createCycles();

        drawFrame();
    }


    /**
     * initializes the Grid, animation frame size, and Cycles.
     * The width and height of an animation frame are set to a default value of 300x300.
     *
     * @param numTilesX the number of tiles in the x direction
     * @param numTilesY the number of tiles in the y direction
     * @param numCycles the number of cycles that will be drawn
     */
    public GameFrame(int numTilesX, int numTilesY, int numCycles){
        //construct grid
        mGameGrid = new Grid(numTilesX,numTilesY, GAME_GRID_TILE_LENGTH);

        mNumCycles=numCycles;

        //set width and height of the animation frame
        mWidth= DEFAULT_FRAME_WIDTH;
        mHeight= DEFAULT_FRAME_HEIGHT;


        //create bitmap
        mGridBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mFrameBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);

        //set the line colors
        mGridLinePaint =new Paint();
        mGridLinePaint.setColor(Color.BLUE);
        mGridLinePaint2 =new Paint();
        mGridLinePaint2.setColor(Color.GRAY);

        createFrame();
        createFrameGrid();

        createCycles();

        drawFrame();
    }


    /**
     * For JUnit testing with mock objects,
     */
    public GameFrame(Grid grid, int width, int height, Paint p1, Paint p2) {
        mGridLinePaint =p1;
        mGridLinePaint2 =p2;
        mGameGrid =grid;
        mWidth=width;
        mHeight=height;
        createFrame();
    }


    /**
     * Determines the max length a Tile can be such that the Grid can fit inside the
     * animation frame. Also adjust padding for the grid so that it is centered inside the Frame.
     */
    private void createFrame(){
        Log.v(TAG,"fitting screen");
        int numTilesX= mGameGrid.getNumTilesX();
        int numTilesY= mGameGrid.getNumTilesY();

        //since the screen is always in portrait mode, the largest possible grid that will
        //fit on the animation frame can be achieved by making the height of the grid as large as possible

        //it is possible that height1 is too big, so we check it with the actual frame height
        double height1 =  (mWidth * numTilesY) / (double) numTilesX;
        double height=Math.min( height1, mHeight);

        mFrameTile = new Tile<>((int)(height / numTilesY));

        mFrameGridWidth = mFrameTile.getLength() * numTilesX;
        mFrameGridHeight = mFrameTile.getLength() * numTilesY;

        mGridPaddingX=mWidth- mFrameGridWidth;
        mGridPaddingY=mHeight- mFrameGridHeight;
    }


    /**
     * The grid is guaranteed to remain the same unless the device screen size changes, because
     * of this, the grid will have its own bitmap so that the same image isn't redrawn.
     */
    private void createFrameGrid(){
        Log.v(TAG,"creating grid");
        Canvas mCanvas = new Canvas(mGridBitmap);

        int numTilesX= mGameGrid.getNumTilesX();
        int numTilesY= mGameGrid.getNumTilesY();

        int paddingX=mGridPaddingX/2;
        int paddingY=mGridPaddingY/2;

        //draw vertical lines
        int offset=paddingX;
        int top=paddingY;
        int bottom = paddingY+ mFrameGridHeight - 1;
        Log.v(TAG,"top: "+top+", bottom: "+bottom+", offset:"+offset);

        for (int tile = 0; tile < numTilesX; tile++) {
            mCanvas.drawLine(offset, top, offset,bottom, mGridLinePaint);
            offset += mFrameTile.getLength();
            mCanvas.drawLine(offset - 1, top, offset - 1,bottom, mGridLinePaint2);
        }


        //draw horizontal lines
        offset=paddingY;
        int left=paddingX;
        int right = paddingX+ mFrameGridWidth - 1;

        for (int tile = 0; tile < numTilesY; tile++) {
            mCanvas.drawLine(left, offset, right, offset, mGridLinePaint);
            offset += mFrameTile.getLength();
            mCanvas.drawLine(left, offset - 1, right, offset - 1, mGridLinePaint2);
        }

    }


    /**
     * Creates cycles with default positions.
     */
    private void createCycles() {
        mCycles= new Cycle[mNumCycles];
        for(int i=0;i<mNumCycles;i++){
            mCycles[i]=new Cycle(0.5+i,0.5,1,1,i);
            mCycles[i].drawCycle(mFrameTile.getLength(), mFrameTile.getLength());
        }
    }


    /**
     * Draws the grid, cycles, and path together into one animation frame.
     */
    private void drawFrame(){
        Log.v(TAG,"drawing frame");
        Canvas c = new Canvas(mFrameBitmap);
        c.drawColor(Color.BLACK);
        //draw grid
        c.drawBitmap(mGridBitmap,0,0,null);

        //draw Cycles
        int x;
        int y;
        for(int i=0;i<mNumCycles;i++) {
            x = mGridPaddingX/2 + gridToFrame(mCycles[i].getLeft());
            y = mGridPaddingY/2 + gridToFrame(mCycles[i].getBottom());
            c.drawBitmap(mCycles[i].getCycleBitmap(), x, y, null);
        }
        //draw paths
    }


    /**
     * Given a value on the GameGrid, the method will transform it to a value on the grid
     * located inside an animation frame.
     *
     * @param value  a value on the x or y axis that lies on the Grid
     * @return the location of the parameter on the animation frame
     */
    private int gridToFrame(double value){
        return (int)(value * mFrameTile.getLength()/ mGameGrid.getTileLength());
    }


    /**
     * Set the animation frame dimensions.
     * @param width The new width of the animation frame.
     * @param height The new height of the animation frame.
     */
    public void setFrameSize(int width, int height){
        mWidth=width;
        mHeight=height;
        mGridBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mFrameBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        createFrame();
        createFrameGrid();
        createCycles();
        drawFrame();

    }


    /**
     * @return bitmap containing the current animation frame
     */
    public Bitmap getFrameBitmap( ){
        return mFrameBitmap;
    }


    /**
     * @return the length of a tile on the animation frame
     */
    public int getFrameTileLength(){return mFrameTile.getLength();}


    /**
     * @return the padding in the x direction of the Grid inside the Frame
     */
    public int getGridPaddingX() {
        return mGridPaddingX;
    }


    /**
     * @return the padding in the Y direction of the Grid inside the Frame
     */
    public int getGridPaddingY() {
        return mGridPaddingY;
    }


    /**
     * @return the width of the grid on the animation frame
     */
    public int getFrameGridWidth(){
        return mFrameGridWidth;
    }


    /**
     * @return the height of the grid on the animation frame
     */
    public int getFrameGridHeight(){
        return mFrameGridHeight;
    }


    /**
     *
     * @return the height of the animation frame
     */
    public int getHeight() {
        return mHeight;
    }


    /**
     *
     * @return the width of the animation frame
     */
    public int getWidth() {
        return mWidth;
    }


    @Override
    public String toString() {
        String description ="~\n"+TAG;
        description+="\n|\twidth: "+mWidth+", height: "+mHeight;
        description+="\n|\tscreenWidth: "+ mFrameGridWidth +", screenHeight: "+ mFrameGridHeight;
        description+="\n|\tpaddingX: "+mGridPaddingX+", paddingY: "+mGridPaddingY;
        description+="\n|\ttileLength: "+ mFrameTile.getLength();
        description+="\n|\tnumCycles: "+mNumCycles;
        description+="\n|\t"+Grid.TAG;
        description+="\n|\t\tnumTilesX: "+ mGameGrid.getNumTilesX()+", numTilesY: "+ mGameGrid.getNumTilesY();
        description+="\n|\t\ttileLength: "+ mGameGrid.getTileLength();
//        for(int i=0;i<mNumCycles;i++){
//            description+=mCycles[i].toString();
//        }
        return description;
    }
}
