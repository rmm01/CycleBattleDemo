package com.yckir.cyclebattledemo;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * A view that holds the cycle game animation.
 */
public class GameView extends View {

    public static final String TAG = "GAME_VIEW";
    private GameFrame mGameFrame;
    private int mGridPaddingX;
    private int mGridPaddingY;

    public static final int BORDER_WIDTH=10;
    private Bitmap mGridWrapper;


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
        //Log.v(TAG, "initializing game view");

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.GameView, 0, 0);

        int numTilesX = a.getInt(R.styleable.GameView_grid_short_length, 6);
        int numTilesY = a.getInt(R.styleable.GameView_grid_long_length, 6);
        int numCycles = a.getInt(R.styleable.GameView_number_of_cycles, 1);

        a.recycle();

        //Create a Screen now in order to avoid allocation of object later on. The screen width
        //and height are unknown at this point so we use the default values
        mGameFrame = new GameFrame(numTilesX, numTilesY, numCycles);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        Log.v(TAG, "|\n|\tonMeasure:  newW=" + width + ", newH=" + height +
                "\n|\tonMeasure:  oldW=" + getWidth() + ", oldH=" + getHeight());

        if(width!= mGameFrame.getWidth()||height!= mGameFrame.getHeight()) {
            //the min padding between the screen edge and content what will be animated
            // is set to border thickness
            mGridPaddingX = Math.max(width / 10, BORDER_WIDTH);
            mGridPaddingY = Math.max(height / 10, BORDER_WIDTH);
            mGameFrame.setFrameSize(width - mGridPaddingX, height - mGridPaddingY);

            drawBorder(width,height);
            Log.v(TAG, mGameFrame.toString());
        }

        //since we didn't alter the width or height, we can call the super instead of setDimension
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Log.v(TAG, "OnDraw:");
        canvas.drawBitmap(mGridWrapper,0,0,null);
        canvas.drawBitmap(mGameFrame.getFrameBitmap(), mGridPaddingX/2, mGridPaddingY/2, null);
    }

    /**
     * Draws a blue border surrounding the GameFrame.
     * @param width the width of the GameView.
     * @param height the height of the GameView.
     */
    private void drawBorder(int width, int height){
        mGridWrapper = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(mGridWrapper);
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        int padX=mGridPaddingX/2;
        int padY=mGridPaddingY/2;

        //left wall
        c.drawRect(
                padX - BORDER_WIDTH,
                padY - BORDER_WIDTH,
                padX ,
                getHeight()-padY+BORDER_WIDTH ,
                p);

        //right wall
        c.drawRect(
                getWidth()- padX,
                padY - BORDER_WIDTH,
                getWidth()- padX+BORDER_WIDTH,
                getHeight()-padY+BORDER_WIDTH ,
                p);

        //top wall
        c.drawRect(
                padX - BORDER_WIDTH,
                padY - BORDER_WIDTH,
                getWidth()-padX + BORDER_WIDTH ,
                padY ,
                p);

        //bottom wall
        c.drawRect(
                padX - BORDER_WIDTH,
                getHeight()-padY,
                getWidth() - padX + BORDER_WIDTH,
                getHeight()-padY + BORDER_WIDTH ,
                p);

    }

    @Override
    public String toString() {
        String info = "~/n \t"+TAG;
        info+="\n \tpaddingX="+mGridPaddingX+", paddingy="+mGridPaddingY;
        return info;
    }
}
