package com.yckir.cyclebattledemo.views.gameSurfaceView;


import android.graphics.Canvas;

import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.FourRegionSwipeDetector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Draws on a canvas supplied by a SurfaceHolder and runs on separate thread.
 * What gets drawn is based on the current
 * {@link com.yckir.cyclebattledemo.views.gameSurfaceView.SurfaceDrawingTask.Draw_Mode}.
 */
public class SurfaceDrawingTask extends AsyncTask<Long, Integer, Void>{

    /**
     * Id's that are used to determine what will be drawn in the {@link #draw(Canvas)} method.<p>
     *
     * FullDraw:   the background and the animation will be drawn together.<br>
     * ANIMATION_DRAW:   the animation will be drawn.<br>
     * BACKGROUND_DRAW:   the background will be drawn.
     *
     */
    @IntDef({FULL_DRAW,ANIMATION_DRAW,BACKGROUND_DRAW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Draw_Mode{}

    /**
     * the background and the animation will be drawn together
     */
    public static final int FULL_DRAW = 0;
    /**
     * the animation will be drawn
     */
    public static final int ANIMATION_DRAW = 1;
    /**
     * the background will be drawn
     */
    public static final int BACKGROUND_DRAW = 2;

    public static String TAG="SURFACE_DRAWING_TASK";

    private static final int TURN_ID = 0;
    private static final int CRASH_ID = 1;

    private final SurfaceHolder mSurfaceHolder;
    private final GameManager mGameManager;
    private final RectangleContainer mRectangleContainer;
    private DrawingTaskListener mDrawingEventListener;
    private FourRegionSwipeDetector mDetector = null;
    private GameSurfaceView.GameEventListener mGameEventListener = null;

    private long mTotalTaskDelay;
    private long mTotalUpdatePositionDelay;
    private long mTotalDrawDelay;
    private int mDrawingMode;


    /**
     * Constructs drawing task that draws on the canvas provided by a surface holder.
     *
     * @param holder used to retrieve canvas to draw on
     * @param gameManager holds the game information and knows how to draw the game into a canvas
     * @param rectangleContainer centers the game frame on a canvas and draws a border surrounding it
     * @param drawMode either {@link #FULL_DRAW}, {@link #ANIMATION_DRAW}, or {@link #BACKGROUND_DRAW}. See
     *                 {@link com.yckir.cyclebattledemo.views.gameSurfaceView.SurfaceDrawingTask.Draw_Mode}
     *                  for more information.
     *
     */
    public SurfaceDrawingTask(SurfaceHolder holder, GameManager gameManager,
                              RectangleContainer rectangleContainer, @Draw_Mode int drawMode){
        mSurfaceHolder=holder;
        mGameManager = gameManager;
        mRectangleContainer = rectangleContainer;
        mDrawingEventListener = null;
        mDrawingMode = drawMode;
    }


    /**
     * Add a listener that will be notified when the task ends.
     *
     * @param listener the listener that will be notified
     */
    public void addDrawingEventListener(DrawingTaskListener listener){
        mDrawingEventListener = listener;
    }


    public void addGameEventListener(GameSurfaceView.GameEventListener listener){
        mGameEventListener = listener;
    }


    /**
     * Set the swipe detector that will be used to draw the touch feedback.
     *
     * @param detector that is tracking the touch events.
     */
    public void setSwipeDetector(FourRegionSwipeDetector detector){
        mDetector = detector;
    }


    /**
     * Set the behavior for draw behavior.
     * See {@link com.yckir.cyclebattledemo.views.gameSurfaceView.SurfaceDrawingTask.Draw_Mode}
     * for more information.
     *
     * @param mode either {@link #FULL_DRAW}, {@link #ANIMATION_DRAW}, or {@link #BACKGROUND_DRAW}.
     */
    public void setDrawMode(@Draw_Mode int mode){
        mDrawingMode = mode;

        // the rectangle color is changed so that the modes can be distinguished while debugging
        //TODO: remove me for final product
        if(mode == FULL_DRAW)
           mRectangleContainer.useRedBoarder();

        if(mode == BACKGROUND_DRAW)
            mRectangleContainer.useOriginalPaint();
    }


    /**
     * Get the current drawing behavior.
     * See {@link com.yckir.cyclebattledemo.views.gameSurfaceView.SurfaceDrawingTask.Draw_Mode}
     * for more information.
     *
     * @return either {@link #FULL_DRAW}, {@link #ANIMATION_DRAW}, or {@link #BACKGROUND_DRAW}.
     */
    @Draw_Mode
    public int getDrawMode(){
        return mDrawingMode;
    }


    /**
     * Runs until all but one cycle crash.
     *
     * @param params the start time of the animation
     * @return null
     */
    @Override
    protected Void doInBackground(Long... params) {
        long start =params[0];
        long frameStartTime;
        Log.v(TAG, "starting at time " + start);
        while (mGameManager.isRunning()) {
            frameStartTime = System.currentTimeMillis() - start;

            if(mGameManager.checkDirectionChangeRequests()){
                publishProgress(TURN_ID);
            }

            mGameManager.move(frameStartTime);
            if( mGameManager.collisionDetection(frameStartTime) ){
                publishProgress(CRASH_ID);
            }

            Canvas canvas = mSurfaceHolder.lockCanvas();
            draw(canvas);
            if (mDetector != null) {
                mDetector.drawTouch(canvas);
                mDetector.drawTouchBoundaries(canvas);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
        Log.v(TAG,"Done with task");
        return null;
    }



    @Override
    protected void onProgressUpdate(Integer... values) {
       if(mGameEventListener == null || values == null)
           return;
        if(values[0] == TURN_ID)
           mGameEventListener.directionChange();
        if(values[0] == CRASH_ID)
            mGameEventListener.crash();
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        ArrayList<GameManager.DirectionChangeRequest> list = mGameManager.getReplay();
        //for(int i = 0; i < list.size(); i++)
        //    Log.v(TAG,list.get(i).toString());
        if(mDrawingEventListener !=null){
            mDrawingEventListener.taskEnded(list);
        }
    }


    /**
     * draw onto the given canvas. What will be drawn will depend on the current
     * {@link com.yckir.cyclebattledemo.views.gameSurfaceView.SurfaceDrawingTask.Draw_Mode} value.
     * This value can be set by calling {@link #setDrawMode(int)}.
     *
     * @param canvas canvas to be drawn on.
     */
    public void draw(Canvas canvas){

        switch (mDrawingMode){
            case FULL_DRAW:
                mRectangleContainer.drawBorder( canvas );
                canvas.save();
                canvas.clipRect(mRectangleContainer.getLeft(), mRectangleContainer.getTop(),
                        mRectangleContainer.getRight(), mRectangleContainer.getBottom());

                mGameManager.drawFull(canvas);
                canvas.restore();
                break;

            case ANIMATION_DRAW:
                canvas.save();
                canvas.clipRect(mRectangleContainer.getLeft(), mRectangleContainer.getTop(),
                        mRectangleContainer.getRight(), mRectangleContainer.getBottom());

                mGameManager.drawAnimation( canvas );
                canvas.restore();
                break;

            case BACKGROUND_DRAW:
                mRectangleContainer.drawBorder( canvas );
                canvas.save();
                canvas.clipRect(mRectangleContainer.getLeft(), mRectangleContainer.getTop(),
                        mRectangleContainer.getRight(), mRectangleContainer.getBottom());

                mGameManager.drawBackground(canvas);
                canvas.restore();
                break;

        }
    }


    /**
     * Logs details on the delays associated with the creation of a frame. Keeps track of total
     * delays for the 60 frame animation. All time is measured in milliseconds
     *
     * @param frameNum the frame number
     * @param startTime the time in the animation that the frame was start to be made
     * @param taskDelay time for the frame to be created
     * @param updatePositionDelay time to move the cycles and perform collision detection
     * @param drawDelay time to draw the frame
     */
    private void logFrameInfo(int frameNum,long startTime, long taskDelay,long updatePositionDelay, long drawDelay){

        String s_frameNum="Frame " + frameNum+": ";
        String s_startTime="start time ="+startTime;
        String s_taskDelay="task delay = " + taskDelay;
        String s_invalidationDelay="updatePosition delay = "+updatePositionDelay;
        String s_drawDelay="draw delay = " + drawDelay;


        Log.v("FRAME_INFO", s_frameNum+s_startTime);
        Log.v("FRAME_INFO", s_frameNum + s_taskDelay);
        Log.v("FRAME_INFO", s_frameNum + s_invalidationDelay);
        Log.v("FRAME_INFO", s_frameNum + s_drawDelay);


        mTotalTaskDelay+=taskDelay;
        mTotalUpdatePositionDelay +=updatePositionDelay;
        mTotalDrawDelay+=drawDelay;


        if(frameNum==300){
            Log.v("SUMMARY","total task delay "+mTotalTaskDelay);
            Log.v("SUMMARY","total updatePosition delay "+ mTotalUpdatePositionDelay);
            Log.v("SUMMARY","total draw delay "+mTotalDrawDelay);
        }
    }


    @Override
    public String toString() {
        ClassStateString description = new ClassStateString(TAG);
        description.addMember("mTotalTaskDelay", mTotalTaskDelay);
        description.addMember("mTotalUpdatePositionDelay", mTotalUpdatePositionDelay);
        description.addMember("mTotalDrawDelay", mTotalDrawDelay);
        return description.getString();
    }


    /**
     * Interface that responds when the task finishes execution.
     */
    public interface DrawingTaskListener {

        /**
         * called when the task finishes execution.
         * @param replay a replay of the match that ended.
         */
        void taskEnded(ArrayList<GameManager.DirectionChangeRequest> replay);
    }
}
