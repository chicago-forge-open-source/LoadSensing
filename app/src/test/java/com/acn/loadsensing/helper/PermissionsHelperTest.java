package com.acn.loadsensing.helper;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class PermissionsHelperTest {

    private Activity mockActivity = mock(Activity.class);
    private PermissionsHelper helper = new PermissionsHelper(mockActivity);
    private ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);

    @Test
    public void enableBleSendsIntent() {
        helper.enableBle();

        verify(mockActivity).startActivityForResult(captor.capture(), eq(2));
        Intent result = captor.getValue();

        assertEquals(BluetoothAdapter.ACTION_REQUEST_ENABLE, result.getAction());
    }
}