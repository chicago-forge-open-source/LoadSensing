package com.acn.loadsensing.thingy;

import android.bluetooth.BluetoothDevice;
import android.widget.ProgressBar;

import com.acn.loadsensing.LineChartManager;
import com.acn.loadsensing.MainActivityViewModel;
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

@RunWith(RobolectricTestRunner.class)
public class BluetoothThingyListenerTest {

    private ProgressBar mockComponentHealthBar = mock(ProgressBar.class);
    private LineChartManager mockChartManager = mock(LineChartManager.class);
    private AWSHelper awsHelper = mock(AWSHelper.class);

    @Test
    public void onServiceDiscoveryCompleted_callsViewModel() {
        ThingySdkManager mockThingySdkManager = mock(ThingySdkManager.class);
        MainActivityViewModel mockViewModel = mock(MainActivityViewModel.class);
        BluetoothThingyListener listener = new BluetoothThingyListener(mockViewModel, mockThingySdkManager, null, mockComponentHealthBar, null);

        listener.onServiceDiscoveryCompleted(null);

        verify(mockViewModel).afterInitialDiscoveryCompleted(eq(mockThingySdkManager), (BluetoothDevice) any());
    }

    @Test
    public void onAccelerometerValueChanged_zValueLessThanTwoDoesNotDecreaseHealthBar() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockChartManager, mockComponentHealthBar, awsHelper);

        listener.onAccelerometerValueChangedEvent(null, 1, 2, 1);

        verify(mockComponentHealthBar, never()).incrementProgressBy(-5);
    }
}