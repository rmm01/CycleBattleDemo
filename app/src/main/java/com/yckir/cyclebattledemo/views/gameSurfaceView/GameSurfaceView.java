package com.yckir.cyclebattledemo.views.gameSurfaceView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yckir.cyclebattledemo.R;
import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.Compass;
import com.yckir.cyclebattledemo.utility.FourRegionSwipeDetector;
import com.yckir.cyclebattledemo.utility.GameResultsData;

import java.util.ArrayList;

/**
 * View that displays the cycle game. Drawing is done on an AsyncTask.
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback,
        SurfaceDrawingTask.DrawingTaskListener, FourRegionSwipeDetector.OnRegionSwipeListener {

    public  static final String     TAG                     =   "GAME_SURFACE_VIEW";
    public  static final int        LOADING                 =   0;
    public  static final int        WAITING                 =   1;
    public  static final int        RUNNING                 =   2;
    public  static final int        PAUSED                  =   3;
    public  static final int        FINISHED                =   4;

    private static final String     WIDTH_KEY               =   TAG + ":HEIGHT";
    private static final String     HEIGHT_KEY              =   TAG + ":WIDTH";
    private static final String     TEXT_TOP_KEY                =   TAG + ":TEXT_TOP";
    private static final String     TEXT_BOT_KEY                =   TAG + ":TEXT_BOT";
    private static final String     STATE_KEY               =   TAG + ":STATE";
    private static final String     START_TIME_KEY          =   TAG + ":START_TIME";
    private static final String     PAUSE_TIME_KEY          =   TAG + ":PAUSE_TIME";
    private static final String     TOTAL_PAUSE_DELAY_KEY   =   TAG + ":TOTAL_PAUSE_DELAY";

    //how much of vertical space is reserved for the text area
    private static final double TEXT_AREA_PERCENTAGE = 0.05;

    private GameManager mGameManager;
    private ReplayManager mReplayManager;
    private RectangleContainer mRectangleContainer;
    private SurfaceDrawingTask mSurfaceDrawingTask;
    private GameEventListener mGameEventListener = null;
    private SurfaceHolder mHolder;
    private FourRegionSwipeDetector mSwipeListener;

    private int mWidth;
    private int mHeight;
    private int mState;
    private String mNumTilesX;
    private String mNumTilesY;
    private long mStartTime;
    private long mPauseTime;
    private long mTotalPauseDelay;


    /**
     * constructs the view. Custom xml attributes are read. default values are
     * tiles_x = 6, tiles_y = 6, cycles = 2, border_length = 10. All default colors are black except
     * for text which is blue.
     * GameContainer, GameManager, and SurfaceDrawingTask are constructed using these attributes.
     *
     * @param context context
     * @param attrs xml attributes
     */
    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mState = LOADING;
        mStartTime = 0;
        mPauseTime = 0;
        mTotalPauseDelay = 0;
        mWidth=0;
        mHeight=0;
        mGameEventListener = null;

        //get custom xml attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GameSurfaceView, 0, 0);
        int numCycles = a.getInt(R.styleable.GameSurfaceView_cycles, 2);
        int boarderColor = a.getColor(R.styleable.GameSurfaceView_boarder_color, Color.BLACK);
        int paddingColor = a.getColor(R.styleable.GameSurfaceView_padding_color, Color.BLACK);
        int textColor = a.getColor(R.styleable.GameSurfaceView_text_color, Color.BLUE);
        int borderSize=a.getDimensionPixelSize(R.styleable.GameSurfaceView_border_length, 10);

        a.recycle();

        mHolder = getHolder();
        mHolder.addCallback(this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        mNumTilesX = pref.getString(
                context.getResources().getString(R.string.pref_grid_width_key) ,
                context.getResources().getString(R.string.pref_grid_width_default));

        mNumTilesY = pref.getString(
                context.getResources().getString(R.string.pref_grid_height_key) ,
                context.getResources().getString(R.string.pref_grid_height_default));

        //width and height are unknown so the default size for frame and container is used
        mGameManager = new GameManager(getContext(), Integer.parseInt(mNumTilesX), Integer.parseInt(mNumTilesY), numCycles);
        mRectangleContainer = new RectangleContainer(boarderColor, paddingColor, textColor, borderSize);
        mRectangleContainer.setVerticalPadding(TEXT_AREA_PERCENTAGE);

        mSwipeListener = new FourRegionSwipeDetector(getContext(), numCycles,
                context.getResources().getDisplayMetrics(), this);
        mSwipeListener.disable();

        mSurfaceDrawingTask=new SurfaceDrawingTask(mHolder, mGameManager, mRectangleContainer, SurfaceDrawingTask.FULL_DRAW);
        mSurfaceDrawingTask.addDrawingEventListener(this);
        mSurfaceDrawingTask.addGameEventListener(mGameEventListener);
        mSurfaceDrawingTask.setSwipeDetector(mSwipeListener);
    }


    /**
     * @return bitmap of the background image.
     */
    private Bitmap createBackground(){
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int mode = mSurfaceDrawingTask.getDrawMode();

        mSurfaceDrawingTask.setDrawMode(SurfaceDrawingTask.BACKGROUND_DRAW);
        mSurfaceDrawingTask.draw(canvas);
        mSurfaceDrawingTask.setDrawMode(mode);
        return bitmap;
    }


    /**
     * Set a listener that will respond to important game events.
     *
     * @param listener the listener that will be called notified when game events happen
     */
    public void setGameEventListener(GameEventListener listener){
        mGameEventListener = listener;
        mSurfaceDrawingTask.addGameEventListener(mGameEventListener);
    }


    /**
     * Start the animation.
     *
     * @param startTime current time in milliseconds
     */
    public void start(long startTime){
        Log.v(TAG,"Starting at time " + startTime);
        mSwipeListener.enable();
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
        mSwipeListener.disable();
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
        mSwipeListener.enable();
        mState=RUNNING;
        long pauseDelay = resumeTime - mPauseTime;
        mTotalPauseDelay+=pauseDelay;
        Log.v(TAG,"Resuming at time " + resumeTime);
        Log.v(TAG,"pause delay was " + pauseDelay);

        mGameManager.setRunning(true);
        mSurfaceDrawingTask = new SurfaceDrawingTask(mHolder, mGameManager,mRectangleContainer,
                mSurfaceDrawingTask.getDrawMode());
        mSurfaceDrawingTask.addDrawingEventListener(this);
        mSurfaceDrawingTask.addGameEventListener(mGameEventListener);
        mSurfaceDrawingTask.setSwipeDetector(mSwipeListener);
        mSurfaceDrawingTask.execute(mStartTime + mTotalPauseDelay);
    }


    /**
     * Revert the state to before start was called
     */
    public void newGame(){
        mSwipeListener.disable();
        mState=WAITING;
        mStartTime=0;
        mPauseTime=0;
        mTotalPauseDelay=0;

        mGameManager.newGame();
        mSurfaceDrawingTask = new SurfaceDrawingTask(mHolder, mGameManager,mRectangleContainer,
                mSurfaceDrawingTask.getDrawMode());
        mSurfaceDrawingTask.addDrawingEventListener(this);
        mSurfaceDrawingTask.addGameEventListener(mGameEventListener);
        mSurfaceDrawingTask.setSwipeDetector(mSwipeListener);
        redrawView();
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
     * Set the text that you want to display at the top of the view.
     * Will not redraw if in RUNNING State
     *
     * @param text text to be displayed.
     * @param redraw true if you want to redraw teh screen, false if not
     */
    public void setTopText(@NonNull String text, boolean redraw){
        mRectangleContainer.setTopText(text);

        if(mState == RUNNING)
            return;

        if(redraw)
            redrawView();
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
            case LOADING:
                return "LOADING";
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
     * Remove the text at the top. Will not redraw if in RUNNING state
     *
     * @param redraw true if you want to redraw the screen, false if not
     */
    public void removeTopText(boolean redraw){
        mRectangleContainer.removeTopText();

        if(mState == RUNNING)
            return;

        if(redraw)
            redrawView();
    }


    /**
     * Causes the GameSurfaceView to redraw itself with the touchBoundaries
     * if they are enabled and any text that has been set. This method will do nothing if in
     * RUNNING state because the drawing thread is constantly redrawing.
     */
    public void redrawView(){
        if(mState == RUNNING)
            return;
        Canvas canvas = mHolder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mSurfaceDrawingTask.draw(canvas);
        mRectangleContainer.drawText(canvas);
        mSwipeListener.drawTouchBoundaries(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }


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
    public void surfaceCreated(SurfaceHolder holder) {}


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.v(TAG, "surfaceChanged:\n format: " + format + ", w = " + width + ", h = " + height
        //+ ", oldW = " +getWidth() + ", oldH = " + getHeight());

        if(width == 0 || height == 0)
            return;

        if( width != mWidth || height != mHeight ) {

            mWidth = width;
            mHeight = height;
            mRectangleContainer.setContainerSize(width, height);

            mGameManager.setFrameSize(mRectangleContainer.getRectangleWidth(), mRectangleContainer.getRectangleHeight());

            Bitmap bitmap = createBackground();
            if (mGameEventListener.backgroundReady(bitmap))
                mSurfaceDrawingTask.setDrawMode(SurfaceDrawingTask.ANIMATION_DRAW);
            else
                mSurfaceDrawingTask.setDrawMode(SurfaceDrawingTask.FULL_DRAW);

            if (mState == LOADING)
                mState = WAITING;
        }
        redrawView();

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}


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
        bundle.putString(TEXT_TOP_KEY, mRectangleContainer.getTopText());
        bundle.putString(TEXT_BOT_KEY, mRectangleContainer.getBottomText());
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
            mRectangleContainer.setTopText( bundle.getString(TEXT_TOP_KEY) );
            mRectangleContainer.setBottomText( bundle.getString(TEXT_BOT_KEY) );
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
        if(mState == RUNNING) {
            mSwipeListener.receiveTouchEvent(event);
            return true;
        }
        return false;
    }


    @Override
    public void taskEnded(ArrayList<GameManager.DirectionChangeRequest> list) {
        //redraw the canvas without the swipe indicators
        redrawView();
        mSwipeListener.disable();

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
         * @param bitmap the bitmap for the background
         * @return true if the background was successfully set, false otherwise
         */
        boolean backgroundReady(Bitmap bitmap);

        /**
         * Called when a cycle has successfully changed directions
         */
        void directionChange();

        /**
         * Called when a cycle has crashed
         */
        void crash();
    }
}
