package com.acn.loadsensing;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.maps.model.Polyline;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class MapManagerTest {
    private MapManager mapManager;
    private Context context;

    @Before
    public void setup(){
        //context = ApplicationProvider.getApplicationContext();
        //mapManager = new MapManager(context);

    }

    @Test
    public void onFull_OnePolyLineIsDrawn(){
//
//        mapManager.full();
//        ArrayList<Polyline> resultingPolylines = mapManager.getPolylines();
//
//        assertEquals(1, resultingPolylines.size());
    }
}
