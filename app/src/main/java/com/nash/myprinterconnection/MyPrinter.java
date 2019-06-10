package com.nash.myprinterconnection;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

public final class MyPrinter implements Serializable {


    //Android Components
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbInterface mInterface;
    private UsbEndpoint mEndpoint;
    private UsbDeviceConnection mConnection;
    private String mUsbDevice = "";
    private PendingIntent mPermissionIntent;
    private Context mContext;

    //Permissions
    private String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    public MyPrinter(Context context, int typeOfConnection) {
        if(typeOfConnection == 1){
            //USB Connection
            USBConnectionInit(context);
        }
        if(typeOfConnection == 2){
            //BT Connection
        }
        if(typeOfConnection == 3){
            //Wifi Connection
        }
    }

    private boolean changeConnection(){
        return false;
    }


    private boolean USBConnectionInit(Context context) {

        mContext = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        boolean isConnected = false;

        //Contains all the UsbDevices list in a Hashmap Datastructure
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        // TODO: Enumerate all the devices and then connect
        //deviceList = mUsbManager.getDeviceList();
        while (deviceIterator.hasNext()) {
            mDevice = deviceIterator.next();
            if (mDevice.getVendorId() == 12232) {
                //Device Found
                Toast.makeText(context.getApplicationContext(), "Device Found!" + mUsbDevice, Toast.LENGTH_SHORT).show();

                isConnected = true;

                mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);

                //Intent-Filter for recognising USB Device
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                IntentFilter filterOnConnection = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
                IntentFilter filterOffConnection = new IntentFilter(ACTION_USB_DEVICE_DETACHED);

                //Register Broadcast Receiver
                context.registerReceiver(mUsbReceiver, filter);
                context.registerReceiver(mUsbReceiver2, filterOnConnection);
                context.registerReceiver(mUsbReceiver1, filterOffConnection);
                mUsbManager.requestPermission(mDevice, mPermissionIntent);
                break;
            }
            else {
                Toast.makeText(context.getApplicationContext(), "Not a Printer", Toast.LENGTH_SHORT).show();
                isConnected = false;
            }
        }
        return isConnected;
    }

    public boolean USBConnectionClose() {
        mConnection.close();
        mContext.unregisterReceiver(mUsbReceiver);
        mContext.unregisterReceiver(mUsbReceiver1);
        mContext.unregisterReceiver(mUsbReceiver2);
        return true;
    }


    private boolean BTConnectionInit(Context context) {
        return false;
    }


    private boolean BTConnectionClose() {
        return false;
    }

    private boolean WifiConnectionInit(Context context) {
        return false;
    }

    private boolean WifiConnectionClose() {
        return false;
    }


    //Broadcast Receivers

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context.getApplicationContext(), "Receiver called", Toast.LENGTH_SHORT).show();
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    Toast.makeText(context.getApplicationContext(), "Receiver called : "+device.getDeviceName() + " "+device.getManufacturerName(), Toast.LENGTH_SHORT).show();

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            mInterface = device.getInterface(0);
                            mEndpoint = mInterface.getEndpoint(1);// 0 IN and  1 OUT to printer.
                            mConnection = mUsbManager.openDevice(device);

                            for (int i = 0; i < mInterface.getEndpointCount(); i++) {
                                Log.i("Printer", "EP: "
                                        + String.format("0x%02X", mInterface.getEndpoint(i)
                                        .getAddress()));

                                if (mInterface.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                                    Log.i("Printer", "Bulk Endpoint");
                                    if (mInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
                                        //epIN = mInterface.getEndpoint(i);
                                        Log.i("Printer", "input stream found");
                                    }else {
                                        mEndpoint = mInterface.getEndpoint(i);
                                        Log.i("Printer", "outstream found");
                                    }
                                } else {
                                    Log.i("Printer", "Not Bulk");
                                }
                            }

                        }
                    } else {
                        Toast.makeText(context, "PERMISSION DENIED FOR THIS DEVICE",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    BroadcastReceiver mUsbReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Toast.makeText(context.getApplicationContext(),"Device Disconnected!",
                    Toast.LENGTH_SHORT).show();
            mConnection.close();
        }
    };

    BroadcastReceiver mUsbReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context.getApplicationContext(),"Device Connected!",
                    Toast.LENGTH_SHORT).show();
        }
    };

}
