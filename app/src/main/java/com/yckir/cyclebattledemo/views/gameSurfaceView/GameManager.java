package com.yckir.cyclebattledemo.views.gameSurfaceView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Compass;
import com.yckir.cyclebattledemo.utility.GameResultsData;
import com.yckir.cyclebattledemo.utility.Tile;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Maintains the Cycles, Grid, and Path for a cycle Game and translates this data into
 * the animation frames.
 */
public class GameManager {
    public  static final String     TAG                     =   "GAME_FRAME";
    private static final String     REMAINING_CYCLES_KEY    =   TAG + ":REMAINING_CYCLES";
    private static final int        DEFAULT_FRAME_WIDTH     =   300;
    private static final int        DEFAULT_FRAME_HEIGHT    =   300;
    private static final int        GAME_GRID_TILE_LENGTH   =   1;

    // This grid will be the same for each player in the same game. All movement, collision
    // detection, etc will be done on this grid and later drawn to fit the users device screen.
    private Grid mGameGrid;

    /**
     * queue that holds requests to change the direction
     */
    private ArrayBlockingQueue<DirectionChangeRequest> mDirectionChanges;

    /**
     * records all requests to change directions
     */
    private ArrayList<DirectionChangeRequest> mRecorder;

    /**
     * data for a tile that appears on the animation frame, this will depend upon the
     * users device screen
     */
    public static Tile<Integer> SCREEN_GRID_TILE = new Tile<>(100);

    private Paint mGridLinePaint;

    private Cycle[] mCycles;
    private boolean mRunning;
    private int mNumCycles;
    private int mRemainingCycles;

    /**
     *     the width and height that the Game must fit into
     */
    private int mFrameWidth;
    private int mFrameHeight;

    /**
     *     the height and width of the Grid when displayed on the animation frame
     */
    private int mFrameGridWidth;
    private int mFrameGridHeight;

    /**
     *     the spacing between the left and top edges of the animation frame and the grid
     */
    private int mGridPaddingX;
    private int mGridPaddingY;



    /**
     * Initializes the Grid, animation frame size, and Cycles.
     *
     * @param width the width of an animation frame
     * @param height the height of an animation frame
     * @param numTilesX the number of tiles in the x direction
     * @param numTilesY the number of tiles in the y direction
     * @param numCycles the number of cycles that will be drawn
     */
    public GameManager(int width, int height, int numTilesX, int numTilesY, int numCycles){
        mFrameWidth =width;
        mFrameHeight =height;
        mNumCycles=numCycles;
        mGameGrid = new Grid(numTilesX,numTilesY,1);

        mDirectionChanges = new ArrayBlockingQueue<>(15);
        mRecorder = new ArrayList<>();
        mRemainingCycles =numCycles;
        mRunning = false;

        mGridLinePaint =new Paint();
        mGridLinePaint.setColor(Color.BLUE);

        initFrameSize();
        createCycles();
    }


    /**
     * initializes the Grid, animation frame size, and Cycles.
     * The width and height of an animation frame are set to a default value of 300x300.
     *
     * @param numTilesX the number of tiles in the x direction
     * @param numTilesY the number of tiles in the y direction
     * @param numCycles the number of cycles that will be drawn
     */
    public GameManager(int numTilesX, int numTilesY, int numCycles){
        //construct grid
        mGameGrid = new Grid(numTilesX,numTilesY, GAME_GRID_TILE_LENGTH);
        mNumCycles=numCycles;

        mFrameWidth = DEFAULT_FRAME_WIDTH;
        mFrameHeight = DEFAULT_FRAME_HEIGHT;

        mDirectionChanges = new ArrayBlockingQueue<>(15);
        mRecorder = new ArrayList<>();
        mRemainingCycles =numCycles;
        mRunning = false;

        mGridLinePaint =new Paint();
        mGridLinePaint.setColor(Color.BLUE);

        initFrameSize();
        createCycles();
    }


    /**
     * For JUnit testing with mock objects,
     */
    public GameManager(Grid grid, int width, int height, Paint p1) {
        mGridLinePaint =p1;
        mGameGrid =grid;
        mFrameWidth =width;
        mFrameHeight =height;
        mRunning = false;
        initFrameSize();
    }


