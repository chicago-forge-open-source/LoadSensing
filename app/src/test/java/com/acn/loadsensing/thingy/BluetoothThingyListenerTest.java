package com.acn.loadsensing.thingy;

import android.bluetooth.BluetoothDevice;
import android.widget.ProgressBar;

import com.acn.loadsensing.MainActivityViewModel;
import com.acn.loadsensing.MapManager;
import com.acn.loadsensing.PickupLocation;
import com.acn.loadsensing.PickupManager;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.thingylib.ThingySdkManager;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class BluetoothThingyListenerTest {

    private ProgressBar mockLoadWeightBar = mock(ProgressBar.class);
    private MapManager mockMapManger = mock(MapManager.class);
    private PickupManager pickupManager;

    private PickupLocation location20;
    private PickupLocation dumpLocation;

    private List<PickupLocation> pickupLocations;

    @Before
    public void setUp() {
        location20 = new PickupLocation("Location20", new LatLng(20, 20), 20, null);
        PickupLocation location50 = new PickupLocation("Location50", new LatLng(20, 20), 50, null);
        PickupLocation location100 = new PickupLocation("Location100", new LatLng(20, 20), 100, null);
        dumpLocation = new PickupLocation("Dump Location", new LatLng(20, 20), 0, null);

        pickupLocations = new ArrayList<>();
        pickupLocations.add(location20);
        pickupLocations.add(location50);
        pickupLocations.add(location100);

        pickupManager = new PickupManager(pickupLocations, dumpLocation);
    }

    @Test
    public void onServiceDiscoveryCompleted_callsViewModel() {
        ThingySdkManager mockThingySdkManager = mock(ThingySdkManager.class);
        MainActivityViewModel mockViewModel = mock(MainActivityViewModel.class);
        BluetoothThingyListener listener = new BluetoothThingyListener(mockViewModel, mockThingySdkManager, null, mockLoadWeightBar, pickupManager);

        listener.onServiceDiscoveryCompleted(null);

        verify(mockViewModel).afterInitialDiscoveryCompleted(eq(mockThingySdkManager), (BluetoothDevice) any());
    }

    @Test
    public void truckFull_goToDump() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);
        List<PickupLocation> expectedLocationsToGoTo = new ArrayList<>();
        expectedLocationsToGoTo.add(dumpLocation);

        listener.setMaximumValue(1f);
        listener.onGravityVectorChangedEvent(null, 1f, 0, 0);

        verify(mockMapManger).makeDirections(expectedLocationsToGoTo);
    }

    @Test
    public void lessThanFull_goToValidLocations() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);
        List<PickupLocation> expectedLocationsToGoTo = new ArrayList<>();
        expectedLocationsToGoTo.add(location20);

        listener.setMaximumValue(1);
        listener.setMinimumValue(.0f);
        listener.onGravityVectorChangedEvent(null, .7f, 0, 0);

        verify(mockMapManger).makeDirections(expectedLocationsToGoTo);
    }

    @Test
    public void empty_showsAllLocations() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);

        listener.setMinimumValue(-.55f);
        listener.onGravityVectorChangedEvent(null, -.55f, 0, 0);

        verify(mockMapManger).makeDirections(pickupLocations);
    }

    @Test
    public void scaleValue_valueEqualsMin_returns0() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);

        int result = listener.scaleValue(-.3f, -.3f, 1f);

        assertEquals(0, result);
    }

    @Test
    public void scaleValue_valueEqualsMax_returns100() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);

        int result = listener.scaleValue(1f, -.3f, 1f);

        assertEquals(100, result);
    }

    @Test
    public void scaleValue_50percent() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);

        int result = listener.scaleValue(-.1f, -.3f, .1f);

        assertEquals(50, result);
    }

    @Test
    public void scaleValue_75percent() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);

        int result = listener.scaleValue(0f, -.3f, .1f);

        assertEquals(75, result);
    }

    @Test
    public void scaleValue_minGreaterThanMax_returns0() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);

        int result = listener.scaleValue(0f, 3f, .1f);

        assertEquals(0, result);
    }

    @Test
    public void scaleValue_bothNegative() {
        BluetoothThingyListener listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);

        int result = listener.scaleValue(-.175f, -.2f, -.1f);

        assertEquals(25, result);
    }
}