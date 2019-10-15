package com.acn.loadsensing;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PickupLocation {
    private String name;
    private LatLng location;
    private int loadWeight;
    private MarkerOptions markerOptions;

    public PickupLocation(String name, LatLng location, int loadWeight, BitmapDescriptor bitMap) {
        this.name = name;
        this.location = location;
        this.loadWeight = loadWeight;
        this.markerOptions = new MarkerOptions().position(location).title(name).icon(bitMap);
    }

    public String getName() {
        return name;
    }

    LatLng getLocation() {
        return location;
    }

    MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    boolean weighsLessThanOrEqualTo(int weight) {
        return this.loadWeight <= weight;
    }
}
