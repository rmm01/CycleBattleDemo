package com.yckir.cyclebattledemo;


import android.graphics.Canvas;

import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Draws the cycle game animation on a separate thread. This iteration currently only animates
 * a total of 60 frames.
 */
public class SurfaceDrawingTask extends AsyncTask<Long, Void, Void>{
    public static String TAG="SURFACE_DRAWING_TASK";

    private final SurfaceHolder mSurfaceHolder;
    private final GameFrame mGameFrame;
    private final RectangleContainer mRectangleContainer;
    private DrawingTaskListener mListener;

    private long mTotalTaskDelay;
    private long mTotalUpdatePositionDelay;
    private long mTotalDrawDelay;

    /**
     * Constructs drawing task that draws on the canvas provided by a surface holder.
     *
     * @param holder used to retrieve canvas to draw on
     * @param gameFrame holds the game information and knows how to draw the game into a canvas
     * @param rectangleContainer centers the GameFrame on a canvas and draws a border surrounding it
     */
    public SurfaceDrawingTask(SurfaceHolder holder, GameFrame gameFrame,RectangleContainer rectangleContainer){
        mSurfaceHolder=holder;
        mGameFrame=gameFrame;
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
        while (mGameFrame.isRunning()){
            frameStartTime=System.currentTimeMillis() - start;
            mGameFrame.checkDirectionChangeRequests();
            mGameFrame.move( frameStartTime );
            mGameFrame.collisionDetection();
            drawFrame();
        }
        Log.v(TAG,"Done with task");
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        if(mListener!=null){
            mListener.taskEnded();
        }
    }


    //Alternate but identical version of doInBackGround. Used for logging
    protected Void doInBackgroundDebug(Long... params) {
        Log.v(TAG, "doInBackGround");
        long start =params[0];
        int count=0;
        Canvas surfaceCanvas;
        long frameStartTime;
        long frameEndTime;

        long actionEnd;

        long drawStart;
        long drawEnd;
        while (count<60){
            count++;
            frameStartTime=System.currentTimeMillis() - start;
            mGameFrame.checkDirectionChangeRequests();
            mGameFrame.move( frameStartTime );
            actionEnd = System.currentTimeMillis() - start;

            //surfaceCanvas = null;
            surfaceCanvas = mSurfaceHolder.lockCanvas();
            drawStart = System.currentTimeMillis();
            doDraw(surfaceCanvas);
            drawEnd = System.currentTimeMillis();

            if( surfaceCanvas != null )
                mSurfaceHolder.unlockCanvasAndPost( surfaceCanvas );
            frameEndTime = System.currentTimeMillis() - start;

            logFrameInfo(count, frameStartTime, frameEndTime - frameStartTime, actionEnd - frameStartTime,
                    drawEnd - drawStart);
        }
        return null;
    }


    /**
     * draws the boarder and GameFrame on a canvas
     *
     * @param canvas the canvas that will be drawn onto. It is assumed that the user has already
     *               set the size of the RectangleContainer and GameFrame to match the canvas.
     */
    public void doDraw(Canvas canvas){
        mRectangleContainer.drawBorder(canvas);
        canvas.save();
        canvas.clipRect(mRectangleContainer.getLeft(), mRectangleContainer.getTop(),
                mRectangleContainer.getRight(), mRectangleContainer.getBottom());

        mGameFrame.drawFrame( canvas );
        //draw path
        canvas.restore();
    }


    /**
     * draws the boarder and GameFrame on the canvas provided by the SurfaceHolder
     */
    public void drawFrame(){

        Canvas canvas = mSurfaceHolder.lockCanvas();

        mRectangleContainer.drawBorder( canvas );

        canvas.save();
        canvas.clipRect(mRectangleContainer.getLeft(), mRectangleContainer.getTop(),
                mRectangleContainer.getRight(), mRectangleContainer.getBottom());

        mGameFrame.drawFrame( canvas );
        //draw path
        canvas.restore();

        mSurfaceHolder.unlockCanvasAndPost( canvas );
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
    interface DrawingTaskListener {

        /**
         * called when the task finishes execution.
         */
        void taskEnded();

    }
}