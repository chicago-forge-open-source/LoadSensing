package com.acn.loadsensing.thingy;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ProgressBar;

import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.test.core.app.ApplicationProvider;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BluetoothThingyListenerTest {

    private ProgressBar mockLoadWeightBar = mock(ProgressBar.class);
    private MapManager mockMapManger = mock(MapManager.class);
    private PickupManager pickupManager;
    private PickupLocation location20;
    private PickupLocation dumpLocation;

    private List<PickupLocation> pickupLocations;
    private BluetoothThingyListener listener;

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

        listener = new BluetoothThingyListener(null, null, mockMapManger, mockLoadWeightBar, pickupManager);
    }

    @Test
    public void constructor_setsProgressBarToNegative1() {
        verify(mockLoadWeightBar).setProgress(-1);
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
        List<PickupLocation> expectedLocationsToGoTo = new ArrayList<>();
        expectedLocationsToGoTo.add(dumpLocation);

        listener.setMaximumValue(1f);
        listener.onGravityVectorChangedEvent(null, 1f, 0, 0);

        verify(mockMapManger).makeDirections(expectedLocationsToGoTo);
    }

    @Test
    public void lessThanFull_goToValidLocations() {
        List<PickupLocation> expectedLocationsToGoTo = new ArrayList<>();
        expectedLocationsToGoTo.add(location20);

        listener.setMaximumValue(1);
        listener.setMinimumValue(.0f);
        listener.onGravityVectorChangedEvent(null, .7f, 0, 0);

        verify(mockMapManger).makeDirections(expectedLocationsToGoTo);
    }

    @Test
    public void justAsFullAsPreviousReading_doesNotRedrawLines() {
        List<PickupLocation> expectedLocationsToGoTo = new ArrayList<>();
        expectedLocationsToGoTo.add(location20);

        listener.setMaximumValue(1);
        listener.setMinimumValue(.0f);

        when(mockLoadWeightBar.getProgress()).thenReturn(70);
        listener.onGravityVectorChangedEvent(null, .7f, 0, 0);

        verify(mockMapManger, never()).makeDirections(expectedLocationsToGoTo);
    }

    @Test
    public void justAsFullAsPreviousReading_doesNotSetProgressAgain() {
        listener.setMaximumValue(1);
        listener.setMinimumValue(.0f);
        when(mockLoadWeightBar.getProgress()).thenReturn(70);
        listener.onGravityVectorChangedEvent(null, .7f, 0, 0);

        verify(mockLoadWeightBar, never()).setProgress(70);
    }

    @Test
    public void empty_showsAllLocations() {
        listener.setMinimumValue(-.55f);
        when(mockLoadWeightBar.getProgress()).thenReturn(20);
        listener.onGravityVectorChangedEvent(null, -.55f, 0, 0);

        verify(mockMapManger).makeDirections(pickupLocations);
    }

    @Test
    public void scaleValue_valueEqualsMin_returns0() {
        int result = listener.scaleValue(-.3f, -.3f, 1f);

        assertEquals(0, result);
    }

    @Test
    public void scaleValue_valueEqualsMax_returns100() {
        int result = listener.scaleValue(1f, -.3f, 1f);

        assertEquals(100, result);
    }

    @Test
    public void scaleValue_50percent() {
        int result = listener.scaleValue(-.1f, -.3f, .1f);

        assertEquals(50, result);
    }

    @Test
    public void scaleValue_upFrom75Percent_to80Percent() {
        int result = listener.scaleValue(0f, -.3f, .1f);

        assertEquals(80, result);
    }

    @Test
    public void scaleValue_minGreaterThanMax_returns0() {
        int result = listener.scaleValue(0f, 3f, .1f);

        assertEquals(0, result);
    }

    @Test
    public void scaleValue_bothNegative() {
        int result = listener.scaleValue(-.175f, -.2f, -.1f);

        //25 scales to 30
        assertEquals(30, result);
    }

    @Test
    public void onButtonStateChangedEvent_firstPress_taresTop() {
        float expectedMin = 1;
        listener.onGravityVectorChangedEvent(null, expectedMin, 0, 0);
        listener.onButtonStateChangedEvent(null, 1);

        float actualMin = listener.getMinimumValue();

        assertEquals(actualMin, actualMin);
    }

    @Test
    public void onButtonStateChangedEvent_secondPress_taresBottom() {
        float expectedMin = .2f;
        float expectedMax = 0f;
        listener.onGravityVectorChangedEvent(null, expectedMin, 0, 0);
        listener.onButtonStateChangedEvent(null, 1);
        listener.onGravityVectorChangedEvent(null, expectedMax, 0, 0);
        listener.onButtonStateChangedEvent(null, 1);

        float actualMin = listener.getMinimumValue();
        assertEquals(actualMin, actualMin);

        float actualMax = listener.getMaximumValue();
        assertEquals(expectedMax, actualMax);
    }

    @Test
    public void crossFullThreshold_setsProgressBarRed() {
        Drawable mockDrawable = mock(Drawable.class);
        listener.setRedDrawable(mockDrawable);

        listener.setMaximumValue(1);
        listener.setMinimumValue(.0f);
        listener.onGravityVectorChangedEvent(null, .95f, 0, 0);

        verify(mockLoadWeightBar).setProgressDrawable(mockDrawable);
    }

    @Test
    public void crossEmptyThreshold_setsProgressBarGreen() {
        Drawable mockDrawable = mock(Drawable.class);
        listener.setGreenDrawable(mockDrawable);

        listener.setMaximumValue(1);
        listener.setMinimumValue(.0f);
        listener.onGravityVectorChangedEvent(null, .1f, 0, 0);

        verify(mockLoadWeightBar).setProgressDrawable(mockDrawable);
    }
}