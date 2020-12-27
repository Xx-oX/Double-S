package com.example.double_s;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class DataReceiverActivity extends AppCompatActivity {

    private TextView textview_show;
    private Handler handler;

    //notify
    public final static UUID uartServiceUUIDString = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    //notify
    public final static UUID uartTXCharacteristicUUIDString = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    //send
    public final static UUID uartRXCharacteristicUUIDString = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");

    private static final String TAG = "DataReceiverActivity";
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i = 0; i < gatts.size(); i++) {
                BluetoothGatt gatt = gatts.get(i);
                BluetoothGattService service = gatt.getService(uartServiceUUIDString);
                Log.d(TAG, service.toString());
                BluetoothGattCharacteristic txCharacteristic= service.getCharacteristic(uartTXCharacteristicUUIDString);
                // 開始讀資料
//                gatt.setCharacteristicNotification(txCharacteristic, true);
//
//                BluetoothGattDescriptor descriptor = txCharacteristic.getDescriptor(
//                        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);
                gatt.readCharacteristic(txCharacteristic);
                // 通知開始收資料
                BluetoothGattCharacteristic rxCharacteristic= service.getCharacteristic(uartRXCharacteristicUUIDString);
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) +1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR);
                int min = cal.get(Calendar.MINUTE);
                int sec = cal.get(Calendar.SECOND);
                int ms = cal.get(Calendar.MILLISECOND);
                int msMinor = ms % 100;
                int msMajor = ms / 100;
                year -= 2000;

                //Log.d("test_Date", year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec );
                byte [] data= { (byte)0x55, (byte)0x01, (byte)0x08, (byte)year, (byte)month, (byte)day, (byte)hour, (byte)min, (byte)sec, (byte)msMajor, (byte)msMinor, (byte)0xaa };
                rxCharacteristic.setValue(data);
                gatt.writeCharacteristic(rxCharacteristic);

                gatt.setCharacteristicNotification(txCharacteristic, true);

                BluetoothGattDescriptor descriptor = txCharacteristic.getDescriptor(
                        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }
    };
    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange");
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "connect");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "disconnect");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered");
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
//            } else {
//                Log.w(TAG, "onServicesDiscovered received: " + status);
//            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.d(TAG, "onCharacteristicRead");
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //Log.d(TAG, "onCharacteristicChanged");
            byte[] values = characteristic.getValue();
            short x1 = ByteBuffer.wrap(new byte[] {values[5], values[4]}).getShort();
            short y1 = ByteBuffer.wrap(new byte[] {values[25], values[24]}).getShort();
            short z1 = ByteBuffer.wrap(new byte[] {values[45], values[44]}).getShort();
            Log.d(TAG, "x: " + x1 +", y: " + y1 + ", z: " + z1);
            String str_result = "time: " + ByteBuffer.wrap(new byte[] {values[3], values[2], values[1], values[0]}).getInt()
                    + "\nx1: " + ByteBuffer.wrap(new byte[] {values[5], values[4]}).getShort()
                    + ", y1: " + ByteBuffer.wrap(new byte[] {values[25], values[24]}).getShort()
                    + ", z1: " + ByteBuffer.wrap(new byte[] {values[45], values[44]}).getShort()
                    + "\nx2: " + ByteBuffer.wrap(new byte[] {values[7], values[6]}).getShort()
                    + ", y2: " + ByteBuffer.wrap(new byte[] {values[27], values[26]}).getShort()
                    + ", z2: " + ByteBuffer.wrap(new byte[] {values[47], values[46]}).getShort()
                    + "\nx3: " + ByteBuffer.wrap(new byte[] {values[9], values[8]}).getShort()
                    + ", y3: " + ByteBuffer.wrap(new byte[] {values[29], values[28]}).getShort()
                    + ", z3: " + ByteBuffer.wrap(new byte[] {values[49], values[48]}).getShort()
                    + "\nx4: " + ByteBuffer.wrap(new byte[] {values[11], values[10]}).getShort()
                    + ", y4: " + ByteBuffer.wrap(new byte[] {values[31], values[30]}).getShort()
                    + ", z4: " + ByteBuffer.wrap(new byte[] {values[51], values[50]}).getShort()
                    + "\nx5: " + ByteBuffer.wrap(new byte[] {values[13], values[12]}).getShort()
                    + ", y5: " + ByteBuffer.wrap(new byte[] {values[33], values[32]}).getShort()
                    + ", z5: " + ByteBuffer.wrap(new byte[] {values[53], values[52]}).getShort()
                    + "\nx6: " + ByteBuffer.wrap(new byte[] {values[15], values[14]}).getShort()
                    + ", y6: " + ByteBuffer.wrap(new byte[] {values[35], values[34]}).getShort()
                    + ", z6: " + ByteBuffer.wrap(new byte[] {values[55], values[54]}).getShort()
                    + "\nx7: " + ByteBuffer.wrap(new byte[] {values[17], values[16]}).getShort()
                    + ", y7: " + ByteBuffer.wrap(new byte[] {values[37], values[36]}).getShort()
                    + ", z7: " + ByteBuffer.wrap(new byte[] {values[57], values[56]}).getShort()
                    + "\nx8: " + ByteBuffer.wrap(new byte[] {values[19], values[18]}).getShort()
                    + ", y8: " + ByteBuffer.wrap(new byte[] {values[39], values[38]}).getShort()
                    + ", z8: " + ByteBuffer.wrap(new byte[] {values[59], values[58]}).getShort()
                    + "\nx9: " + ByteBuffer.wrap(new byte[] {values[21], values[20]}).getShort()
                    + ", y9: " + ByteBuffer.wrap(new byte[] {values[41], values[40]}).getShort()
                    + ", z9: " + ByteBuffer.wrap(new byte[] {values[61], values[60]}).getShort()
                    + "\nx10: " + ByteBuffer.wrap(new byte[] {values[23], values[22]}).getShort()
                    + ", y10: " + ByteBuffer.wrap(new byte[] {values[43], values[42]}).getShort()
                    + ", z10: " + ByteBuffer.wrap(new byte[] {values[63], values[62]}).getShort()
                    + "\navg1: " + Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2) + Math.pow(z1, 2));
            Message msg = Message.obtain();
            msg.obj = str_result;
            msg.setTarget(handler);
            msg.sendToTarget();
//            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };
    private ArrayList<BluetoothGatt> gatts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_receive);
        gatts = new ArrayList<>();
        Button startButton = findViewById(R.id.button_start);
        startButton.setOnClickListener(onClickListener);
        textview_show = findViewById(R.id.txt_show);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                textview_show.setText(msg.obj.toString());
            }
        };
        Intent intent = getIntent();
        ArrayList<BluetoothDevice> deviceToConnect = (ArrayList<BluetoothDevice>)intent.getSerializableExtra("devicesToConnect");
        for (int i = 0; i < deviceToConnect.size(); i++) {
            Log.d("DataReceiverActivity", "connect " + deviceToConnect.get(i).getName());
            BluetoothGatt gatt = deviceToConnect.get(i).connectGatt(this, false, bluetoothGattCallback);
            gatts.add(gatt);
            refreshDeviceCache(gatt);
        }
    }
    private boolean refreshDeviceCache(BluetoothGatt gatt){
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        }
        catch (Exception localException) {
            Log.e("DataReceiverActivity", "An exception occured while refreshing device");
        }
        return false;
    }
}