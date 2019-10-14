package com.acn.loadsensing.bleItem;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class BleItem implements Parcelable {

    private BluetoothDevice device;
    private String name;

    public BleItem() {
    }

    public BleItem(ScanResult result) {
        this.device = result.getDevice();
        this.name = result.getScanRecord() != null ? result.getScanRecord().getDeviceName() : null;
    }

    protected BleItem(Parcel in) {
        name = in.readString();
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
    }

    public static final Creator<BleItem> CREATOR = new Creator<BleItem>() {
        @Override
        public BleItem createFromParcel(Parcel in) {
            return new BleItem(in);
        }

        @Override
        public BleItem[] newArray(int size) {
            return new BleItem[size];
        }
    };

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(device, 0);
    }
}