    /**
     * Determines the max length a Tile can be such that the Grid can fit inside the
     * animation frame. Also adjust padding for the grid so that it is centered inside the Frame.
     */
    private void initFrameSize(){

        int numTilesX= mGameGrid.getNumTilesX();
        int numTilesY= mGameGrid.getNumTilesY();

        //since the screen is always in portrait mode, the largest possible grid that will
        //fit on the animation frame can be achieved by making the height of the grid as large as possible

        //it is possible that height1 is too big, so we check it with the actual frame height
        double height1 =  (mFrameWidth * numTilesY) / (double) numTilesX;
        double height=Math.min( height1, mFrameHeight);

        SCREEN_GRID_TILE = new Tile<>((int)(height / numTilesY));

        mFrameGridWidth = SCREEN_GRID_TILE.getLength() * numTilesX;
        mFrameGridHeight = SCREEN_GRID_TILE.getLength() * numTilesY;

        mGridPaddingX = ( mFrameWidth - mFrameGridWidth ) / 2;
        mGridPaddingY = ( mFrameHeight - mFrameGridHeight ) / 2;
    }


    /**
     * Creates cycles with default positions.
     */
    private void createCycles() {
        Log.v(TAG,"creating " + mNumCycles + " cycles");
        mCycles= new Cycle[mNumCycles];
        double w = Tile.convert(SCREEN_GRID_TILE,Grid.GAME_GRID_TILE,mFrameGridWidth);
        double h = Tile.convert(SCREEN_GRID_TILE, Grid.GAME_GRID_TILE, mFrameGridHeight);



        if(mNumCycles>=1) {
            mCycles[0] = new Cycle(w/2,0.5, 0.5, 0.5, 0);
        }
        if(mNumCycles>=2) {
            mCycles[1] = new Cycle( w/2, h -0.5, 0.5, 0.5, 1);
        }
        if(mNumCycles>=3) {
            mCycles[2] = new Cycle(0.5, h/2, 0.5, 0.5, 2);
        }
        if(mNumCycles>=4) {
            mCycles[3] = new Cycle(w-0.5, h/2, 0.5, 0.5, 3);
        }


    }


    /**
     * Draws the Grid onto the supplied canvas centered at the GamesFrames width and height.
     *
     * @param canvas the canvas that will be drawn on
     */
    private void drawGrid(Canvas canvas){
        Rect rect = canvas.getClipBounds();
        int numTilesX = mGameGrid.getNumTilesX();
        int numTilesY = mGameGrid.getNumTilesY();
        int left = mGridPaddingX + rect.left;
        int top = mGridPaddingY + rect.top;
        int bottom = top+ mFrameGridHeight - 1;
        int right = left+ mFrameGridWidth - 1;

        //draw vertical lines
        int offset=left;
        for (int tile = 0; tile < numTilesX; tile++) {
            canvas.drawLine(offset, top, offset, bottom, mGridLinePaint);
            offset += SCREEN_GRID_TILE.getLength();
            canvas.drawLine(offset - 1, top, offset - 1, bottom, mGridLinePaint);
        }

        //draw horizontal lines
        offset=top;
        for (int tile = 0; tile < numTilesY; tile++) {
            canvas.drawLine(left, offset, right, offset, mGridLinePaint);
            offset += SCREEN_GRID_TILE.getLength();
            canvas.drawLine(left, offset - 1, right, offset - 1, mGridLinePaint);
        }
    }


    /**
     * Draws the cycle paths onto the supplied canvas
     *
     * @param canvas the canvas that will be drawn on
     */
    private void drawPath(Canvas canvas){
        Rect r =canvas.getClipBounds();
        int paddingX=mGridPaddingX+r.left;
        int paddingY=mGridPaddingY+r.top;

        canvas.save();
        canvas.clipRect(paddingX,paddingY,paddingX+mFrameGridWidth,paddingY+mFrameGridHeight);
        for (int i=0; i < mNumCycles; i++){
            mCycles[i].drawPath(canvas);
        }
        canvas.restore();
    }


    /**
     * Draws the cycles onto the supplied canvas
     *
     * @param canvas the canvas that will be drawn on
     */
    private void drawCycles(Canvas canvas){
        Rect r =canvas.getClipBounds();
        int paddingX=mGridPaddingX+r.left;
        int paddingY=mGridPaddingY+r.top;
        int left,right,top,bottom;
        Cycle cycle;
        for(int i=0;i<mNumCycles;i++) {
            cycle=mCycles[i];
            left = paddingX +
                    (int) Tile.convert(Grid.GAME_GRID_TILE, SCREEN_GRID_TILE, cycle.getLeft());
            right = paddingX +
                    (int) Tile.convert(Grid.GAME_GRID_TILE, SCREEN_GRID_TILE, cycle.getRight());
            top = paddingY +
                    (int) Tile.convert(Grid.GAME_GRID_TILE, SCREEN_GRID_TILE ,cycle.getTop());
            bottom = paddingY +
                    (int) Tile.convert(Grid.GAME_GRID_TILE, SCREEN_GRID_TILE, cycle.getBottom());

            canvas.save();
            canvas.clipRect(left, top, right, bottom);
            mCycles[i].drawCycle(canvas);
            canvas.restore();
        }
    }


