package com.yckir.cyclebattledemo.utility;


import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.yckir.cyclebattledemo.views.gameSurfaceView.Cycle;

/**
 * Used to store data for a finished game. Stores place, name, and crashTime from cycles into
 * arrays that are accessed get methods. the zeroth element of each array contains a label for the
 * data,
 */
public class GameResultsData {

    public  static final String TAG = "GAME_DATA";

    private static final String PLACE_LABEL = "Place";
    private static final String NAME_LABEL = "Player";
    private static final String TIME_LABEL = "Time";
    private static final String WINS_LABEL = "Wins";
    private static final String DEFAULT_PLACE = "0";

    private String[] mPlaces;
    private String[] mNames;
    private String[] mDurations;



    /**
     * Reads the cycles array and stores their name, place, and crashTime. These stored values are
     * stored in arrays and are sorted based on their place.
     *
     * @param cycles the cycles that will be read.
     */
    public GameResultsData(Cycle[] cycles ){
        int dataSize = cycles.length + 1;

        mPlaces = new String[dataSize];
        mNames = new String[dataSize];
        mDurations = new String[dataSize];

        mPlaces[0] = PLACE_LABEL;
        mNames[0] = NAME_LABEL;
        mDurations[0] = TIME_LABEL;

        for(int i =1; i < dataSize; i++){
            mPlaces[i] = DEFAULT_PLACE;
        }

        for (Cycle cycle : cycles) {
            readCycleData( cycle );
        }

    }


    /**
     * Read and store the cycles name, place, and crashTime variables.
     *
     * @param cycle the cycle that will be read.
     */
    private void readCycleData( Cycle cycle ){
        int place = cycle.getPlace();
        int index = getPlaceIndex(place);

        mPlaces[index] =  formatPlace(place);
        mNames[index] = cycle.getName();
        mDurations[index] = formatTime(cycle.getCrashTime());
    }


    /**
     * Formats the place to a string that will be displayed to the user.
     * This will be in the format of "   place"
     *
     * @param place the place that the cycle finished in
     * @return the formatted parameter.
     */
    public static String formatPlace(int place){
        return "   " + Integer.toString(place);
    }


    /**
     * Formats the time into a string into the format "nn.nn" (n = digit). If the time is equal
     * to the cycles default value, the formatted result will be "- - - -".
     *
     * @param time the time in milliseconds to format
     * @return the formatted value of the parameter
     */
    public static String formatTime( long time ){
        if(time == Cycle.DEFAULT_TIME){
            return "- - - -";
        }else{
            double seconds = time / 1000.0;
            String formattedTime = Double.toString(seconds);
            int length = ( formattedTime.length() > 4 ) ? 4 : formattedTime.length();
            return formattedTime.substring(0,length);
        }
    }


    /**
     * Formats the parameter to a string that will be displayed to the user.
     * This will be in the format of "   param"
     *
     * @param wins the number of wins for the cycle
     * @return the formatted parameter
     */
    public static String formatWins( int wins){
        return "   " + Integer.toString(wins);
    }


    /**
     * Get the data array index for a given place. Ties can happen causing multiple cycles
     * to have the same place. This function will search for the next available index in this case.
     *
     * @param place the place of a cycle.
     * @return the data array index to store itd data
     */
    private int getPlaceIndex( int place ){

        for(int index = place; index < mPlaces.length; index++){

            if( mPlaces[index].compareTo(DEFAULT_PLACE) == 0 )
                return index;
        }
        Log.e(TAG, "unable to find place for " + place);
        return 0;
    }


    /**
     * Initializes the map to contain a zero for each cycle name this object currently has.
     *
     * @param map map to be initialized.
     */
    public void initMap(ArrayMap<String, Integer> map){
        for (String name : mNames){
            map.put(name ,0);
        }
    }


    /**
     *  For each entry in mPlaces that has a value of 1, its value in the map will be incremented.
     *
     * @param winsMap map containing the number of wins for each cycle, indexed by its name.
     * @return formatted string array for the number of wins.
     */
    public String[] updateWins(ArrayMap<String, Integer> winsMap){
        String[] winsArray = new String[mPlaces.length];
        winsArray[0] = WINS_LABEL;
        int numWins;

        for(int i = 1; i < mPlaces.length; i++){

            numWins =  winsMap.get( mNames[i]);

            if(mPlaces[i].compareTo(formatPlace(1)) == 0 ){
                numWins += 1;
                winsMap.put( mNames[i], numWins);
            }
            winsArray[i] = formatWins(numWins);
        }
        return winsArray;
    }


    /**
     * @return the durations array for hte cycles, ordered by the cycles places.
     */
    public String[] getDurations() {
        return mDurations;
    }


    /**
     * @return the place array for hte cycles.
     */
    public String[] getPlace() {
        return mPlaces;
    }


    /**
     * @return the durations array for hte cycles, ordered by the cycles places.
     */
    public String[] getNames() {
        return mNames;
    }
}