package com.yckir.cyclebattledemo;


import android.os.AsyncTask;
import android.util.Log;

/**
 * Task that draws the contents of a frame in a GameView animation.
 */
public class DrawingAsyncTask extends AsyncTask<Long,Void,Void>{
    public static String TAG = "DRAWING_ASYNC_TASK";
    private int frame;
    private GameView mGameView;
    private final GameFrame mGameFrame;
    private long mFrameStartTime;
    private long mFrameEndTime;


    /**
     * Constructs a Task that will create 60 frames of the GameView animation
     *
     * @param gameView The View that holds the animation
     * @param gameFrame The class that holds all the information required to draw an animation frame
     */
    public DrawingAsyncTask(GameView gameView, GameFrame gameFrame){
        frame=1;
        mGameView=gameView;
        mGameFrame=gameFrame;
    }


    /**
     * Creates 60 frames of the gameView animation. Once a frame is created, it invalidates the
     * gameView and it waits for the view to notify it that it has finished onDraw();
     *
     * @param params the First value is the start time of the animation.
     * @return null
     */
    @Override
    protected Void doInBackground(Long... params) {
        long totalTaskTime;
        long frameCreationDelay;
        Log.v(TAG,getThreadSignature());
        frame=0;
        while(frame<60){

            synchronized (mGameFrame) {
                frame++;
                mFrameStartTime = System.currentTimeMillis();


                totalTaskTime = mFrameStartTime - params[0];

                Log.v("QWERTY", "|" + (mFrameStartTime % 1000) + "| start task Frame " + frame + " at animation time " + totalTaskTime);

                //move the cycles and draw the frame
                mGameFrame.move(mFrameStartTime, 0);
                mGameFrame.drawFrame();

                mFrameEndTime=System.currentTimeMillis();

                frameCreationDelay = mFrameEndTime- mFrameStartTime;
                Log.v("QWERTY", "|" + (mFrameEndTime % 1000) + "| finish task Frame " + frame + " in  " + frameCreationDelay);
                publishProgress();
                try {
                    Log.v("QWERTY","Task is waiting");
                    mGameFrame.wait();
                    Log.v("QWERTY", "Task is done waiting");
                } catch (InterruptedException e) {
                    Log.v(TAG,"wait exception");
                    e.printStackTrace();
                }

            }
        }

        return null;
    }


    /**
     * invalidates the gameView
     * @param values null
     */
    @Override
    protected void onProgressUpdate(Void... values) {
            mGameView.invalidate();
    }


    /**
     * @return the time in milliseconds of the start of the animation
     */
    public long getFrameStartTime() {
        return mFrameStartTime;
    }


    /**
     *
     * @return  the time in milliseconds of the end of the animation
     */
    public long getFrameEndTime() {
        return mFrameEndTime;
    }


    /**
     * return details of the current thread
     * @return details about the current thread
     */
    public String getThreadSignature() {
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();
        long p = t.getPriority();
        String gname = t.getThreadGroup().getName();
        return (name + ":(id)" + l + ":(priority)" + p
                + ":(group)" + gname);
    }
}
