package com.amha.helloestimote.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;

public class EstimoteActivity extends Activity {


    private static final int SCAN_WAIT_TIME = 50000;
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

    private BeaconManager beaconManager;

    TextView mText;
    TextView mRssi;
    TextView mMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimote);

        //Add Parse application key here:
        //Parse.initialize(this, "key1", "key 2");

        //Get ui text views to update with beacon data
        mText = (TextView)findViewById(R.id.beacon_count);
        mRssi = (TextView)findViewById(R.id.rssi);
        mMajor = (TextView)findViewById(R.id.major_value);

        //create new beacon manager with custom scan interval
        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(1000, SCAN_WAIT_TIME);    //1000 is the default value

        // Should be invoked in #onCreate.
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                Log.d("AMHA", "Ranged beacons: " + beacons);

                if(beacons.size() > 0) {
                    mText.setText(beacons.size() + "");

                    //TODO: Add List view of beacon results.
                    Beacon mBeacon = beacons.get(0);
                    mRssi.setText(mBeacon.getRssi() + "");
                    mMajor.setText(mBeacon.getMajor() + "");

                    /* Removing parse call for the time being.

                    //create parse object
                    ParseObject testObject = new ParseObject("DemoBeaconData");

                    //populate parse object with beacon data
                    testObject.put("rssi", mBeacon.getRssi() + "");
                    testObject.put("major", mBeacon.getMajor() + "");
                    testObject.put("minor", mBeacon.getMinor() + "");

                    //save to parse
                    //testObject.saveInBackground();
                    */
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.estimote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent mIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(mIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Start scanning for beacons.
     *
     */
    public void startTracking(View v){
        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                    Toast.makeText(getApplicationContext(), "Beacon Search is On", Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    Log.e("AMHA", "Cannot start ranging", e);
                }
            }
        });

    }


    /**
     * Stop scanning for iBeacons.
     */
    public void stopTracking(View v){

        // Should be invoked in #onStop.
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
            mText.setText("" + 0);
            Toast.makeText(getApplicationContext(), "Beacon Search is Off", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            Log.e("AMHA", "Cannot stop but it does not matter now", e);
        }

        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }

    /**
     * Event handler for Switch widget.
     */
    public void onToggleClicked(View v){

        boolean on = ((Switch) v).isChecked();

        if(on) {
            startTracking(v);
        }
        else {
            stopTracking(v);
        }
    }

    public void mapButtonClick (View v){
        Intent mIntent = new Intent(getApplicationContext(), OfficeMapActivity.class);
        startActivity(mIntent);
    }
}