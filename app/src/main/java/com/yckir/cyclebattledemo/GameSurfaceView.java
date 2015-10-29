package com.yckir.cyclebattledemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * View that displays the cycle game. Drawing is done on an AsyncTask. To start and stop the game,
 * the user must call start and stop
 */
public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    public static final String TAG = "GAME_SURFACE_VIEW";
    private GameFrame mGameFrame;
    private RectangleContainer mRectangleContainer;
    private SurfaceDrawingTask mSurfaceDrawingTask;



    /**
     * constructs the view. Custom xml attributes are read. default values are
     * tiles_x= 6, tiles_y= 6, cycles -1, border_length=10 and all colors are black. GameContainer, GameFrame,
     * and SurfaceDrawingTask are constructed using these attributes.
     *
     * @param context context
     * @param attrs xml attributes
     */
    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

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

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        //width and height are unknown so the default size for frame and container is used
        mGameFrame = new GameFrame(numTilesX, numTilesY, numCycles);
        mRectangleContainer = new RectangleContainer(boarderColor,backgroundColor,borderSize);

        mSurfaceDrawingTask=new SurfaceDrawingTask(holder,mGameFrame, mRectangleContainer);
    }

    /**
     * start the animation after a delay. The delay is not used at the moment. All calls to start
     * have effectivly 0 delay
     *
     * @param delay time in milliseconds until the game starts
     */
    public void start(long delay){
        long startTime=System.currentTimeMillis();
        mGameFrame.setStartTime(startTime);
        mSurfaceDrawingTask.execute(startTime);
    }


    /**
     * Stop the game. not functional at the moment.
     */
    public void stop(){
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
}
