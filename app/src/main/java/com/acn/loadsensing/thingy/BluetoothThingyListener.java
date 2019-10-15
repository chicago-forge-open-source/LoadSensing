package com.acn.loadsensing.thingy;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.ProgressBar;

import com.acn.loadsensing.MainActivityViewModel;
import com.acn.loadsensing.MapManager;
import com.acn.loadsensing.PickupLocation;
import com.acn.loadsensing.PickupManager;
import com.acn.loadsensing.helper.AWSHelper;

import java.util.List;

import no.nordicsemi.android.thingylib.ThingyListener;
import no.nordicsemi.android.thingylib.ThingySdkManager;

public class BluetoothThingyListener implements ThingyListener {

    private static final int BUTTON_UP = 0;
    private static final int BUTTON_DOWN = 1;

    private MainActivityViewModel viewModel;
    private ThingySdkManager thingySdkManager;
    private MapManager mapManager;
    private ProgressBar loadWeightBar;
    private AWSHelper awsHelper;
    private PickupManager pickupManager;

    private float currentGravityX = 0f;
    private float minimumValue = -.55f;
    private float maximumValue = -.05f;

    public BluetoothThingyListener(MainActivityViewModel viewModel,
                                   ThingySdkManager thingySdkManager,
                                   MapManager mapManager,
                                   ProgressBar loadWeightBar,
                                   AWSHelper awsHelper, PickupManager pickupManager) {
        this.viewModel = viewModel;
        this.thingySdkManager = thingySdkManager;
        this.mapManager = mapManager;
        this.loadWeightBar = loadWeightBar;
        this.awsHelper = awsHelper;
        this.pickupManager = pickupManager;
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device, int connectionState) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, int connectionState) {

    }

    @Override
    public void onServiceDiscoveryCompleted(BluetoothDevice device) {
        viewModel.afterInitialDiscoveryCompleted(thingySdkManager, device);
    }

    @Override
    public void onBatteryLevelChanged(BluetoothDevice bluetoothDevice, int batteryLevel) {

    }

    @Override
    public void onTemperatureValueChangedEvent(BluetoothDevice bluetoothDevice, String temperature) {

    }

    @Override
    public void onPressureValueChangedEvent(BluetoothDevice bluetoothDevice, String pressure) {

    }

    @Override
    public void onHumidityValueChangedEvent(BluetoothDevice bluetoothDevice, String humidity) {

    }

    @Override
    public void onAirQualityValueChangedEvent(BluetoothDevice bluetoothDevice, int eco2, int tvoc) {

    }

    @Override
    public void onColorIntensityValueChangedEvent(BluetoothDevice bluetoothDevice, float red, float green, float blue, float alpha) {

    }

    @Override
    public void onButtonStateChangedEvent(BluetoothDevice bluetoothDevice, int buttonState) {
        if(buttonState == BUTTON_DOWN) {
            minimumValue = currentGravityX;
        } else {
            maximumValue = currentGravityX;
        }
    }

    @Override
    public void onTapValueChangedEvent(BluetoothDevice bluetoothDevice, int direction, int count) {

    }

    @Override
    public void onOrientationValueChangedEvent(BluetoothDevice bluetoothDevice, int orientation) {

    }

    @Override
    public void onQuaternionValueChangedEvent(BluetoothDevice bluetoothDevice, float w, float x, float y, float z) {

    }

    @Override
    public void onPedometerValueChangedEvent(BluetoothDevice bluetoothDevice, int steps, long duration) {

    }

    @Override
    public void onAccelerometerValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {
    }

    @Override
    public void onGyroscopeValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

    }

    @Override
    public void onCompassValueChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {

    }

    @Override
    public void onEulerAngleChangedEvent(BluetoothDevice bluetoothDevice, float roll, float pitch, float yaw) {

    }

    @Override
    public void onRotationMatrixValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] matrix) {

    }

    @Override
    public void onHeadingValueChangedEvent(BluetoothDevice bluetoothDevice, float heading) {

    }

    @Override
    public void onGravityVectorChangedEvent(BluetoothDevice bluetoothDevice, float x, float y, float z) {
        currentGravityX = x;
        int percentFull = scaleValue(x, minimumValue, maximumValue);
        loadWeightBar.setProgress(percentFull);

        List<PickupLocation> locationsToGoTo = pickupManager.getValidLocations(percentFull);

        mapManager.makeDirections(locationsToGoTo);
    }

    @Override
    public void onSpeakerStatusValueChangedEvent(BluetoothDevice bluetoothDevice, int status) {

    }

    @Override
    public void onMicrophoneValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] data) {

    }

    int scaleValue(float value, float minimumValue, float maximumValue) {

        if (minimumValue < 0) {
            float absMinimum = Math.abs(minimumValue);
            value += absMinimum;
            maximumValue += absMinimum;
            minimumValue += absMinimum;
        }

        float magnitude = maximumValue - minimumValue;

        return (int) ((value / magnitude) * 100);
    }

    void setMinimumValue(float minimumValue) {
        this.minimumValue = minimumValue;
    }

    float getMinimumValue() {
        return minimumValue;
    }

    public float getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(float maximumValue) {
        this.maximumValue = maximumValue;
    }
}
