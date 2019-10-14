package com.acn.loadsensing;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.acn.loadsensing.bleItem.BleItem;
import com.acn.loadsensing.databinding.ActivityMainBinding;
import com.acn.loadsensing.deviceScan.DeviceScanActivity;
import com.acn.loadsensing.helper.AWSHelper;
import com.acn.loadsensing.thingy.BluetoothThingyListener;
import com.acn.loadsensing.thingy.ThingyService;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import no.nordicsemi.android.thingylib.ThingyListenerHelper;
import no.nordicsemi.android.thingylib.ThingySdkManager;

import static com.acn.loadsensing.deviceScan.BleRecyclerAdapter.EXTRA_BLUETOOTH;
import static com.acn.loadsensing.deviceScan.DeviceScanActivity.INITIAL_CONFIGURATION_RESULT;

public class MainActivity extends AppCompatActivity implements ThingySdkManager.ServiceConnectionListener, OnMapReadyCallback {

    private MainActivityViewModel viewModel;
    private ThingySdkManager thingySdkManager;
    private BluetoothThingyListener thingyListener;
    private ProgressBar componentHealthBar;
    private BleItem connectedDevice;
    private LineChartManager chartManager;
    private static final String LOG_TAG = "***";
    private static final String CUSTOMER_SPECIFIC_IOT_ENDPOINT = "a2soq6ydozn6i0-ats.iot.us-west-2.amazonaws.com";
    private AWSHelper awsHelper;
    private GoogleMap mMap;

    LatLng dropOffCenter = new LatLng(41.8748568, -87.6383141);
    LatLng rushUniversityHospital = new LatLng(41.8747095, -87.6706407);
    LatLng theForgeChi = new LatLng(41.8960417, -87.6535176);
    LatLng northwesternMemorialHospital = new LatLng(41.8934742, -87.6373256);
    private MarkerOptions dropOff;
    private BitmapDescriptor dropOffIcon;
    private BitmapDescriptor currentLocationIcon;
    private MarkerOptions currentLocation;
    private BitmapDescriptor northwesternHospitalIcon;
    private MarkerOptions northwesternHospital;
    private BitmapDescriptor rushHospitalIcon;
    private MarkerOptions rushHospital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        componentHealthBar = findViewById(R.id.component_health_bar);
        componentHealthBar.setProgress(100);
        //  awsHelper = new AWSHelper(setUpAWS());
        // awsHelper.connectToAWS();
        createMapMarkers();

        thingySdkManager = ThingySdkManager.getInstance();
        thingyListener = new BluetoothThingyListener(viewModel, thingySdkManager, chartManager, componentHealthBar, awsHelper);
        setConnectOnClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        thingySdkManager.bindService(this, ThingyService.class);
        ThingyListenerHelper.registerThingyListener(this, thingyListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        thingySdkManager.unbindService(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // awsHelper.disconnectFromAWS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //   awsHelper.turnLightOff();

        if (resultCode == RESULT_OK && data != null) {
            connectedDevice = data.getParcelableExtra(EXTRA_BLUETOOTH);
            viewModel.connectToDevice(this, thingySdkManager, connectedDevice);
        }
    }

    private AWSIotMqttManager setUpAWS() {
        final CountDownLatch latch = new CountDownLatch(1);
        AWSMobileClient.getInstance().initialize(
                this,
                new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails result) {
                        latch.countDown();
                    }

                    @Override
                    public void onError(Exception e) {
                        latch.countDown();
                        Log.e(LOG_TAG, "onError: ", e);
                    }
                }
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String clientId = UUID.randomUUID().toString();
        return new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_IOT_ENDPOINT);
    }

    private void setConnectOnClickListener() {
        final FloatingActionButton connect = findViewById(R.id.connectFab);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent initialConfiguration = new Intent(MainActivity.this, DeviceScanActivity.class);
                startActivityForResult(initialConfiguration, INITIAL_CONFIGURATION_RESULT);
            }
        });
    }

    @Override
    public void onServiceConnected() {
        if (connectedDevice != null) {
            BluetoothDevice device = connectedDevice.getDevice();
            if (thingySdkManager.hasInitialServiceDiscoverCompleted(device)) {
                viewModel.afterInitialDiscoveryCompleted(thingySdkManager, device);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(dropOff);
        mMap.addMarker(currentLocation);
        mMap.addMarker(northwesternHospital);
        mMap.addMarker(rushHospital);

        mMap.addPolyline(new PolylineOptions().add(rushUniversityHospital).add(theForgeChi).add(northwesternMemorialHospital).add(dropOffCenter));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rushUniversityHospital));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }

    private void createMapMarkers() {
        createBitmaps();
        dropOff = new MarkerOptions()
                .position(dropOffCenter)
                .title(getString(R.string.dropOffLocation))
                .icon(dropOffIcon);

        currentLocation = new MarkerOptions()
                .position(theForgeChi)
                .title(getString(R.string.currentLocation))
                .icon(currentLocationIcon);

        northwesternHospital = new MarkerOptions()
                .position(northwesternMemorialHospital)
                .title(getString(R.string.northwesternHospital))
                .icon(northwesternHospitalIcon);

        rushHospital = new MarkerOptions()
                .position(rushUniversityHospital)
                .title(getString(R.string.rushHospital))
                .icon(rushHospitalIcon);
    }

    private void createBitmaps() {
        dropOffIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_delete));
        currentLocationIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_home));
        northwesternHospitalIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_nw_hospital));
        rushHospitalIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.ic_rush_hospital));
    }


    public Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
