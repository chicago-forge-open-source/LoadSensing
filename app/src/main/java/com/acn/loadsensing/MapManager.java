package com.acn.loadsensing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapManager implements OnMapReadyCallback {

    private LatLng dropOffCenter = new LatLng(41.8748568, -87.6383141);
    private LatLng rushUniversityHospital = new LatLng(41.8747095, -87.6706407);
    private LatLng theForgeChi = new LatLng(41.8960417, -87.6535176);
    private LatLng northwesternMemorialHospital = new LatLng(41.8934742, -87.6373256);
    private MarkerOptions dropOff;
    private BitmapDescriptor dropOffIcon;
    private BitmapDescriptor currentLocationIcon;
    private MarkerOptions currentLocation;
    private BitmapDescriptor northwesternHospitalIcon;
    private MarkerOptions northwesternHospital;
    private BitmapDescriptor rushHospitalIcon;
    private MarkerOptions rushHospital;
    private Context context;
    private GoogleMap map;

    public MapManager(Context context) {
        this.context = context;
        createMapMarkers();
    }

    public GoogleMap getMap() {
        return map;
    }

    public void full(){
        map.addPolyline(new PolylineOptions().add(theForgeChi, dropOffCenter));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.addMarker(dropOff);
        map.addMarker(currentLocation);
        map.addMarker(northwesternHospital);
        map.addMarker(rushHospital);

        //map.addPolyline(new PolylineOptions().add(rushUniversityHospital).add(theForgeChi).add(northwesternMemorialHospital).add(dropOffCenter));
        map.moveCamera(CameraUpdateFactory.newLatLng(rushUniversityHospital));
        map.moveCamera(CameraUpdateFactory.zoomTo(13));
    }

    private void createMapMarkers() {
        createBitmaps();
        dropOff = new MarkerOptions()
                .position(dropOffCenter)
                .title(context.getString(R.string.dropOffLocation))
                .icon(dropOffIcon);

        currentLocation = new MarkerOptions()
                .position(theForgeChi)
                .title(context.getString(R.string.currentLocation))
                .icon(currentLocationIcon);

        northwesternHospital = new MarkerOptions()
                .position(northwesternMemorialHospital)
                .title(context.getString(R.string.northwesternHospital))
                .icon(northwesternHospitalIcon);

        rushHospital = new MarkerOptions()
                .position(rushUniversityHospital)
                .title(context.getString(R.string.rushHospital))
                .icon(rushHospitalIcon);
    }

    private void createBitmaps() {
        dropOffIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_delete));
        currentLocationIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_home));
        northwesternHospitalIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_nw_hospital));
        rushHospitalIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_rush_hospital));
    }


    public Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
