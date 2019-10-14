package com.acn.loadsensing.thingy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.acn.loadsensing.MainActivity;
import com.acn.loadsensing.R;

import no.nordicsemi.android.thingylib.BaseThingyService;
import no.nordicsemi.android.thingylib.ThingyConnection;

import static no.nordicsemi.android.dfu.DfuBaseService.NOTIFICATION_ID;

public class ThingyService extends BaseThingyService {

    private static final String PRIMARY_CHANNEL = "Thingy:52 Connectivity Status";
    private static final String PRIMARY_CHANNEL_ID = "com.acn.loadsensing.connectivity.status";
    private NotificationChannel mNotificationChannel;

    public class ThingyBinder extends BaseThingyBinder {
        @Override
        public ThingyConnection getThingyConnection(BluetoothDevice device) {
            return mThingyConnections.get(device);
        }
    }

    @Override
    public void onDeviceConnected(final BluetoothDevice device, final int connectionState) {
    }

    @Override
    public void onDeviceDisconnected(final BluetoothDevice device, final int connectionState) {
        super.onDeviceDisconnected(device, connectionState);
        cancelNotification(device);
    }

    @Nullable
    @Override
    public ThingyBinder onBind(final Intent intent) {
        return new ThingyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationPrerequisites();
        startForeground(NOTIFICATION_ID, createForegroundNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationPrerequisites() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationChannel == null) {
            mNotificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, PRIMARY_CHANNEL, NotificationManager.IMPORTANCE_LOW);
        }
        mNotificationManager.createNotificationChannel(mNotificationChannel);
    }

    private Notification createForegroundNotification() {
        final NotificationCompat.Builder builder = getBackgroundNotificationBuilder();
        builder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        builder.setContentTitle(("Bluetooth car is running."));

        return builder.build();
    }

    private NotificationCompat.Builder getBackgroundNotificationBuilder() {
        final Intent parentIntent = new Intent(this, MainActivity.class);
        parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_car_white);
        builder.setChannelId(PRIMARY_CHANNEL_ID);
        return builder;
    }

    private void cancelNotification(final BluetoothDevice device) {
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(device.getAddress(), NOTIFICATION_ID);
    }
}

