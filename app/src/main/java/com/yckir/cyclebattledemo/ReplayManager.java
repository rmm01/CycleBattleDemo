package com.yckir.cyclebattledemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

public class ReplayManager {
    public static final String TAG = "GAME_REPLAY";
    private ArrayList<GameManager.DirectionChangeRequest> mList;
    private GameSurfaceView mGameSurfaceView;
    private MyHandler mHandler;

    public ReplayManager(ArrayList<GameManager.DirectionChangeRequest> list, GameSurfaceView gameSurfaceView){
        mList = list;
        mGameSurfaceView=gameSurfaceView;
        mHandler = new MyHandler();
    }

    public void play(){
        GameManager.DirectionChangeRequest request;
        long startTime = System.currentTimeMillis();
        Bundle bundle;
        Message message;
        mGameSurfaceView.newGame();
        mGameSurfaceView.start(startTime);
        Log.v(TAG, "playing game of size " + mList.size());
        for (int i = 0; i < mList.size(); i++){
            request = mList.get(i);
            bundle = new Bundle();
            bundle.putSerializable(MyHandler.Direction_KEY, request.getDirection());
            bundle.putLong(MyHandler.TIME_KEY, request.getTime() + startTime);
            bundle.putInt(MyHandler.PLAYER_KEY, request.getCycleNum());

            message = Message.obtain();
            message.arg1=i;
            message.setData(bundle);
            mHandler.sendMessageDelayed(message,request.getTime());
        }
    }

    private class MyHandler extends Handler{
        public static final String TIME_KEY = "TIME_KEY";
        public static final String Direction_KEY = "DIRECTION_KEY";
        public static final String PLAYER_KEY = "PLAYER_KEY";

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            mGameSurfaceView.requestDirectionChange(bundle.getInt(PLAYER_KEY),
                    (Compass)bundle.getSerializable(Direction_KEY), bundle.getLong(TIME_KEY));
        }
    }
}
