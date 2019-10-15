package com.acn.loadsensing;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;

public class PickupManagerTest {

    private PickupManager pickupManager;
    private PickupLocation location20;
    private PickupLocation location50;
    private PickupLocation location100;
    private PickupLocation dumpLocation;

    private List<PickupLocation> pickupLocations;

    @Before
    public void setUp() {
        location20 = new PickupLocation("Location20", new LatLng(20, 20), 20, null);
        location50 = new PickupLocation("Location50", new LatLng(20, 20), 50, null);
        location100 = new PickupLocation("Location100", new LatLng(20, 20), 100, null);
        dumpLocation = new PickupLocation("Dump Location", new LatLng(20, 20), 0, null);

        pickupLocations = new ArrayList<>();
        pickupLocations.add(location20);
        pickupLocations.add(location50);
        pickupLocations.add(location100);

        pickupManager = new PickupManager(pickupLocations, dumpLocation);
    }

    @Test
    public void getValidLocations_noWeight_returnsAllLocations() {
        List<PickupLocation> result = pickupManager.getValidLocations(0);

        assertEquals(pickupLocations, result);

    }

    @Test
    public void getValidLocations_fullTruck_returnDumpLocation() {
        List<PickupLocation> expected = new ArrayList<>();
        expected.add(dumpLocation);

        List<PickupLocation> actual = pickupManager.getValidLocations(100);

        assertEquals(expected, actual);
    }

    @Test
    public void getValidLocations_50PercentFull_returnsLocationsUnder50() {

        List<PickupLocation> expected = new ArrayList<>();
        expected.add(location50);
        expected.add(location20);

        List<PickupLocation> actual = pickupManager.getValidLocations(50);

        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }
}