    /**
     * Draws a black background, grid, cycles, and path together onto a canvas.
     *
     * @param canvas the canvas to be drawn to.
     */
    public void drawFull(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        drawGrid(canvas);
        drawPath(canvas);
        drawCycles(canvas);
    }


    /**
     * Draw the background Image on a canvas. This includes a black canvas with the grid.
     *
     * @param canvas the canvas to be drawn to.
     */
    public void drawBackground(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        drawGrid(canvas);
    }


    /**
     * Draw the animation onto a canvas. this clears the canvas making it transparent and
     * then draws the path and cycles.
     *
     * @param canvas the canvas to be drawn to.
     */
    public void drawAnimation(Canvas canvas){
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        drawPath(canvas);
        drawCycles(canvas);
    }


    /**
     * Creates a new game by initializing new cycles. This method will fail if a game is
     * currently running.
     */
    public void newGame(){
        if( mRunning )
            return;

        mRemainingCycles = mNumCycles;
        mRunning=false;
        mDirectionChanges.clear();
        mRecorder= new ArrayList<>();
        createCycles();
    }


    /**
     * move all the cycles. This method will fail if a game is not currently running.
     * @param time the time in milliseconds since the game started
     */
    public void move(long time){
        if( !mRunning )
            return;

        for(int i=0;i<mNumCycles;i++){
            mCycles[i].move(time);
        }
    }


    /**
     * Creates a request to change the direction. The request is valid, it be applied on the next
     * animation frame. This method will fail if a game is not currently running.
     *
     * @param cycleNum the id for the cycle
     * @param newDirection the new direction for the cycle
     * @param time the time in milliseconds when the cycle will change directions
     */
    public void requestDirectionChange(int cycleNum, Compass newDirection, long time){
        //so that buffer isn't full of messages before the game starts
        if( !mRunning )
            return;

        DirectionChangeRequest node = new DirectionChangeRequest(newDirection,time,cycleNum);
        DirectionChangeRequest copyNode = node.makeCopy();
        mDirectionChanges.add(node);
        mRecorder.add(copyNode);
    }


    /**
     * Checks to see if their are any requests to change directions and apply them they are valid.
     * This method will fail if a game is not currently running.
     *
     * @return true if at least one direction changed, false otherwise.
     */
    public boolean checkDirectionChangeRequests(){
        if( !mRunning )
            return false;

        DirectionChangeRequest node = mDirectionChanges.poll();

        boolean change = false;
        boolean temp;
        while(node!=null){

            temp = mCycles[node.getCycleNum()].changeDirection(node.getDirection(), node.getTime());
            change = temp || change;
            node = mDirectionChanges.poll();
        }
        return change;
    }


    /**
     * Detects if any of cycles have collided and sets them to crashed status preventing them from
     * moving. This method will fail if a game is not currently running.
     *
     * @param currentTime the current time in milliseconds.
     */
    public void collisionDetection(long currentTime){
        if( !mRunning )
            return;

        int initialCycles = mRemainingCycles;

        for( int currentCycle = 0; currentCycle < mNumCycles; currentCycle++ ){
            //don't check if already crashed
            if( mCycles[currentCycle].hasCrashed() )
                continue;
            //check if cycle out of bounds,
            if( mGameGrid.OutOfBounds(mCycles[currentCycle])) {
                Log.v(TAG, "Player " + currentCycle + " is out of bounds");
                mCycles[currentCycle].crashed(currentTime);
                mRemainingCycles--;
                continue;
            }
            //check to see if cycle crashed with its own path
            if(mCycles[currentCycle].selfCrashed()) {
                Log.v(TAG, "Player " + currentCycle + " crashed with itself");
                mCycles[currentCycle].crashed(currentTime);
                mRemainingCycles--;
                continue;
            }

            //check to see if cycle crashed with its another cycle or their path
            for(int otherCycles = 0; otherCycles < mNumCycles; otherCycles++){
                if(otherCycles == currentCycle )
                    continue;
                if(mCycles[otherCycles].intersectsWithPath(mCycles[currentCycle])){
                    Log.v(TAG,"Player " + currentCycle + " crashed with cycle " + otherCycles);
                    mCycles[currentCycle].crashed(currentTime);
                    mRemainingCycles--;
                    break;
                }

            }
        }

        //determine the places for cycles now that all collisions have been detected.
        if( initialCycles != mRemainingCycles ){
            for( Cycle cycle:mCycles ){
               if(cycle.hasCrashed() && cycle.getPlace() == Cycle.DEFAULT_PLACE)
                   cycle.setPlace(mRemainingCycles + 1);
            }
        }

        if( mRemainingCycles <= 1 ) {
            //set place 1 for the uncrashed cycles.
            for (Cycle cycle:mCycles) {
                if( !cycle.hasCrashed() )
                    cycle.setPlace(1);
            }
            mRunning = false;
        }
    }


