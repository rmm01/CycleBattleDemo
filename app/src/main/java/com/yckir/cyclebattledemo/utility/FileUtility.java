package com.yckir.cyclebattledemo.utility;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtility {

    private static final String TAG = "FILE_UTILITY";
    private static final String BACKGROUND_PATH = "background/";


    /**
     * Creates all the directories used by the application if they don't already exist.
     *
     * @param context app context
     */
    public static void createDirectories(Context context){
        File file = new File(context.getFilesDir(),BACKGROUND_PATH);

        if( !file.mkdirs() && !file.isDirectory())
            Log.e(TAG, "createDirectories: failed to create directory " + BACKGROUND_PATH);
    }



    /**
     * Writes the bitmap onto the given file in PNG format.
     *
     * @param bitmap the bitmap that contains the image.
     * @param file the file where the image will be created.
     * @return true if the image file was successfully created, false otherwise.
     */
    public static boolean writeBitmapToPNG(Bitmap bitmap, File file){
        Log.v(TAG, "writing image file " + file.getName());

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Delete the specified File from the internal private storage directory..
     *
     * @param fileName the name of the file
     * @param context app context
     */
    public static void deleteBackgroundFile(String fileName, Context context){
        fileName = BACKGROUND_PATH + fileName;
        Log.v(TAG, "dbf:" + fileName + " is being deleted");
        if(context.getFilesDir().exists())
            if( !context.getFilesDir().delete())
                Log.e(TAG,"deleteBackgroundFile: error could not delete the file " + fileName);
    }


    /**
     * create a File instance whose path is in the background directory of internal private storage.
     *
     * @param fileName the name of the file.
     * @param context app context
     * @return the file that was specified, null if the directory could not be created.
     */
    public static File getBackgroundFile(String fileName, Context context) {
        File file = new File(context.getFilesDir(), BACKGROUND_PATH);
        if(file.exists() && file.isDirectory())
            return new File(file,fileName);
        return null;
    }


    /**
     * Check to see if a file exists for the given file name in the background directory of
     * the internal private storage.
     *
     * @param fileName the name of the file.
     * @param context app context
     * @return true if the file exists, false otherwise.
     */
    public static boolean backgroundFileExists(String fileName, Context context){
        fileName = BACKGROUND_PATH + fileName;
        File backgroundImageFile = new File(context.getFilesDir(),fileName);
        boolean b  =  backgroundImageFile.exists();

        Log.v(TAG, "bfe:" + fileName + " exists = " + b);

        return b;
    }
}
