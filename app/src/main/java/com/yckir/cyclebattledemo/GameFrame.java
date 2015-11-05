package com.yckir.cyclebattledemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.concurrent.ArrayBlockingQueue;

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
    public static Tile<Integer> SCREEN_GRID_TILE = new Tile<>(100);

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
    private Bitmap mFrameBitmap2;

    private Paint mGridLinePaint;
    private Paint mGridLinePaint2;

    private static final int DEFAULT_FRAME_WIDTH =300;
    private static final int DEFAULT_FRAME_HEIGHT =300;
    private static final int GAME_GRID_TILE_LENGTH =1;

    private long mStartTime;
    private boolean mBufferToggle;
    private Canvas mCanvas;

    //queue that holds requests to change the direction
    private ArrayBlockingQueue<DirectionChangeRequest> mDirectionChanges;

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
        mStartTime=-1;
        mNumCycles=numCycles;
        mBufferToggle=true;

        mGameGrid = new Grid(numTilesX,numTilesY,1);

        //create bitmap
        mGridBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mFrameBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mFrameBitmap2 =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);

        //set the line colors
        mGridLinePaint =new Paint();
        mGridLinePaint.setColor(Color.BLUE);
        mGridLinePaint2 =new Paint();
        mGridLinePaint2.setColor(Color.GRAY);

        createFrame();
        createFrameGrid();
        createCycles();

        drawFrame();
        mDirectionChanges = new ArrayBlockingQueue<>(15);
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
        mStartTime=-1;
        mBufferToggle=true;

        //create bitmap
        mGridBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mFrameBitmap =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mFrameBitmap2 =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);

        //set the line colors
        mGridLinePaint =new Paint();
        mGridLinePaint.setColor(Color.BLUE);
        mGridLinePaint2 =new Paint();
        mGridLinePaint2.setColor(Color.GRAY);

        createFrame();
        createFrameGrid();

        createCycles();

        drawFrame();
        mDirectionChanges = new ArrayBlockingQueue<>(15);
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

        int numTilesX= mGameGrid.getNumTilesX();
        int numTilesY= mGameGrid.getNumTilesY();

        //since the screen is always in portrait mode, the largest possible grid that will
        //fit on the animation frame can be achieved by making the height of the grid as large as possible

        //it is possible that height1 is too big, so we check it with the actual frame height
        double height1 =  (mWidth * numTilesY) / (double) numTilesX;
        double height=Math.min( height1, mHeight);

        SCREEN_GRID_TILE = new Tile<>((int)(height / numTilesY));

        mFrameGridWidth = SCREEN_GRID_TILE.getLength() * numTilesX;
        mFrameGridHeight = SCREEN_GRID_TILE.getLength() * numTilesY;

        mGridPaddingX=mWidth- mFrameGridWidth;
        mGridPaddingY=mHeight- mFrameGridHeight;
    }


    /**
     * The grid is guaranteed to remain the same unless the device screen size changes, because
     * of this, the grid will have its own bitmap so that the same image isn't redrawn.
     */
    private void createFrameGrid(){
        Canvas mCanvas = new Canvas(mGridBitmap);

        int numTilesX= mGameGrid.getNumTilesX();
        int numTilesY= mGameGrid.getNumTilesY();

        int paddingX=mGridPaddingX/2;
        int paddingY=mGridPaddingY/2;

        //draw vertical lines
        int offset=paddingX;
        int top=paddingY;
        int bottom = paddingY+ mFrameGridHeight - 1;

        for (int tile = 0; tile < numTilesX; tile++) {
            mCanvas.drawLine(offset, top, offset,bottom, mGridLinePaint);
            offset += SCREEN_GRID_TILE.getLength();
            mCanvas.drawLine(offset - 1, top, offset - 1,bottom, mGridLinePaint);
        }


        //draw horizontal lines
        offset=paddingY;
        int left=paddingX;
        int right = paddingX+ mFrameGridWidth - 1;

        for (int tile = 0; tile < numTilesY; tile++) {
            mCanvas.drawLine(left, offset, right, offset, mGridLinePaint);
            offset += SCREEN_GRID_TILE.getLength();
            mCanvas.drawLine(left, offset - 1, right, offset - 1, mGridLinePaint);
        }
        mGridBitmap=mGridBitmap.copy(Bitmap.Config.ARGB_8888,false);
    }


    /**
     * Creates cycles with default positions.
     */
    private void createCycles() {
        mCycles= new Cycle[mNumCycles];
        for(int i=0;i<mNumCycles;i++){
            mCycles[i]=new Cycle(0.5+i,0.5,.25,.25,i);
            mCycles[i].drawCycle(SCREEN_GRID_TILE.getLength(), SCREEN_GRID_TILE.getLength());
        }
    }


    /**
     * Given a value on the GameGrid, the method will transform it to a value on the grid
     * located inside an animation frame.
     *
     * @param value  a value on the x or y axis that lies on the Grid
     * @return the location of the parameter on the animation frame
     */
    private int gridToFrame(double value){
        return (int)(value * SCREEN_GRID_TILE.getLength()/ mGameGrid.getTileLength());
    }


    private Bitmap getNewDrawBitmap(){
        mBufferToggle=!mBufferToggle;
        if(mBufferToggle)
            return mFrameBitmap;
        else
            return mFrameBitmap2;
    }

    /**
     * Draws the grid, cycles, and path together into one animation frame on a canvas.
     *
     * @param canvas the canvas where a frame of the game animation will be drawn.
     */
    public void drawFrame(Canvas canvas){
        canvas.drawColor(Color.BLACK);

        int numTilesX= mGameGrid.getNumTilesX();
        int numTilesY= mGameGrid.getNumTilesY();

        Rect r =canvas.getClipBounds();

        int paddingX=mGridPaddingX/2+r.left;
        int paddingY=mGridPaddingY/2+r.top;

        //draw vertical lines
        int offset=paddingX;
        int top=paddingY;
        int bottom = paddingY+ mFrameGridHeight - 1;

        for (int tile = 0; tile < numTilesX; tile++) {
            canvas.drawLine(offset, top, offset,bottom, mGridLinePaint);
            offset += SCREEN_GRID_TILE.getLength();
            canvas.drawLine(offset - 1, top, offset - 1,bottom, mGridLinePaint);
        }


        //draw horizontal lines
        offset=paddingY;
        int left=paddingX;
        int right = paddingX+ mFrameGridWidth - 1;

        for (int tile = 0; tile < numTilesY; tile++) {
            canvas.drawLine(left, offset, right, offset, mGridLinePaint);
            offset += SCREEN_GRID_TILE.getLength();
            canvas.drawLine(left, offset - 1, right, offset - 1, mGridLinePaint);
        }

        //draw paths
        canvas.save();
        canvas.clipRect(paddingX,paddingY,paddingX+mFrameGridWidth,paddingY+mFrameGridHeight);
        for(int i=0;i<mNumCycles;i++){
            mCycles[i].drawPath(canvas);
        }
        canvas.restore();

                //Draw Cycles

        for(int i=0;i<mNumCycles;i++) {
            left = paddingX + gridToFrame(mCycles[i].getLeft());
            right = paddingX + gridToFrame(mCycles[i].getRight());
            top = paddingY + gridToFrame(mCycles[i].getTop());
            bottom = paddingY + gridToFrame(mCycles[i].getBottom());

            canvas.save();
            canvas.clipRect(left,top,right,bottom);
            mCycles[i].drawCycle(canvas);
            canvas.restore();
        }

    }


    /**
     * Draws the grid, cycles, and path together into one animation frame. The resulting bitmap is
     * obtained through getFrameBitmap
     * TODO delete me
     */
    public void drawFrame(){
        mCanvas=new Canvas(getNewDrawBitmap());
        //create a fake rect in order to avoid android studio rendering problems
        mCanvas.save();
        mCanvas.clipRect(0, 0, mWidth, mHeight);
        drawFrame(mCanvas);
        mCanvas.restore();
    }


    /**
     * move all the cycles
     * @param time the time in milliseconds since the game started
     */
    public void move(long time){
        for(int i=0;i<mNumCycles;i++){
            mCycles[i].move(time);
        }
    }


    /**
     * Creates a request to change the direction. The request is valid, it be applied on the next
     * animation frame.
     *
     * @param cycleNum the id for the cycle
     * @param newDirection the new direction for the cycle
     * @param time the time in milliseconds when the cycle will change directions
     */
    public void requestDirectionChange(int cycleNum, Compass newDirection, long time){
        //so that buffer isn't full of messages before the game starts
        if(mStartTime<0){
            return;
        }
        DirectionChangeRequest node = new DirectionChangeRequest(newDirection,time,cycleNum);
        mDirectionChanges.add(node);
    }


    /**
     * Checks to see if their are any requests to change directions and apply them they are valid.
     */
    public void checkDirectionChangeRequests(){
        DirectionChangeRequest node = mDirectionChanges.poll();
        while(node!=null){
            mCycles[node.getCycleNum()].changeDirection(node.getDirection(), node.getTime());
            node = mDirectionChanges.poll();
        }
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
        mFrameBitmap2 =Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        createFrame();
        createFrameGrid();
        createCycles();
        drawFrame();

    }


    /**
     * set the start time of the game
     * @param startTime the time that the game will begin, in milliseconds.
     */
    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }


    /**
     * @return bitmap containing the current animation frame
     */
    public Bitmap getFrameBitmap( ){
        //return mFrameBitmap;
        //return mFrameBitmap.copy(Bitmap.Config.ARGB_8888,false);
        if(mBufferToggle)
            return mFrameBitmap;
        else
            return mFrameBitmap2;
    }


    /**
     * @return the length of a tile on the animation frame
     */
    public int getFrameTileLength(){return SCREEN_GRID_TILE.getLength();}


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


    /**
     *
     * @return the start time of the animation
     */
    public long getStartTime() {
        return mStartTime;
    }


    @Override
    public String toString() {
        String description ="~\n"+TAG;
        description+="\n|\twidth: "+mWidth+", height: "+mHeight;
        description+="\n|\tscreenWidth: "+ mFrameGridWidth +", screenHeight: "+ mFrameGridHeight;
        description+="\n|\tpaddingX: "+mGridPaddingX+", paddingY: "+mGridPaddingY;
        description+="\n|\ttileLength: "+ SCREEN_GRID_TILE.getLength();
        description+="\n|\tnumCycles: "+mNumCycles;
        description+="\n|\t"+Grid.TAG;
        description+="\n|\t\tnumTilesX: "+ mGameGrid.getNumTilesX()+", numTilesY: "+ mGameGrid.getNumTilesY();
        description+="\n|\t\ttileLength: "+ mGameGrid.getTileLength();
        return description;
    }


    /**
     * An information node that keeps track of the details for when a cycle wants to changes its direction.
     */
    private final class DirectionChangeRequest {
        private final Compass mDirection;
        private final long mTime;
        private final int mCycleNum;


        /**
         * Constructs a node keeps track of the details for when a cycle changes direction.
         * @param direction the direction that the cycle should change directions to
         * @param time When the request was made. The time the milliseconds since thee start
         *             of the animation.
         * @param cycleNum the cycle that should change its direction.
         */
        public DirectionChangeRequest(Compass direction, long time, int cycleNum){
            mDirection=direction;
            mTime=time;
            mCycleNum=cycleNum;
        }


        /**
         * @return the cycle for this direction change request
         */
        public int getCycleNum() {
            return mCycleNum;
        }


        /**
         * @return the direction for this direction change request
         */
        public Compass getDirection() {
            return mDirection;
        }


        /**
         * @return the time for this direction change request.
         */
        public long getTime() {
            return mTime;
        }

    }
}
