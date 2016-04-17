package com.yckir.cyclebattledemo.utility;


import android.os.Handler;
import android.os.Message;

/**
 * Notifies a listener when a set amount of time has expired.
 */
public class AlarmHandler extends Handler {
    public static final String TAG="ALARM_HANDLER";
    private boolean started;
    private AlarmListener mTimerListener;


    /**
     * constructs a AlarmHandler by setting a listener.
     * @param listener the listener that will be notified when the wait time is over
     */
    public AlarmHandler(AlarmListener listener){
        started=false;
        mTimerListener=listener;
    }


    /**
     * Notifies the listeners that the wait time is over if the alarm is still on.
     *
     * @param msg empty message
     */
    @Override
    public void handleMessage(Message msg) {
        if(started && mTimerListener != null)
            mTimerListener.alarm( msg.what );
    }


    /**
     * Turn on the alarm and sets how long to wait until the AlarmListener is notified.
     * The Alarm is considered to be on until turnOffAlarm() is called.
     *
     * @param delay how long to wait in milliseconds until the AlarmListener is notified
     * @param id the id for the for the alarm
     */
    public  void setAlarm(long delay, int id){
        started=true;
        sendEmptyMessageDelayed(id, delay);
    }


    /**
     * Turns off the alarm. AlarmListener will not be been notified if it hasn't already.
     */
    public void turnOffAlarm(){
        started=false;
    }


    /**
     * returns whether the alarm is on
     * @return true if the alarm is on, false otherwise
     */
    public boolean isAlarmOn(){
        return started;
    }


    /**
     * Listener used ny AlarmHandler to notify that a waiting period is over
     */
    interface AlarmListener {
        /**
         * Will be called by AlarmHandler when a waiting period is over
         *
         * @param id the id of the alarm.
         */
        void alarm(int id);
    }

}
