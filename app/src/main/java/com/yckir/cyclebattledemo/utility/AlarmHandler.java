package com.yckir.cyclebattledemo.utility;


import android.os.Handler;
import android.os.Message;

/**
 * Notifies a listener when a set amount of time has expired.
 */
public class AlarmHandler extends Handler {
    public static final String TAG="ALARM_HANDLER";
    private AlarmListener mTimerListener;


    /**
     * constructs a AlarmHandler by setting a listener.
     * @param listener the listener that will be notified when the wait time is over
     */
    public AlarmHandler(AlarmListener listener){
        mTimerListener=listener;
    }


    /**
     * Notifies the listeners that the wait time is over if the alarm is still on.
     *
     * @param msg empty message
     */
    @Override
    public void handleMessage(Message msg) {
        if(mTimerListener != null)
            mTimerListener.alarm( msg.what );
    }


    /**
     * Sets how long to wait until the AlarmListener is notified.
     *
     * @param delay how long to wait in milliseconds until the AlarmListener is notified
     * @param id the id for the for the alarm
     */
    public  void setAlarm(long delay, int id){
        sendEmptyMessageDelayed(id, delay);
    }


    /**
     * Listener used ny AlarmHandler to notify that a waiting period is over
     */
    public interface AlarmListener {
        /**
         * Will be called by AlarmHandler when a waiting period is over
         *
         * @param id the id of the alarm.
         */
        void alarm(int id);
    }

}
