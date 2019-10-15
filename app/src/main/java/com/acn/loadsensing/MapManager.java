package com.acn.loadsensing;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapManager implements OnMapReadyCallback {

    private final List<PickupLocation> locations;
    private final PickupLocation startLocation;
    private GoogleMap map;
    private ArrayList<Polyline> polylines = new ArrayList<>();

    public MapManager(List<PickupLocation> locations, PickupLocation startLocation) {
        this.locations = locations;
        this.startLocation = startLocation;
        createMapMarkers();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        for (PickupLocation location : locations) {
            map.addMarker(location.getMarkerOptions());
        }

        map.moveCamera(CameraUpdateFactory.newLatLng(startLocation.getLocation()));
        map.moveCamera(CameraUpdateFactory.zoomTo(13));
    }


    private Polyline addPolyline(LatLng start, LatLng end) {
        return map.addPolyline(new PolylineOptions().add(start).add(end));
    }

    private void clearPolylines() {
        for (Polyline polyline : polylines) {
            polyline.remove();
        }
    }

    private void createMapMarkers() {

    }


    public void makeDirections(List<PickupLocation> locationsToGoTo) {
        clearPolylines();
        PickupLocation currentStart = startLocation;
        for (PickupLocation location : locationsToGoTo) {
            polylines.add(addPolyline(currentStart.getLocation(), location.getLocation()));
            currentStart = location;
        }
    }
}
