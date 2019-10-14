package com.acn.loadsensing.deviceScan;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.acn.loadsensing.R;
import com.acn.loadsensing.bleItem.BleItem;
import com.acn.loadsensing.databinding.ActivityDeviceScanBinding;
import com.acn.loadsensing.helper.PermissionsHelper;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

import static com.acn.loadsensing.helper.PermissionsHelper.ACCESS_COARSE_LOCATION_CODE;

public class DeviceScanActivity extends AppCompatActivity {

    public static final int INITIAL_CONFIGURATION_RESULT = 3;
    private ActivityDeviceScanBinding binding;
    private DeviceScanViewModel viewModel;
    private BleRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_device_scan);

        viewModel = ViewModelProviders.of(this).get(DeviceScanViewModel.class);
        binding.setDeviceScanViewModel(viewModel);

        binding.setLifecycleOwner(this);
        adapter = new BleRecyclerAdapter(this);

        RecyclerView recyclerView = binding.bleRecycler;
        recyclerView.setAdapter(adapter);

        if (viewModel.prepareForScanning(new PermissionsHelper(this))) {
            viewModel.startBLEScanner(scanCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == ACCESS_COARSE_LOCATION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.startBLEScanner(scanCallback);
            } else {
                Log.v("***", "Access Coarse Location Permission denied");
            }

        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
        }

        @Override
        public void onBatchScanResults(final List<ScanResult> results) {
            List<BleItem> bleItems = new ArrayList<>();
            for (ScanResult result : results) {

                bleItems.add(new BleItem(result));
            }

            adapter.setItems(bleItems);
        }

        @Override
        public void onScanFailed(final int errorCode) {
        }
    };

}
