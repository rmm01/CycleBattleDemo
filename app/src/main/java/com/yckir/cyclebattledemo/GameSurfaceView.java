package com.yckir.cyclebattledemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * View that displays the cycle game. Drawing is done on an AsyncTask.
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, SurfaceDrawingTask.DrawingTaskListener{
    public static final String TAG = "GAME_SURFACE_VIEW";
    private final GameFrame mGameFrame;
    private RectangleContainer mRectangleContainer;
    private SurfaceDrawingTask mSurfaceDrawingTask;
    private SurfaceHolder mHolder;
    private long mStartTime;
    private long mPauseTime;
    private long mTotalPauseDelay;
    private GameEventListener mGameEventListener;
    private int mState;
    private final int WAITING=0;
    private final int RUNNING=1;
    private final int PAUSED=2;
    private final int FINISHED=3;

    /**
     * constructs the view. Custom xml attributes are read. default values are
     * tiles_x = 6, tiles_y = 6, cycles = 1, border_length = 10 and all colors are black.
     * GameContainer, GameFrame, and SurfaceDrawingTask are constructed using these attributes.
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
        mGameEventListener = null;

        //get custom xml attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GameSurfaceView, 0, 0);
        int numTilesX = a.getInt(R.styleable.GameSurfaceView_tiles_x, 6);
        int numTilesY = a.getInt(R.styleable.GameSurfaceView_tiles_y, 6);
        int numCycles = a.getInt(R.styleable.GameSurfaceView_cycles, 1);
        int boarderColor = a.getColor(R.styleable.GameSurfaceView_boarder_color, 0);
        int backgroundColor = a.getColor(R.styleable.GameSurfaceView_background_color,0);
        int borderSize=a.getDimensionPixelSize(R.styleable.GameSurfaceView_border_length, 10);
        a.recycle();

        mHolder = getHolder();
        mHolder.addCallback(this);

        //width and height are unknown so the default size for frame and container is used
        mGameFrame = new GameFrame(numTilesX, numTilesY, numCycles);
        mRectangleContainer = new RectangleContainer(boarderColor,backgroundColor,borderSize);

        mSurfaceDrawingTask=new SurfaceDrawingTask(mHolder,mGameFrame, mRectangleContainer);
        mSurfaceDrawingTask.addListener(this);
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
        mGameFrame.setRunning(true);
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
        mGameFrame.setRunning(false);
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

        mGameFrame.setRunning(true);
        mSurfaceDrawingTask = new SurfaceDrawingTask(mHolder,mGameFrame,mRectangleContainer);
        mSurfaceDrawingTask.addListener(this);
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

        mGameFrame.newGame();
        mSurfaceDrawingTask = new SurfaceDrawingTask(mHolder,mGameFrame,mRectangleContainer);
        mSurfaceDrawingTask.addListener(this);

        Canvas canvas = mHolder.lockCanvas();
        mSurfaceDrawingTask.doDraw(canvas);
        mHolder.unlockCanvasAndPost(canvas);
    }


    /**
     * Gets string form of the current state of the game.
     *
     * @return string form of Paused, Running, Waiting, or Finished
     */
    public String getState() {
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
     * Change the direction of the cycle if the input is valid.
     *
     * @param cycleNum the id for the cycle
     * @param newDirection the new direction for the cycle
     * @param currentTime current time in milliseconds
     */
    public void requestDirectionChange(int cycleNum, Compass newDirection, long currentTime){
        mGameFrame.requestDirectionChange( cycleNum, newDirection, currentTime -
                ( mStartTime + mTotalPauseDelay ) );
    }


    //callback methods
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Log.v(TAG, "surface created");
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(getResources().getColor(R.color.colorPrimary));
        holder.unlockCanvasAndPost(canvas);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        //Log.v(TAG, "surfaceChanged:\n format: " + format + ", w = " + width + ", h = " + height);

        mRectangleContainer.setContainerSize(width, height,0.9,0.9);
        mGameFrame.setFrameSize(mRectangleContainer.getRectangleWidth(), mRectangleContainer.getRectangleHeight());

        Canvas canvas = holder.lockCanvas();
        mSurfaceDrawingTask.doDraw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.v(TAG,"surfaceDestroyed");
    }


    //this method is used so that android studio can render the view
    @Override
    protected void onDraw(Canvas canvas) {
        mRectangleContainer.setContainerSize(getWidth(), getHeight(), 0.9, 0.9);
        mGameFrame.setFrameSize(mRectangleContainer.getRectangleWidth(), mRectangleContainer.getRectangleHeight());
        //mSurfaceDrawingTask.doDraw(canvas);
        mRectangleContainer.drawBorder(canvas);
        canvas.save();
        canvas.clipRect(mRectangleContainer.getLeft(), mRectangleContainer.getTop(),
                mRectangleContainer.getRight(), mRectangleContainer.getBottom());

        mGameFrame.drawFrame(canvas);
        //draw path
        canvas.restore();
    }


    /**
     * Called when an async task is finished. Notifies a listener if it exists
     */
    @Override
    public void taskEnded() {
        if(mGameEventListener != null && mState == RUNNING) {
            mState=FINISHED;
            mGameEventListener.gameEnded(0);
        }
    }


    /**
     * interface to notify of important game events.
     */
    interface GameEventListener {

        /**
         * Called when the game has finished.
         *
         * @param winner the cycleNumber of the winner
         */
        void gameEnded(int winner);

    }
}
