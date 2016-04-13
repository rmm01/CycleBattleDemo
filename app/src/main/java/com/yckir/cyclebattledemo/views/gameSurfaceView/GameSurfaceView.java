package com.yckir.cyclebattledemo.views.gameSurfaceView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yckir.cyclebattledemo.R;
import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Compass;
import com.yckir.cyclebattledemo.utility.FileUtility;
import com.yckir.cyclebattledemo.utility.FourRegionSwipeDetector;
import com.yckir.cyclebattledemo.utility.GameResultsData;

import java.io.File;
import java.util.ArrayList;

/**
 * View that displays the cycle game. Drawing is done on an AsyncTask.
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback,
        SurfaceDrawingTask.DrawingTaskListener, FourRegionSwipeDetector.OnRegionSwipeListener {

    public  static final String     TAG                     =   "GAME_SURFACE_VIEW";
    public  static final int        WAITING                 =   0;
    public  static final int        RUNNING                 =   1;
    public  static final int        PAUSED                  =   2;
    public  static final int        FINISHED                =   3;
    private static final String     WIDTH_KEY               =   TAG + ":HEIGHT";
    private static final String     HEIGHT_KEY              =   TAG + ":WIDTH";
    private static final String     STATE_KEY               =   TAG + ":STATE";
    private static final String     START_TIME_KEY          =   TAG + ":START_TIME";
    private static final String     PAUSE_TIME_KEY          =   TAG + ":PAUSE_TIME";
    private static final String     TOTAL_PAUSE_DELAY_KEY   =   TAG + ":TOTAL_PAUSE_DELAY";

    private GameManager mGameManager;
    private ReplayManager mReplayManager;
    private RectangleContainer mRectangleContainer;
    private SurfaceDrawingTask mSurfaceDrawingTask;
    private GameEventListener mGameEventListener;
    private SurfaceHolder mHolder;
    private FourRegionSwipeDetector mSwipeListener;

    private int mWidth;
    private int mHeight;
    private int mState;
    private long mStartTime;
    private long mPauseTime;
    private long mTotalPauseDelay;


    /**
     * constructs the view. Custom xml attributes are read. default values are
     * tiles_x = 6, tiles_y = 6, cycles = 1, border_length = 10 and all colors are black.
     * GameContainer, GameManager, and SurfaceDrawingTask are constructed using these attributes.
     *
     * @param context context
     * @param attrs xml attributes
     */
    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mState = WAITING;
        mStartTime = 0;
        mPauseTime = 0;
        mTotalPauseDelay = 0;
        mWidth=0;
        mHeight=0;
        mGameEventListener = null;

        //get custom xml attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GameSurfaceView, 0, 0);
        int numTilesX = a.getInt(R.styleable.GameSurfaceView_tiles_x, 6);
        int numTilesY = a.getInt(R.styleable.GameSurfaceView_tiles_y, 6);
        int numCycles = a.getInt(R.styleable.GameSurfaceView_cycles, 1);
        int boarderColor = a.getColor(R.styleable.GameSurfaceView_boarder_color, 0);
        //int backgroundColor = a.getColor(R.styleable.GameSurfaceView_background_color,0);
        int borderSize=a.getDimensionPixelSize(R.styleable.GameSurfaceView_border_length, 10);
        a.recycle();

        mHolder = getHolder();
        mHolder.addCallback(this);

        //width and height are unknown so the default size for frame and container is used
        mGameManager = new GameManager(numTilesX, numTilesY, numCycles);
        mRectangleContainer = new RectangleContainer(boarderColor,borderSize);

        mSwipeListener = new FourRegionSwipeDetector(numCycles,
                context.getResources().getDisplayMetrics(), this);

        mSurfaceDrawingTask=new SurfaceDrawingTask(mHolder, mGameManager, mRectangleContainer, SurfaceDrawingTask.FULL_DRAW);
        mSurfaceDrawingTask.addListener(this);
        mSurfaceDrawingTask.setSwipeDetector(mSwipeListener);
    }


    /**
     * Creates and saves the background image onto a file. If the the file exists, it retrieves and
     * returns that file. Sets the
     * {@link com.yckir.cyclebattledemo.views.gameSurfaceView.SurfaceDrawingTask.Draw_Mode}
     *  of mSurfaceDrawingTask.
     *
     * @param fileName the name of the Background file.
     * @return File of the background image, null if the file could not be made,
     */
    private File createBackgroundImage(String fileName){

        //if file exists, set the mode and return
        if(FileUtility.backgroundFileExists(fileName, getContext())){
            mSurfaceDrawingTask.setDrawMode(SurfaceDrawingTask.ANIMATION_DRAW);
            return FileUtility.getBackgroundFile(fileName, getContext());
        }

        File backgroundImageFile = FileUtility.getBackgroundFile(fileName, getContext());

        //if cant make the file, set mode to draw background and animations
        if(backgroundImageFile == null){
            mSurfaceDrawingTask.setDrawMode(SurfaceDrawingTask.FULL_DRAW);
            return null;
        }

        //draw the image onto a canvas
        mSurfaceDrawingTask.setDrawMode(SurfaceDrawingTask.BACKGROUND_DRAW);
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mSurfaceDrawingTask.draw(canvas);

        // if cant write image to file, set set mode to draw background and animations
        if( !FileUtility.writeBitmapToPNG(bitmap, backgroundImageFile )){
            mSurfaceDrawingTask.setDrawMode(SurfaceDrawingTask.FULL_DRAW);
            FileUtility.deleteBackgroundFile(fileName, getContext());
            return null;
        }

        //successfully drew the image, set mode to draw only the cycle animations
        mSurfaceDrawingTask.setDrawMode(SurfaceDrawingTask.ANIMATION_DRAW);
        return  backgroundImageFile;
    }


    /**
     * Add a listener that will respond to important game events.
     *
     * @param listener the listener that will be called notified when game events happen
     */
    public void addGameEventListener(GameEventListener listener){
        mGameEventListener = listener;
    }


    /**
     * Start the animation.
     *
     * @param startTime current time in milliseconds
     */
    public void start(long startTime){
        Log.v(TAG,"Starting at time " + startTime);

        mState=RUNNING;
        mStartTime=startTime;
        mGameManager.setRunning(true);
        mSurfaceDrawingTask.setSwipeDetector(mSwipeListener);
        mSurfaceDrawingTask.execute(startTime);
    }


    /**
     * Pause the animation.
     *
     * @param pauseTime current time in milliseconds
     */
    public void pause(long pauseTime){
        Log.v(TAG, "Pausing at time " + pauseTime);

        mState=PAUSED;
        mPauseTime=pauseTime;
        mGameManager.setRunning(false);
    }


    /**
     * Resume the animation.
     *
     * @param resumeTime current time in milliseconds
     */
    public void resume(long resumeTime){
        mState=RUNNING;
        long pauseDelay = resumeTime - mPauseTime;
        mTotalPauseDelay+=pauseDelay;
        Log.v(TAG,"Resuming at time " + resumeTime);
        Log.v(TAG,"pause delay was " + pauseDelay);

        mGameManager.setRunning(true);
        mSurfaceDrawingTask = new SurfaceDrawingTask(mHolder, mGameManager,mRectangleContainer,
                mSurfaceDrawingTask.getDrawMode());
        mSurfaceDrawingTask.addListener(this);
        mSurfaceDrawingTask.setSwipeDetector(mSwipeListener);
        mSurfaceDrawingTask.execute(mStartTime + mTotalPauseDelay);
    }


    /**
     * Revert the state to before start was called
     */
    public void newGame(){
        mState=WAITING;
        mStartTime=0;
        mPauseTime=0;
        mTotalPauseDelay=0;

        mGameManager.newGame();
        mSurfaceDrawingTask = new SurfaceDrawingTask(mHolder, mGameManager,mRectangleContainer,
                mSurfaceDrawingTask.getDrawMode());
        mSurfaceDrawingTask.addListener(this);
        mSurfaceDrawingTask.setSwipeDetector(mSwipeListener);
        Canvas canvas = mHolder.lockCanvas();
        mSurfaceDrawingTask.draw(canvas);
        mSwipeListener.drawTouchBoundaries(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }


    /**
     * show a replay of the most recent game
     */
    public void replay(){
        mReplayManager.play();
    }


    /**
     * Set the number of players. If the number of players changes, all of the cycles are
     * recreated.
     *
     * @param numPlayers the new number of players.
     */
    public void setNumPlayers(int numPlayers){
        if(numPlayers != mGameManager.getNumCycles()) {
            mGameManager.updateNumPlayers(numPlayers);
            mSwipeListener.setNumRegions(numPlayers);
        }
    }


    /**
     * Gets string form of the current state of the game.
     *
     * @return string form of Paused, Running, Waiting, or Finished
     */
    public String getStringState() {
        switch (mState) {
            case RUNNING:
                return "RUNNING";
            case PAUSED:
                return "PAUSED";
            case FINISHED:
                return "FINISHED";
            case WAITING:
                return "WAITING";
            default:
                return "UNKNOWN";
        }
    }


    /**
     * Gets state int value for the current state of the game.
     *
     * @return int value of Paused, Running, Waiting, or Finished
     */
    public int getState(){return mState;}


    /**
     * Change the direction of the cycle if the input is valid.
     *
     * @param cycleNum the id for the cycle
     * @param newDirection the new direction for the cycle
     * @param currentTime current time in milliseconds
     */
    public void requestDirectionChange(int cycleNum, Compass newDirection, long currentTime){
        mGameManager.requestDirectionChange(cycleNum, newDirection, currentTime -
                (mStartTime + mTotalPauseDelay));
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated");
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        holder.unlockCanvasAndPost(canvas);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.v(TAG, "surfaceChanged:\n format: " + format + ", w = " + width + ", h = " + height
        //+ ", oldW = " +getWidth() + ", oldH = " + getHeight());
        if( mState == WAITING ) {
            Log.v(TAG, "surfaceChanged");
            mWidth=width;
            mHeight=height;
            mRectangleContainer.setContainerSize(width, height);
            mGameManager.setFrameSize(mRectangleContainer.getRectangleWidth(), mRectangleContainer.getRectangleHeight());
        }

        String fileName = "MultiplayerBackgroundImage_"+width+"x"+height+".png";
        File backgroundFile = createBackgroundImage(fileName);
        if(backgroundFile != null)
            mGameEventListener.backgroundReady(backgroundFile);

        Canvas canvas = holder.lockCanvas();
        mSurfaceDrawingTask.draw(canvas);
        mSwipeListener.drawTouchBoundaries(canvas);
        holder.unlockCanvasAndPost(canvas);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed");
    }


    //this method is used so that android studio can render the view
    @Override
    protected void onDraw(Canvas canvas) {
        mRectangleContainer.setContainerSize(getWidth(), getHeight());
        mGameManager.setFrameSize(mRectangleContainer.getRectangleWidth(), mRectangleContainer.getRectangleHeight());
        //mSurfaceDrawingTask.doDraw(canvas);
        mRectangleContainer.drawBorder(canvas);
        canvas.save();
        canvas.clipRect(mRectangleContainer.getLeft(), mRectangleContainer.getTop(),
                mRectangleContainer.getRight(), mRectangleContainer.getBottom());

        mGameManager.drawFull(canvas);
        //draw path
        canvas.restore();
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt(WIDTH_KEY, mWidth);
        bundle.putInt(HEIGHT_KEY, mHeight);
        bundle.putInt(STATE_KEY, mState);
        bundle.putLong(START_TIME_KEY, mStartTime);
        bundle.putLong(PAUSE_TIME_KEY, mPauseTime);
        bundle.putLong(TOTAL_PAUSE_DELAY_KEY, mTotalPauseDelay);
        mGameManager.saveState(bundle);
        return bundle;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle = (Bundle) state;

            mWidth = bundle.getInt(WIDTH_KEY, 200);
            mHeight = bundle.getInt(HEIGHT_KEY, 200);
            mState = bundle.getInt(STATE_KEY, WAITING);
            mStartTime = bundle.getLong(START_TIME_KEY, 0);
            mPauseTime = bundle.getLong(PAUSE_TIME_KEY,0);
            mTotalPauseDelay = bundle.getLong(TOTAL_PAUSE_DELAY_KEY, 0);
            state = bundle.getParcelable("instanceState");
            mRectangleContainer.setContainerSize(mWidth, mHeight);
            mGameManager.setFrameSize(mRectangleContainer.getRectangleWidth(), mRectangleContainer.getRectangleHeight());

            mGameManager.restoreState(bundle);
        }
        super.onRestoreInstanceState(state);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mSwipeListener.receiveTouchEvent(event);
        return true;
    }


    @Override
    public void taskEnded(ArrayList<GameManager.DirectionChangeRequest> list) {
        if(mGameEventListener != null && mState == RUNNING) {
            mState=FINISHED;
            mGameEventListener.gameEnded(mGameManager.generateResults());
            mReplayManager = new ReplayManager(list,this);
        }
    }


    @Override
    public void onRegionSwipe(int playerNumber, Compass direction, long swipeTime) {
            if( mState != RUNNING )
                return;
            requestDirectionChange(playerNumber, direction, swipeTime);
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("mState", getStringState());
        description.addMember("mStartTime",mStartTime);
        description.addMember("mPauseTime", mPauseTime);
        description.addMember("mTotalPauseDelay", mTotalPauseDelay);

        description.addClassMember("mRectangleContainer", mRectangleContainer);
        description.addClassMember("mSurfaceDrawingTask", mSurfaceDrawingTask);
        description.addClassMember("mGameManager", mGameManager);

        return description.getString();
    }


    /**
     * interface to notify of important game events.
     */
    public interface GameEventListener {

        /**
         * Called when the game has finished.
         *
         * @param gameResultsData object containing data related to the results of the game.
         */
        void gameEnded(GameResultsData gameResultsData);

        /**
         * Called when the background file is ready to be used.
         *
         * @param file the background image
         */
        void backgroundReady(File file);

    }
}