    public void updateNumPlayers(int numPlayers){
        mNumCycles=numPlayers;
        mRemainingCycles=numPlayers;
        createCycles();
    }


    /**
     * Set the animation frame dimensions.
     * @param width The new width of the animation frame.
     * @param height The new height of the animation frame.
     */
    public void setFrameSize(int width, int height){
        mFrameWidth =width;
        mFrameHeight =height;
        initFrameSize();
        createCycles();
    }


    /**
     * Set the game to be either running or not running.
     *
     * @param running true if the game is active, false otherwise
     */
    public void setRunning(boolean running){
        mRunning = running;
    }


    /**
     * @return true if the game is active, false otherwise
     */
    public boolean isRunning(){return mRunning;}


    /**
     * generates an object containing the results of the game.
     *
     * @return an instance of GameResultsData, null if the game is still running.
     */
    public GameResultsData generateResults(){
        if(mRunning)
            return null;
        return new GameResultsData(mCycles);
    }


    /**
     * @return the padding from the frame edge to the grid edge in x direction
     */
    public int getGridPaddingX() {
        return mGridPaddingX;
    }


    /**
     * @return the padding from the frame edge to the grid edge in y direction
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
    public int getFrameHeight() {
        return mFrameHeight;
    }


    /**
     *
     * @return the width of the animation frame
     */
    public int getFrameWidth() {
        return mFrameWidth;
    }


    /**
     * @return the number of cycles
     */
    public int getNumCycles(){return mNumCycles;}


    public ArrayList<DirectionChangeRequest> getReplay(){
        return mRecorder;
    }


    /**
     * Save the state of the GameManager onto a bundle.
     *
     * @param bundle the bundle to save the state onto
     */
    public void saveState(Bundle bundle){
        bundle.putInt(REMAINING_CYCLES_KEY,mRemainingCycles);
        for(int i = 0; i < mNumCycles; i++){
            mCycles[i].saveState(bundle);
        }
    }


    /**
     * Restore the previous state of the GameManager from a bundle.
     *
     * @param bundle the bundle that has the previous state saved
     */
    public void restoreState(Bundle bundle){
        mRemainingCycles = bundle.getInt(REMAINING_CYCLES_KEY,mNumCycles);
        for(int i = 0; i < mNumCycles; i++){
            mCycles[i].restoreState(bundle);
        }
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("mNumCycles",mNumCycles );
        description.addMember("mRemainingCycles", mRemainingCycles);
        description.addMember("mFrameWidth", mFrameWidth);
        description.addMember("mFrameHeight", mFrameHeight);
        description.addMember("mFrameGridWidth", mFrameGridWidth);
        description.addMember("mFrameGridHeight", mFrameGridHeight);
        description.addMember("mGridPaddingX", mGridPaddingX);
        description.addMember("mGridPaddingY", mGridPaddingY);
        description.addMember("mRunning", mRunning);
        description.addClassMember("mGameGrid", mGameGrid);
        description.addClassMember("SCREEN_GRID_TILE", SCREEN_GRID_TILE);

        for(int i = 0; i <mNumCycles;i++)
            description.addClassMember("mCycle[" + i + "}", mCycles[i]);


        return description.getString();

    }


    /**
     * An information node that keeps track of the details for when a cycle wants to changes its direction.
     */
    public final class DirectionChangeRequest {
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


        /**
         * @return a copy of the current instance
         */
        public DirectionChangeRequest makeCopy(){
            return new DirectionChangeRequest(mDirection,mTime,mCycleNum);
        }


        @Override
        public String toString() {
            return "Direction Change Request: player " + mCycleNum +
                    ", direction " + mDirection + ", at time " + mTime;
        }
    }
}
