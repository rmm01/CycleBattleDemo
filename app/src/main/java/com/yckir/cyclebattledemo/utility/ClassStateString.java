package com.yckir.cyclebattledemo.utility;


public class ClassStateString {
    private static final String LEFT_MARKER = "|";
    private static final String KEY_VALUE_DIVIDER = ": ";
    private static final String ln = "\n";
    private static final String TAB = "\t";
    private static int NUM_TABS=1;
    private String mDetails;
    private String mIntro;


    /**
     * Creates a formatted string of a class. The default number of tabs is 1.
     *
     * @param className the name of the class.
     */
    public ClassStateString(String className){
        mIntro = LEFT_MARKER;
        for(int i = 0; i < NUM_TABS; i++)
            mIntro+=TAB;
        mDetails = mIntro + className + KEY_VALUE_DIVIDER + ln;

    }


    /**
     * Add a member variable to the string being formatted.
     *
     * @param key the identifier of the value;
     * @param value the value of the key
     * @param <T> the generic for the type of the key
     */
    public <T> void addMember(String key, T value){
        mDetails += mIntro + key + KEY_VALUE_DIVIDER + value + ln;
    }


    /**
     * Add a class member variable to the string being formatted.
     *
     * @param className the class name
     * @param value the class object
     * @param <T> the generic for the type of the class
     */
    public <T> void addClassMember(String className, T value){
        mDetails += mIntro + ln + mIntro + className + ln;
        incrementTabs();
        mDetails += mIntro +ln + value;
        decrementTabs();
    }


    /**
     * Concatenates the a string on a new line with out any tabs.
     *
     * @param s The string that will be concatenated at the end of the string.
     */
    public void concat(String s){
        mDetails += mIntro +ln + s + mIntro +ln;
    }


    /**
     * increments the number of of tabs that will be used at the beginning of the string
     */
    public  void incrementTabs(){
        NUM_TABS++;
        mIntro+=TAB;
    }


    /**
     * Decrements the number of of tabs that will be used at the beginning of the string. The
     * minimum Number of tabs is 0;
     */
    public  void decrementTabs(){
        if(NUM_TABS==0)
            return;

        NUM_TABS--;
        mIntro = LEFT_MARKER;
        for(int i = 0; i < NUM_TABS; i++){
            mIntro+=TAB;
        }
    }


    /**
     * gets the formatted class string
     * @return the formatted class string
     */
    public String getString(){
        return mDetails;
    }
}
