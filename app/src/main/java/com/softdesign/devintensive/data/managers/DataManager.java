package com.softdesign.devintensive.data.managers;


public class DataManager {

    private static DataManager INSTANCE = null;

    private PreferencesManager mPreferencesManager;

    /**
     * Create PreferencesManager
     */
    private DataManager() {
        this.mPreferencesManager = new PreferencesManager();
    }

    /**
     * @return New DataManager or this
     */
    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    /**
     * @return current preferences manager
     */
    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }
}
