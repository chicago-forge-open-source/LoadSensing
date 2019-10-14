package com.acn.loadsensing;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.ViewModel;

import com.acn.loadsensing.bleItem.BleItem;
import com.acn.loadsensing.thingy.ThingyService;

import no.nordicsemi.android.thingylib.ThingySdkManager;

public class MainActivityViewModel extends ViewModel implements Observable {

    @Bindable
    private String deviceName = "No Device Connected";
    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }

    public String getDeviceName() {
        return deviceName;
    }

    void connectToDevice(Context context, ThingySdkManager sdkManager, BleItem device) {
        if (sdkManager != null) {
            BluetoothDevice bluetoothDevice = device.getDevice();
            changeDeviceName(bluetoothDevice);
            sdkManager.connectToThingy(context, bluetoothDevice, ThingyService.class);
            sdkManager.setSelectedDevice(bluetoothDevice);
        }
    }

    private void changeDeviceName(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice != null) {
            deviceName = bluetoothDevice.getName();
            notifyPropertyChanged(com.acn.loadsensing.BR.deviceName);
        }
    }

    public void afterInitialDiscoveryCompleted(ThingySdkManager sdkManager, BluetoothDevice device) {
        sdkManager.enableButtonStateNotification(device, true);
        sdkManager.enableMotionNotifications(device, true);
    }

    private void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }

}
