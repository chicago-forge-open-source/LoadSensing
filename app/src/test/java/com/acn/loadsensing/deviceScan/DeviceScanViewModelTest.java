package com.acn.loadsensing.deviceScan;

import com.acn.loadsensing.helper.PermissionsHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class DeviceScanViewModelTest {

    private PermissionsHelper mockHelper = mock(PermissionsHelper.class);
    private DeviceScanViewModel viewModel = new DeviceScanViewModel();

    @Test
    public void prepareForScanning_doesNotHaveCoarseLocation() {
        boolean result = viewModel.prepareForScanning(mockHelper);

        verify(mockHelper).requestCoarseLocationPermissions();
        assertFalse(result);
    }

    @Test
    public void prepareForScanning_doesNotHaveLocationTurnedOn() {
        when(mockHelper.hasCoarseLocationPermission()).thenReturn(true);
        boolean result = viewModel.prepareForScanning(mockHelper);

        verify(mockHelper).enableLocation();
        assertFalse(result);
    }

    @Test
    public void prepareForScanning_doesNotHaveBluetoothTurnedOn() {
        when(mockHelper.hasCoarseLocationPermission()).thenReturn(true);
        when(mockHelper.isLocationEnabled()).thenReturn(true);

        boolean result = viewModel.prepareForScanning(mockHelper);

        verify(mockHelper).enableBle();
        assertFalse(result);
    }

    @Test
    public void prepareForScanning_hasAllPermissions() {
        when(mockHelper.hasCoarseLocationPermission()).thenReturn(true);
        when(mockHelper.isLocationEnabled()).thenReturn(true);
        when(mockHelper.isBleEnabled()).thenReturn(true);

        boolean result = viewModel.prepareForScanning(mockHelper);
        assertTrue(result);
    }


}