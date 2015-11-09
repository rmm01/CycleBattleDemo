package com.yckir.cyclebattledemo;

import android.graphics.Paint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GameFrameTest {

    private GameFrame mGameFrame1;
    private GameFrame mGameFrame2;
    private GameFrame mGameFrame3;

    @Mock
    Paint mMockPaint;

    @Test
    public void testHeightGreaterThanWidth() throws Exception{
        mGameFrame1 = new GameFrame(new Grid(5,5,10),500,500,mMockPaint);
        assertTrue(mGameFrame1.getFrameWidth() <= mGameFrame1.getFrameHeight());
    }

    @Test
    public void testInitialization() throws Exception {

        //test object 1
        mGameFrame1 = new GameFrame(new Grid(5,5,10),500,500,mMockPaint);
        assertEquals(500, mGameFrame1.getFrameWidth());
        assertEquals(500, mGameFrame1.getFrameHeight());

        assertEquals(500, mGameFrame1.getGridPaddingX()*2 + mGameFrame1.getFrameGridWidth());
        assertEquals(500, mGameFrame1.getGridPaddingY() * 2 + mGameFrame1.getFrameGridHeight());

        assertEquals(mGameFrame1.getFrameWidth(), mGameFrame1.getFrameGridWidth());
        assertEquals(mGameFrame1.getFrameHeight(), mGameFrame1.getFrameGridHeight());

        assertEquals(100, (int)GameFrame.SCREEN_GRID_TILE.getLength());

        assertEquals(0, mGameFrame1.getGridPaddingX()*2 );
        assertEquals(0, mGameFrame1.getGridPaddingY() * 2);

        //test object 2
        mGameFrame2 = new GameFrame(new Grid(7,11,1),333,900,mMockPaint);
        assertEquals(333, mGameFrame2.getFrameWidth());
        assertEquals(900, mGameFrame2.getFrameHeight());

        assertEquals(333, mGameFrame2.getGridPaddingX()*2+ mGameFrame2.getFrameGridWidth());
        assertEquals(899, mGameFrame2.getGridPaddingY() * 2 + mGameFrame2.getFrameGridHeight());

        assertEquals(47, (int)GameFrame.SCREEN_GRID_TILE.getLength());

        assertEquals(329, mGameFrame2.getFrameGridWidth());
        assertEquals(517, mGameFrame2.getFrameGridHeight());

        assertEquals(4, 2 * mGameFrame2.getGridPaddingX());
        assertEquals(382, mGameFrame2.getGridPaddingY() * 2);
        assertEquals(333, mGameFrame2.getFrameGridWidth()+2* mGameFrame2.getGridPaddingX());

        //test object 3
        mGameFrame3 = new GameFrame(new Grid(2,11,1),300,999,mMockPaint);
        assertEquals(300, mGameFrame3.getFrameWidth());
        assertEquals(999, mGameFrame3.getFrameHeight());

        assertEquals(300, 2*mGameFrame3.getGridPaddingX()+ mGameFrame3.getFrameGridWidth());
        assertEquals(998, mGameFrame3.getGridPaddingY() * 2 + mGameFrame3.getFrameGridHeight());

        assertEquals(180, mGameFrame3.getFrameGridWidth());
        assertEquals(990, mGameFrame3.getFrameGridHeight());

        assertEquals(90,(int)GameFrame.SCREEN_GRID_TILE.getLength());

        assertEquals(120, 2*mGameFrame3.getGridPaddingX());
        assertEquals(8, mGameFrame3.getGridPaddingY() * 2);



    }


}