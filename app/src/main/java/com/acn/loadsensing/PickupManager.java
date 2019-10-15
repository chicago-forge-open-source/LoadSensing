package com.acn.loadsensing;

import java.util.ArrayList;
import java.util.List;

public class PickupManager {

    private List<PickupLocation> pickupLocations;
    private static final int TRUCK_MAX_LOAD = 100;
    private PickupLocation dumpLocation;

    public PickupManager(List<PickupLocation> pickupLocations, PickupLocation dumpLocation) {
            this.pickupLocations = pickupLocations;
            this.dumpLocation = dumpLocation;
    }

    public List<PickupLocation> getValidLocations(int i) {
        List<PickupLocation> validLocations = new ArrayList<>();

        for(PickupLocation location: pickupLocations) {
            if(location.weighsLessThanOrEqualTo (TRUCK_MAX_LOAD - i)) {
                validLocations.add(location);
            }
        }

        if(validLocations.isEmpty()){
            validLocations.add(dumpLocation);
        }

        return validLocations;
    }
}
