package com.acn.loadsensing.helper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import static android.content.Context.BLUETOOTH_SERVICE;

public class PermissionsHelper {

    public static final int ACCESS_COARSE_LOCATION_CODE = 1;
    public static final int BLE_REQUEST_CODE = 2;
    private Activity activity;

    public PermissionsHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean isLocationEnabled() {
        int locationMode = Settings.Secure.LOCATION_MODE_OFF;
        try {
            locationMode = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (final Settings.SettingNotFoundException e) {
            // do nothing
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public void enableLocation() {
        Log.v("***", "Location services are disabled");
    }

    public boolean hasCoarseLocationPermission() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCoarseLocationPermissions() {
        activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_CODE);
    }

    public boolean isBleEnabled() {
        final BluetoothManager bm = (BluetoothManager) activity.getSystemService(BLUETOOTH_SERVICE);
        final BluetoothAdapter ba = bm.getAdapter();
        return ba != null && ba.isEnabled();
    }

    public void enableBle() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableIntent, BLE_REQUEST_CODE);
    }
}
