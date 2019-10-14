package com.acn.loadsensing;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.acn.loadsensing.bleItem.BleItem;
import com.acn.loadsensing.thingy.ThingyService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import no.nordicsemi.android.thingylib.ThingySdkManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class MainActivityViewModelTest {

    private MainActivityViewModel viewModel = new MainActivityViewModel();
    private BleItem bleItem;

    @Before
    public void setUp() {
        bleItem = new BleItem();
        bleItem.setName("Test");
    }

    @Test
    public void connectToDevice_connectsToThingy() {
        Context mockContext = mock(Context.class);
        ThingySdkManager mockThingySdkManager = mock(ThingySdkManager.class);

        viewModel.connectToDevice(mockContext, mockThingySdkManager, bleItem);

        verify(mockThingySdkManager).connectToThingy(eq(mockContext), (BluetoothDevice) any(), eq(ThingyService.class));
    }

    @Test
    public void connectToDevice_setsSelectedDevice() {
        Context mockContext = mock(Context.class);
        ThingySdkManager mockThingySdkManager = mock(ThingySdkManager.class);

        viewModel.connectToDevice(mockContext, mockThingySdkManager, bleItem);

        verify(mockThingySdkManager).setSelectedDevice((BluetoothDevice) any());
    }

    @Test
    public void afterInitialDiscoveryCompleted_enablesMotionNotifications() {
        ThingySdkManager mockThingySdkManager = mock(ThingySdkManager.class);

        viewModel.afterInitialDiscoveryCompleted(mockThingySdkManager, null);

        verify(mockThingySdkManager).enableMotionNotifications((BluetoothDevice) any(), eq(true));
    }

    @Test
    public void afterInitialDiscoveryCompleted_enablesButtonStateNotification() {
        ThingySdkManager mockThingySdkManager = mock(ThingySdkManager.class);

        viewModel.afterInitialDiscoveryCompleted(mockThingySdkManager, null);

        verify(mockThingySdkManager).enableButtonStateNotification((BluetoothDevice) any(), eq(true));
    }
}
