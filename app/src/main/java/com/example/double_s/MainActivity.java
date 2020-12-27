package com.example.double_s;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "ConnectActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private static final long SCAN_PERIOD = 10000;
    private static final int REQUEST_ENABLE_BT = 1;
    private boolean mScanning,permission;
    private Button scan;
    private ListView ble_list;
    //    private ArrayList<BluetoothDevice> ble_address;
//    private ArrayList<String> ble_name;
//    private ArrayList<Boolean> ble_to_connect;
    private BLEDevices bleDevices;
    //private ArrayAdapter scannedAddressAdapter;
    private ListAdapter scannedDeviceAdapter;
    private Handler mHandler;
    private Button connectButton;
    private static final int LOCATION_REQUEST_CODE = 2;



    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan = findViewById(R.id.btnScan);
        ble_list = findViewById(R.id.lstBle);
        connectButton = findViewById(R.id.btnConnect);
        bleDevices = new BLEDevices();
        mHandler = new Handler();
        //scannedAddressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ble_name);
        scannedDeviceAdapter = new ListAdapter(this, bleDevices.getNames());
        //ble_list.setAdapter(scannedAddressAdapter);
        ble_list.setAdapter(scannedDeviceAdapter);
        if(!isLocationPermissionGranted()) //取得位置權限
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initializes list view adapter.
                //scannedAddressAdapter.clear();
                scanLeDevice(true);
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DataReceiverActivity.class);
                intent.putExtra("devicesToConnect", bleDevices.getSelectedBluetoothDevices());
                startActivity(intent);
//                final ArrayList<BluetoothDevice> devices = bleDevices.getAddress();
//                ArrayList<BluetoothGatt> bluetoothGatts = new ArrayList<>();
//                for (int i = 0; i < devices.size(); i++) {
//                    BluetoothGatt gatt = devices.get(i).connectGatt(ConnectActivity.this, false, new BluetoothGattCallback() {
//                        @Override
//                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {}
//                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {}
//                    });
//                }
//                ServiceConnection serviceConnection = new ServiceConnection() {
//                    @Override
//                    public void onServiceConnected(ComponentName componentName, IBinder service) {
//                        bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
//                    }
//
//                    @Override
//                    public void onServiceDisconnected(ComponentName componentName) {
//
//                    }
//                }
//                final BluetoothDevice device = ble_address.get(position);
//                Log.d("test", device+" ");
//                if (device == null) return;
//                intent = new Intent(ConnectActivity.this,  DeviceControlActivity.class);
//                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//
//                SharedPreferences pref = getSharedPreferences("iSleepBetter", MODE_PRIVATE);
//                pref.edit()
//                        .putString("device_name", device.getName())
//                        .commit();
//                pref.edit()
//                        .putString("device_address", device.getAddress())
//                        .commit();
//
//                if (mScanning) {
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    mScanning = false;
//                }
//
//                (new Handler()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(intent);
//                    }
//                }, 1000);
            }
        });
        ble_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bleDevices.selectDevice(position);
                CheckedTextView chkItem = (CheckedTextView) view.findViewById(R.id.check);
                chkItem.setChecked(bleDevices.isDeviceSelected(position));
                bleDevices.getSelectedBluetoothDevices();
//                final BluetoothDevice device = ble_address.get(position);
//                Log.d("test", device+" ");
//                if (device == null) return;
//                intent = new Intent(ConnectActivity.this,  DeviceControlActivity.class);
//                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//
//                SharedPreferences pref = getSharedPreferences("iSleepBetter", MODE_PRIVATE);
//                pref.edit()
//                        .putString("device_name", device.getName())
//                        .commit();
//                pref.edit()
//                        .putString("device_address", device.getAddress())
//                        .commit();
//
//                if (mScanning) {
//                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                    mScanning = false;
//                }
//
//                (new Handler()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(intent);
//                    }
//                }, 1000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOCATION_REQUEST_CODE){
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void scanLeDevice(final boolean enable) {
        bleDevices.clearDevices();
        scannedDeviceAdapter = new ListAdapter(this, bleDevices.getNames());
        //ble_list.setAdapter(scannedAddressAdapter);
        ble_list.setAdapter(scannedDeviceAdapter);
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return view;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(device.getName()!=null){
                                ArrayList bleAddress = bleDevices.getAddress();
                                if(!bleAddress.contains(device)){
                                    Log.d("t1", device+"   " + device.getAddress()+"   " + device.getName());
                                    bleDevices.addDevice(device.getName(), device);
                                    scannedDeviceAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            };

    //檢查位置權限
    private boolean isLocationPermissionGranted()
    {
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
