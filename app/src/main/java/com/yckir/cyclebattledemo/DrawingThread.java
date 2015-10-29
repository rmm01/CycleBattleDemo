package com.yckir.cyclebattledemo;

import android.os.Message;
import android.util.Log;



public class DrawingThread extends Thread{
    public static String TAG = "DrawingThread";
    private static UIHandler  mHandler;
    private static GameFrame mGameFrame;

    public DrawingThread(UIHandler handler,GameFrame gameFrame){
        Log.v(TAG, "creating DrawingThread");
        //Log.v(TAG,getThreadSignature());
        mGameFrame=gameFrame;
        mHandler=handler;
    }

    @Override
    public void run() {
        Log.v(TAG, "running");
        Log.v(TAG, getThreadSignature());
        Log.v(TAG, "sleeping: "+System.currentTimeMillis());
        sleepForInSecs(5);
        Log.v(TAG, "done sleeping: " + System.currentTimeMillis());
        sendFrameReadyMessage();
        Log.v(TAG, "done:");
    }

    public void sendFrameReadyMessage(){
        Log.v(TAG, "sendFrameReadyMessage");
        Log.v(TAG, getThreadSignature());
        Message msg = UIHandler.UIMESSAGES.FRAME_READY_MESSAGE();
        mHandler.sendMessage(msg);
    }

    public String getThreadSignature() {
        Thread t = Thread.currentThread();
        long l = t.getId();
        String name = t.getName();
        long p = t.getPriority();
        String gname = t.getThreadGroup().getName();
        return (name + ":(id)" + l + ":(priority)" + p
                + ":(group)" + gname);
    }

    public void sleepForInSecs(int secs) {
        try
        {
            Thread.sleep(secs * 1000);
        }
        catch(InterruptedException x)
        {
            throw new RuntimeException("interrupted",x);
        }
    }

    @Override
    public String toString() {return TAG;}
}
