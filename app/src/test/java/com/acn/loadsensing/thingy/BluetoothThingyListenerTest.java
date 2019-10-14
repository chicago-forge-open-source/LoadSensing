package com.acn.loadsensing.thingy;

import android.bluetooth.BluetoothDevice;
import android.widget.ProgressBar;

import com.acn.loadsensing.MainActivityViewModel;
import com.acn.loadsensing.MapManager;
import com.acn.loadsensing.helper.AWSHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import no.nordicsemi.android.thingylib.ThingySdkManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BluetoothThingyListenerTest {

    private ProgressBar mockLoadWeightBar = mock(ProgressBar.class);
    private MapManager mockMapManger = mock(MapManager.class);
    private AWSHelper awsHelper = mock(AWSHelper.class);

    @Test
    public void onServiceDiscoveryCompleted_callsViewModel() {
        ThingySdkManager mockThingySdkManager = mock(ThingySdkManager.class);
        MainActivityViewModel mockViewModel = mock(MainActivityViewModel.class);
        BluetoothThingyListener listener = new BluetoothThingyListener(mockViewModel, mockThingySdkManager, null, mockLoadWeightBar, null);

        listener.onServiceDiscoveryCompleted(null);

        verify(mockViewModel).afterInitialDiscoveryCompleted(eq(mockThingySdkManager), (BluetoothDevice) any());
    }

    @Test
    public void OnFull_GoToDump(){
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, awsHelper);
        when(mockLoadWeightBar.getProgress()).thenReturn(90);

        listener.onGravityVectorChangedEvent(null, 1, 0,0);

        verify(mockMapManger).full();
    }

    @Test
    public void lessThanFull_doNothing(){
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, awsHelper);
        when(mockLoadWeightBar.getProgress()).thenReturn(50);

        listener.onGravityVectorChangedEvent(null, 2, 0,0);

        verify(mockMapManger, never()).full();
    }
}