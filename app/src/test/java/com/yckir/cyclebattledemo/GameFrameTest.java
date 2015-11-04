package com.yckir.cyclebattledemo;

import android.graphics.Paint;

import org.junit.Before;
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

    @Before
    public void init(){
        mGameFrame1 = new GameFrame(new Grid(5,5,10),500,500,mMockPaint,mMockPaint);
        mGameFrame2 = new GameFrame(new Grid(7,11,1),333,900,mMockPaint,mMockPaint);
        mGameFrame3 = new GameFrame(new Grid(2,11,1),300,999,mMockPaint,mMockPaint);
    }

    @Test
    public void testHeightGreaterThanWidth() throws Exception{
        mGameFrame1 = new GameFrame(new Grid(5,5,10),500,500,mMockPaint,mMockPaint);
        assertTrue(mGameFrame1.getWidth() <= mGameFrame1.getHeight());
    }

    @Test
    public void testInitialization() throws Exception {

        //test object 1
        mGameFrame1 = new GameFrame(new Grid(5,5,10),500,500,mMockPaint,mMockPaint);
        assertEquals(500, mGameFrame1.getWidth());
        assertEquals(500, mGameFrame1.getHeight());

        assertEquals(500, mGameFrame1.getGridPaddingX()+ mGameFrame1.getFrameGridWidth());
        assertEquals(500, mGameFrame1.getGridPaddingY() + mGameFrame1.getFrameGridHeight());

        assertEquals(mGameFrame1.getWidth(), mGameFrame1.getFrameGridWidth());
        assertEquals(mGameFrame1.getHeight(), mGameFrame1.getFrameGridHeight());

        assertEquals(100, mGameFrame1.getFrameTileLength());

        assertEquals(0, mGameFrame1.getGridPaddingX());
        assertEquals(0, mGameFrame1.getGridPaddingY());

        //test object 2
        mGameFrame2 = new GameFrame(new Grid(7,11,1),333,900,mMockPaint,mMockPaint);
        assertEquals(333, mGameFrame2.getWidth());
        assertEquals(900, mGameFrame2.getHeight());

        assertEquals(333, mGameFrame2.getGridPaddingX()+ mGameFrame2.getFrameGridWidth());
        assertEquals(900, mGameFrame2.getGridPaddingY() + mGameFrame2.getFrameGridHeight());

        assertEquals(47, mGameFrame2.getFrameTileLength());

        assertEquals(329, mGameFrame2.getFrameGridWidth());
        assertEquals(517, mGameFrame2.getFrameGridHeight());

        assertEquals(4, mGameFrame2.getGridPaddingX());
        assertEquals(383, mGameFrame2.getGridPaddingY());
        assertEquals(333, mGameFrame2.getFrameGridWidth()+ mGameFrame2.getGridPaddingX());

        //test object 3
        mGameFrame3 = new GameFrame(new Grid(2,11,1),300,999,mMockPaint,mMockPaint);
        assertEquals(300, mGameFrame3.getWidth());
        assertEquals(999, mGameFrame3.getHeight());

        assertEquals(300, mGameFrame3.getGridPaddingX()+ mGameFrame3.getFrameGridWidth());
        assertEquals(999, mGameFrame3.getGridPaddingY() + mGameFrame3.getFrameGridHeight());

        assertEquals(180, mGameFrame3.getFrameGridWidth());
        assertEquals(990, mGameFrame3.getFrameGridHeight());

        assertEquals(90, mGameFrame3.getFrameTileLength());

        assertEquals(120, mGameFrame3.getGridPaddingX());
        assertEquals(9, mGameFrame3.getGridPaddingY());



    }


}