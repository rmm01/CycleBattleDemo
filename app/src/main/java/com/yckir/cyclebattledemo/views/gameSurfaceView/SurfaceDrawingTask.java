package com.yckir.cyclebattledemo.views.gameSurfaceView;


import android.graphics.Canvas;

import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yckir.cyclebattledemo.utility.ClassStateString;
import com.yckir.cyclebattledemo.utility.FourRegionSwipeDetector;

import java.util.ArrayList;

/**
 * Draws the cycle game animation on a separate thread. This iteration currently only animates
 * a total of 60 frames.
 */
public class SurfaceDrawingTask extends AsyncTask<Long, Void, Void>{
    public static String TAG="SURFACE_DRAWING_TASK";

    private final SurfaceHolder mSurfaceHolder;
    private final GameManager mGameManager;
    private final RectangleContainer mRectangleContainer;
    private DrawingTaskListener mListener;
    private FourRegionSwipeDetector mDetector = null;

    private long mTotalTaskDelay;
    private long mTotalUpdatePositionDelay;
    private long mTotalDrawDelay;

    /**
     * Constructs drawing task that draws on the canvas provided by a surface holder.
     *
     * @param holder used to retrieve canvas to draw on
     * @param gameManager holds the game information and knows how to draw the game into a canvas
     * @param rectangleContainer centers the game frame on a canvas and draws a border surrounding it
     */
    public SurfaceDrawingTask(SurfaceHolder holder, GameManager gameManager,RectangleContainer rectangleContainer){
        mSurfaceHolder=holder;
        mGameManager = gameManager;
        mRectangleContainer = rectangleContainer;
        mListener=null;
    }


    /**
     * Add a listener that will be notified when the task ends.
     *
     * @param listener the listener that will be notified
     */
    public void addListener(DrawingTaskListener listener){
        mListener = listener;
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
            mGameManager.checkDirectionChangeRequests();
            mGameManager.move(frameStartTime);
            mGameManager.collisionDetection();

            Canvas canvas = mSurfaceHolder.lockCanvas();
            drawFrame(canvas);
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
    protected void onPostExecute(Void aVoid) {
        ArrayList<GameManager.DirectionChangeRequest> list = mGameManager.getReplay();
        //for(int i = 0; i < list.size(); i++)
        //    Log.v(TAG,list.get(i).toString());
        if(mListener!=null){
            mListener.taskEnded(list);
        }
    }


    /**
     * draws the boarder and game frame on the canvas provided
     */
    public void drawFrame(Canvas canvas){
        mRectangleContainer.drawBorder( canvas );

        canvas.save();
        canvas.clipRect(mRectangleContainer.getLeft(), mRectangleContainer.getTop(),
                mRectangleContainer.getRight(), mRectangleContainer.getBottom());

        mGameManager.drawFrame( canvas );
        canvas.restore();
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
