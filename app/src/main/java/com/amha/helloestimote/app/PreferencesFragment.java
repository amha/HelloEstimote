package com.amha.helloestimote.app;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * @date: 10/23/14
 * @author Amha Mogus
 */
public class PreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
