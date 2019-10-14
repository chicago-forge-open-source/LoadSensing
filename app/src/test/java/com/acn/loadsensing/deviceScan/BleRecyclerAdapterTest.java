package com.acn.loadsensing.deviceScan;

import android.app.Activity;
import android.content.Intent;

import com.acn.loadsensing.bleItem.BleItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class BleRecyclerAdapterTest {

    private Activity mockActivity = mock(Activity.class);
    private BleRecyclerAdapter adapter = new BleRecyclerAdapter(mockActivity);
    private ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);

    @Test
    public void onItemClickReturnsToMainActivity() {
        BleItem item = new BleItem();
        String expectedName = "Test Bluetooth Device";
        item.setName(expectedName);

        adapter.handleClickReturnsToMain(item);

        verify(mockActivity).setResult(eq(-1), captor.capture());
        Intent result = captor.getValue();

        BleItem bleItem = result.getParcelableExtra("EXTRA_BLUETOOTH");
        assertEquals(expectedName, bleItem.getName());
    }
}