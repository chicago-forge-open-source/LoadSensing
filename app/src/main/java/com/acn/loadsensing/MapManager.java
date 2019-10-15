package com.acn.loadsensing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapManager implements OnMapReadyCallback {

    private final List<PickupLocation> locations;
    private final PickupLocation startLocation;
    private final Context context;
    private GoogleMap map;
    private ArrayList<Polyline> polylines = new ArrayList<>();

    MapManager(Context context, List<PickupLocation> locations, PickupLocation startLocation) {
        this.context = context;
        this.locations = locations;
        this.startLocation = startLocation;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        createMapMarkers();
        setMarkerClickListeners();

        map.moveCamera(CameraUpdateFactory.newLatLng(startLocation.getLocation()));
        map.moveCamera(CameraUpdateFactory.zoomTo(13));
    }

    private void setMarkerClickListeners() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String googleNavUri = "google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude;
                Uri gmmIntentUri = Uri.parse(googleNavUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
                return false;
            }
        });
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
        for (PickupLocation location : locations) {
            map.addMarker(location.getMarkerOptions());
        }
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
