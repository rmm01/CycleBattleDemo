package com.yckir.cyclebattledemo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UIHandler extends Handler {
    public static String TAG = "UIHandler";
    private GameView gameView;

    public UIHandler(GameView view){
        gameView=view;
    }

    @Override
    public void handleMessage(Message msg) {
        logMessage(msg);

        switch (msg.what){
            case UIMESSAGES.FRAME_READY_ID:
                gameView.invalidate();
                break;
        }
        super.handleMessage(msg);
    }

    private static void logMessage(Message msg){
        String details="what = "+msg.what+"\n";
        Log.v(TAG,details);
    }


    @Override
    public String toString() {
        return TAG;
    }

    public static class UIMESSAGES{
        public static final int FRAME_READY_ID=0;

        public static Message FRAME_READY_MESSAGE(){
            Message m = Message.obtain();
            m.what=FRAME_READY_ID;
            return m;
        }

    }


}
