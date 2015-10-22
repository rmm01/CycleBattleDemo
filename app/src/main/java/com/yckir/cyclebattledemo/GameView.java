package com.yckir.cyclebattledemo;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * A view that holds the cycle game animation.
 */
public class GameView extends View  {

    public static final String TAG = "GAME_VIEW";
    private final GameFrame mGameFrame;
    private int mGridPaddingX;
    private int mGridPaddingY;

    public static final int BORDER_WIDTH=20;
    private Paint mBoarderPaint;
    private Bitmap mGridWrapper;

    private AlarmHandler mAlarm;

    //private DrawingThread mDrawingThread;
    //private DrawingTask mDrawingTask;
    private DrawingAsyncTask mDrawingTask2;

    private int mFrame;


    private long mTotalTaskDelay;
    private long mTotalInvalidationDelay;
    private long mTotalDrawDelay;
    private long mTotalDelay;



    /**
     * Initializes attributes that were specified in xml.
     * Reads in custom attributes for the number of players,
     * number of tiles in the both directions
     *
     * @param context context of the activity
     * @param attrs   attributes specified in xml
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GameView, 0, 0);

        int numTilesX = a.getInt(R.styleable.GameView_grid_short_length, 6);
        int numTilesY = a.getInt(R.styleable.GameView_grid_long_length, 6);
        int numCycles = a.getInt(R.styleable.GameView_number_of_cycles, 1);

        a.recycle();

        //Create a Screen now in order to avoid allocation of object later on. The screen width
        //and height are unknown at this point so we use the default values
        mGameFrame = new GameFrame(numTilesX, numTilesY, numCycles);

        //UIHandler handler = new UIHandler(this);
        //mDrawingThread = new DrawingThread(handler,mGameFrame);
        //mDrawingTask = new DrawingTask(this,mGameFrame);
        mDrawingTask2 = new DrawingAsyncTask(this,mGameFrame);

        mAlarm = new AlarmHandler(new AlarmHandler.AlarmListener() {
            /**
             * starts the drawing task that will animate for 60 frames
             */
            @Override
            public void alarm() {
                mFrame=1;
                mTotalTaskDelay=0;
                mTotalInvalidationDelay=0;
                mTotalDrawDelay=0;
                mTotalDelay=0;
                mDrawingTask2.execute(mGameFrame.getStartTime());
            }
        });

        int color=getResources().getColor(R.color.colorAccent);
        mBoarderPaint= new Paint();
        mBoarderPaint.setColor(color);
    }


    /**
     * Draws a blue border surrounding the GameFrame.
     * @param width the width of the GameView.
     * @param height the height of the GameView.
     */
    private void drawBorder(int width, int height){
        mGridWrapper = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(mGridWrapper);

        int padX=mGridPaddingX/2;
        int padY=mGridPaddingY/2;

        //left wall
        c.drawRect(
                padX - BORDER_WIDTH,
                padY - BORDER_WIDTH,
                padX ,
                getHeight()-padY+BORDER_WIDTH ,
                mBoarderPaint);

        //right wall
        c.drawRect(
                getWidth() - padX,
                padY - BORDER_WIDTH,
                getWidth() - padX + BORDER_WIDTH,
                getHeight() - padY + BORDER_WIDTH,
                mBoarderPaint);

        //top wall
        c.drawRect(
                padX - BORDER_WIDTH,
                padY - BORDER_WIDTH,
                getWidth() - padX + BORDER_WIDTH,
                padY,
                mBoarderPaint);

        //bottom wall
        c.drawRect(
                padX - BORDER_WIDTH,
                getHeight() - padY,
                getWidth() - padX + BORDER_WIDTH,
                getHeight() - padY + BORDER_WIDTH,
                mBoarderPaint);

        //mGridWrapper=mGridWrapper.copy(Bitmap.Config.ARGB_8888,false);
        //mGridWrapper=Bitmap.createBitmap(mGridWrapper);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        //Log.v(TAG, "|\n|\tonMeasure:  newW=" + width + ", newH=" + height +
        //       "\n|\tonMeasure:  oldW=" + getWidth() + ", oldH=" + getHeight());

        if(width!= mGameFrame.getWidth()||height!= mGameFrame.getHeight()) {
            //the min padding between the screen edge and content what will be animated
            // is set to border thickness
            mGridPaddingX = Math.max(width / 10, BORDER_WIDTH);
            mGridPaddingY = Math.max(height / 10, BORDER_WIDTH);
            mGameFrame.setFrameSize(width - mGridPaddingX, height - mGridPaddingY);

            drawBorder(width, height);
            //Log.v(TAG, mGameFrame.toString());
        }

        //since we didn't alter the width or height, we can call the super instead of setDimension
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //use single drawing task
        long drawStartTime=System.currentTimeMillis();
        //make sure the drawing task isn't drawing while you access the frame
        synchronized (mGameFrame) {

            Log.v("QWERTY", "-" + drawStartTime%1000 + "- start onDraw Frame " + mFrame);
            canvas.drawBitmap(mGridWrapper, 0, 0, null);
            canvas.drawBitmap(mGameFrame.getFrameBitmap(), mGridPaddingX / 2, mGridPaddingY / 2, null);

            long drawEndTime=System.currentTimeMillis();
            Log.v("QWERTY", "-" + (drawEndTime%1000) +"- finish onDraw Frame " +mFrame );

            mFrame++;

            if(mAlarm.isAlarmOn()){

                logFrameInfo(mFrame-1,
                        mDrawingTask2.getFrameEndTime()-mDrawingTask2.getFrameStartTime(),
                        drawStartTime-mDrawingTask2.getFrameEndTime(),
                        drawEndTime-drawStartTime);

                mGameFrame.notify();
            }


        }

    }


    /**
     * Starts the game after an initial delay.
     * @param delay time in milliseconds until the game starts
     */
    public void start(int delay) {
        long startTime = System.currentTimeMillis()+delay;
        mGameFrame.setStartTime(startTime);
        //Log.v(TAG, "StartTime: " + startTime);
        mAlarm.setAlarm(delay);
    }


    /**
     * Stops the game
     */
    public void stop() {
        mAlarm.turnOffAlarm();
    }


    /**
     * Logs the delays associated with animating a frame.
     * On the 60th frame, the total delays will be logged
     *
     * @param frameNum the frame number
     * @param taskDelay the time it takes drawing task to finish one frame
     * @param invalidationDelay the time it takes view.invalidate() to call ondraw
     * @param drawDelay the time ondraw takes to finish
     */
    private void logFrameInfo(int frameNum,long taskDelay,long invalidationDelay, long drawDelay){
        long totalDelay=taskDelay+invalidationDelay+drawDelay;
        String s_frameNum="Frame " + frameNum+": ";
        String s_taskDelay="task delay = " + taskDelay;
        String s_invalidationDelay="invalidation delay = "+invalidationDelay;
        String s_drawDelay="draw delay = " + drawDelay;
        String s_totalDelay="total delay = "+totalDelay;

        Log.v("FRAME_INFO",s_frameNum+s_taskDelay);
        Log.v("FRAME_INFO",s_frameNum+s_invalidationDelay);
        Log.v("FRAME_INFO",s_frameNum+s_drawDelay);
        Log.v("FRAME_INFO",s_frameNum+s_totalDelay);

        mTotalTaskDelay+=taskDelay;
        mTotalInvalidationDelay+=invalidationDelay;
        mTotalDrawDelay+=drawDelay;
        mTotalDelay+=totalDelay;

        if(frameNum==60){
            Log.v("SUMMARY","total task delay "+mTotalTaskDelay);
            Log.v("SUMMARY","total invalidation delay "+mTotalInvalidationDelay);
            Log.v("SUMMARY","total draw delay "+mTotalDrawDelay);
            Log.v("SUMMARY","total delay "+mTotalDelay);
        }
    }


    @Override
    public String toString() {
        String info = "~/n \t"+TAG;
        info+="\n \tpaddingX="+mGridPaddingX+", paddingy="+mGridPaddingY;
        return info;
    }


}
