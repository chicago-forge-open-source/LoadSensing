package com.acn.loadsensing;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.acn.loadsensing.thingy.BluetoothThingyListener;
import com.acn.loadsensing.thingy.ThingyService;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import no.nordicsemi.android.thingylib.ThingyListenerHelper;
import no.nordicsemi.android.thingylib.ThingySdkManager;

import static com.acn.loadsensing.deviceScan.BleRecyclerAdapter.EXTRA_BLUETOOTH;
import static com.acn.loadsensing.deviceScan.DeviceScanActivity.INITIAL_CONFIGURATION_RESULT;

public class MainActivity extends AppCompatActivity implements ThingySdkManager.ServiceConnectionListener {

    private MainActivityViewModel viewModel;
    private ThingySdkManager thingySdkManager;
    private BluetoothThingyListener thingyListener;
    private ProgressBar loadWeightBar;
    private BleItem connectedDevice;
    private MapManager mapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        loadWeightBar = findViewById(R.id.load_weight_bar);
        loadWeightBar.setProgress(100);

        ArrayList<PickupLocation> pickupLocations = new ArrayList<>();
        ArrayList<PickupLocation> markerLocations = new ArrayList<>();

        PickupLocation rushUniversityHospital = new PickupLocation("Rush Hospital", new LatLng(41.8747095, -87.6706407), 50, createBitMapDescriptor(R.drawable.ic_rush_hospital));
        PickupLocation northwesternHospital = new PickupLocation("Northwestern Hospital", new LatLng(41.8934742, -87.6373256), 30, createBitMapDescriptor(R.drawable.ic_nw_hospital));
        PickupLocation theForgeChi = new PickupLocation("The Forge", new LatLng(41.8960417, -87.6535176), 10, createBitMapDescriptor(R.drawable.ic_home));
        PickupLocation dropOffCenter = new PickupLocation("Drop Off Center", new LatLng(41.8748568, -87.6383141), 0, createBitMapDescriptor(R.drawable.ic_delete));

        pickupLocations.add(rushUniversityHospital);
        pickupLocations.add(northwesternHospital);
        PickupManager pickupManager = new PickupManager(pickupLocations, dropOffCenter);

        markerLocations.add(theForgeChi);
        markerLocations.add(dropOffCenter);
        markerLocations.add(northwesternHospital);
        markerLocations.add(rushUniversityHospital);

        mapManager = new MapManager(this, markerLocations, theForgeChi);
        mapFragment.getMapAsync(mapManager);

        thingySdkManager = ThingySdkManager.getInstance();
        thingyListener = new BluetoothThingyListener(viewModel, thingySdkManager, mapManager, loadWeightBar, pickupManager);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            connectedDevice = data.getParcelableExtra(EXTRA_BLUETOOTH);
            viewModel.connectToDevice(this, thingySdkManager, connectedDevice);
        }
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

    public BitmapDescriptor createBitMapDescriptor(int drawableId) {
        return BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(drawableId));
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
