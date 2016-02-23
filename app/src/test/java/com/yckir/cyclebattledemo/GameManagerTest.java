package com.yckir.cyclebattledemo;

import android.graphics.Paint;

import com.yckir.cyclebattledemo.views.gameSurfaceView.GameManager;
import com.yckir.cyclebattledemo.views.gameSurfaceView.Grid;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GameManagerTest {

    private GameManager mGameManager1;
    private GameManager mGameManager2;
    private GameManager mGameManager3;

    @Mock
    Paint mMockPaint;

    @Test
    public void testHeightGreaterThanWidth() throws Exception{
        mGameManager1 = new GameManager(new Grid(5,5,10),500,500,mMockPaint);
        assertTrue(mGameManager1.getFrameWidth() <= mGameManager1.getFrameHeight());
    }

    @Test
    public void testInitialization() throws Exception {

        //test object 1
        mGameManager1 = new GameManager(new Grid(5,5,10),500,500,mMockPaint);
        assertEquals(500, mGameManager1.getFrameWidth());
        assertEquals(500, mGameManager1.getFrameHeight());

        assertEquals(500, mGameManager1.getGridPaddingX()*2 + mGameManager1.getFrameGridWidth());
        assertEquals(500, mGameManager1.getGridPaddingY() * 2 + mGameManager1.getFrameGridHeight());

        assertEquals(mGameManager1.getFrameWidth(), mGameManager1.getFrameGridWidth());
        assertEquals(mGameManager1.getFrameHeight(), mGameManager1.getFrameGridHeight());

        assertEquals(100, (int) GameManager.SCREEN_GRID_TILE.getLength());

        assertEquals(0, mGameManager1.getGridPaddingX()*2 );
        assertEquals(0, mGameManager1.getGridPaddingY() * 2);

        //test object 2
        mGameManager2 = new GameManager(new Grid(7,11,1),333,900,mMockPaint);
        assertEquals(333, mGameManager2.getFrameWidth());
        assertEquals(900, mGameManager2.getFrameHeight());

        assertEquals(333, mGameManager2.getGridPaddingX()*2+ mGameManager2.getFrameGridWidth());
        assertEquals(899, mGameManager2.getGridPaddingY() * 2 + mGameManager2.getFrameGridHeight());

        assertEquals(47, (int) GameManager.SCREEN_GRID_TILE.getLength());

        assertEquals(329, mGameManager2.getFrameGridWidth());
        assertEquals(517, mGameManager2.getFrameGridHeight());

        assertEquals(4, 2 * mGameManager2.getGridPaddingX());
        assertEquals(382, mGameManager2.getGridPaddingY() * 2);
        assertEquals(333, mGameManager2.getFrameGridWidth()+2* mGameManager2.getGridPaddingX());

        //test object 3
        mGameManager3 = new GameManager(new Grid(2,11,1),300,999,mMockPaint);
        assertEquals(300, mGameManager3.getFrameWidth());
        assertEquals(999, mGameManager3.getFrameHeight());

        assertEquals(300, 2* mGameManager3.getGridPaddingX()+ mGameManager3.getFrameGridWidth());
        assertEquals(998, mGameManager3.getGridPaddingY() * 2 + mGameManager3.getFrameGridHeight());

        assertEquals(180, mGameManager3.getFrameGridWidth());
        assertEquals(990, mGameManager3.getFrameGridHeight());

        assertEquals(90,(int) GameManager.SCREEN_GRID_TILE.getLength());

        assertEquals(120, 2* mGameManager3.getGridPaddingX());
        assertEquals(8, mGameManager3.getGridPaddingY() * 2);



    }


}