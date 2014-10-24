/**
 * Copyright 2014 Amha Mogus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/
package com.amha.helloestimote.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
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

    // Default value provided by Estimote.
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";

    // Setting the scan region or area variables/
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

    // The amount of time to wait, in miliseconds, before the we scan for beacons.
    private static final int SCAN_WAIT_TIME = 50000;

    // Interface with beacon ranging mechanism.
    private BeaconManager beaconManager;

    // Used for bluetooth request.
    private final static int REQUEST_ENABLE_BT = 1;

    // Console Hook.
    private static final String TAG = "AMHA-OUTPUT";

    // TODO: Save these values in a data structure.
    TextView mText;
    TextView mRssi;
    TextView mMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimote);

        //Add Parse application key here:
        //Parse.initialize(this, "key1", "key 2");

        // Check Bluetooth has been turned on.
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            // Create Alert Dialog.
            AlertDialog.Builder builder = new AlertDialog.Builder(EstimoteActivity.this);
            builder.setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_message)
                    .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d(TAG, "Negative Button Pressed.");

                                }
                            }
                    ).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "Positive Button Pressed.");
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                    //TODO: Show toast message when bluetooth has been enabled.
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

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