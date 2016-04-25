package com.yckir.cyclebattledemo.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;

import android.os.AsyncTask;
import android.util.Log;

import com.yckir.cyclebattledemo.views.gameSurfaceView.SurfaceDrawingTask;

import java.io.File;

/**
 * Creates and retrieves files from internal private storage. A listener can be set to get the bitmap
 * of the file once the task finished. If the file exists, then the file returns the bitmap of that
 * file, if not it creates the file.
 */
public class WriteFileTask extends AsyncTask<Void,Void,Integer>{

    public static final String TAG = "WriteFileTask";

    private static final int FILE_READY = 0;
    private static final int FILE_ERROR = 1;

    private Context mContext;
    private String mFileName;
    private WriteFileListener mListener;
    private Bitmap mBitmap;


    /**
     * Creates an instance of the Async task. setListener should be called to know when the task
     * completes.
     *
     * @param context app context
     * @param filename name of file
     * @param bitmap bitmap that will be written to file if it does not exist.
     */
    public WriteFileTask(@NonNull Context context, String filename,Bitmap bitmap){
        mContext = context;
        mBitmap = bitmap;
        mFileName = filename;
    }

    /**
     * @param listener callback listener for when task finishes.
     */
    public void setListener(@NonNull WriteFileListener listener){
        mListener = listener;
    }


    @Override
    protected Integer doInBackground(Void[] params) {
            Log.v(TAG, "doInBackground for WriteFileTask");

            if(FileUtility.backgroundFileExists(mFileName, mContext)){
                File file = FileUtility.getBackgroundFile(mFileName, mContext);

                if(file == null) {
                    Log.v(TAG, "file exists but could not getBackgroundFile");
                    return FILE_ERROR;
                }
                mBitmap = BitmapFactory.decodeFile(file.getPath());

                Log.v(TAG, "file exists");
                return FILE_READY;
            }

            File file = FileUtility.getBackgroundFile(mFileName, mContext);

            //if cant make the file
            if(file == null){
                Log.v(TAG, "could not getBackgroundFile");
                return FILE_ERROR;
            }


            // if cant write image to file
            if( !FileUtility.writeBitmapToPNG(mBitmap, file )){
                FileUtility.deleteBackgroundFile(mFileName, mContext);
                Log.v(TAG, "could not writeBitmapToPNG");
                return FILE_ERROR;
            }

            mBitmap = BitmapFactory.decodeFile(file.getPath());

            Log.v(TAG, "file created");
            return  FILE_READY;

        }

    @Override
    protected void onPostExecute(Integer param) {
        if(mListener == null)
            return;

        switch (param){
            case FILE_READY:
                mListener.onFileReady(mBitmap);
                break;
            case FILE_ERROR:
                mListener.onFileReady(null);
                break;
            default:
                mListener.onFileReady(null);
                break;
        }
    }


    /**
     * callback interface used to deliver bitmap once the file been created.
     */
    public interface WriteFileListener{

        /**
         * Called when the task has finished executing.
         *
         * @param fileBitmap bitmap of the file, null if file could not be created
         */
        void onFileReady(Bitmap fileBitmap);
    }
}
