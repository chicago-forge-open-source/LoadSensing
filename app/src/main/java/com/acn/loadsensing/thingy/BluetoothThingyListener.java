package com.acn.loadsensing.thingy;

import android.bluetooth.BluetoothDevice;
import android.widget.ProgressBar;

import com.acn.loadsensing.LineChartManager;
import com.acn.loadsensing.MainActivityViewModel;
import com.acn.loadsensing.helper.AWSHelper;

import no.nordicsemi.android.thingylib.ThingyListener;
import no.nordicsemi.android.thingylib.ThingySdkManager;

public class BluetoothThingyListener implements ThingyListener {

    private MainActivityViewModel viewModel;
    private ThingySdkManager thingySdkManager;
    private LineChartManager chartManager;
    private ProgressBar componentHealthBar;
    private AWSHelper awsHelper;

    public BluetoothThingyListener(MainActivityViewModel viewModel,
                                   ThingySdkManager thingySdkManager,
                                   LineChartManager chartManager,
                                   ProgressBar componentHealthBar,
                                   AWSHelper awsHelper) {
        this.viewModel = viewModel;
        this.thingySdkManager = thingySdkManager;
        this.chartManager = chartManager;
        this.componentHealthBar = componentHealthBar;
        this.awsHelper = awsHelper;
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
        componentHealthBar.setProgress(100);
//        awsHelper.turnLightOff();
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
        chartManager.addAccelerationVectorEntry(x, y, z);
        if (Math.abs(z) >= 2) {
            componentHealthBar.incrementProgressBy(-5);
        }

//        if (componentHealthBar.getProgress() == 0) {
//            awsHelper.turnLightOn();
//        }
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
        chartManager.addGravityVectorEntry(x, y, z);
    }

    @Override
    public void onSpeakerStatusValueChangedEvent(BluetoothDevice bluetoothDevice, int status) {

    }

    @Override
    public void onMicrophoneValueChangedEvent(BluetoothDevice bluetoothDevice, byte[] data) {

    }
}
