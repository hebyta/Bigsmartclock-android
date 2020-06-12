package com.example.bigsmartclockapp.core.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.example.bigsmartclockapp.core.config.AppConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {

    private static final String SERIAL_PORT_SERVICE_ID = "00001101-0000-1000-8000-00805F9B34FB";
    private static BluetoothManager singleton;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice btDevice;
    private BluetoothSocket btSocket;
    private boolean isListening = false;
    private BluetoothCallback callback;

    private BluetoothManager() {
        if (bluetoothAdapter == null)
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothManager getInstance() {
        if (singleton == null)
            singleton = new BluetoothManager();
        return singleton;
    }

    public boolean isAvailable() {
        return bluetoothAdapter != null;
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public BluetoothCallback getCallback() {
        return callback;
    }

    public void setCallback(BluetoothCallback callback) {
        this.callback = callback;
    }

    public ArrayList<BluetoothDevice> getPairedDevices() {
        ArrayList<BluetoothDevice> result = new ArrayList<BluetoothDevice>();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                result.add(device);
            }
        }
        return result;
    }

    public boolean connectByAddress(String address) throws IOException {
        ArrayList<BluetoothDevice> pairedDevices = getPairedDevices();
        BluetoothDevice btDevice = null;
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if(device.getAddress().equals(address)){
                    btDevice = device;
                    break;
                }
            }
        }
        if(btDevice != null)
            return connectToDevice(btDevice);
        return false;
    }

    public boolean connectToDevice(BluetoothDevice bluetoothDevice) throws IOException {
        boolean connected = false;
        btDevice = bluetoothDevice;
        try {
            btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(SERIAL_PORT_SERVICE_ID));
            btSocket.connect();
            connected = btSocket.isConnected();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }

    public boolean disconect() throws IOException {
        stopListening();
        btSocket.close();
        return !btSocket.isConnected();
    }

    public void startListening() throws IOException {
        sendData("command=start|");
        isListening = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String bufferData = "";
                boolean addBytes = true;
                while (!Thread.currentThread().isInterrupted() && isListening && btSocket.isConnected()) {
                    try {
                        int availableBytes = btSocket.getInputStream().available();
                        if(availableBytes > 0){
                            byte[] packagedBytes = new byte[availableBytes];
                            btSocket.getInputStream().read(packagedBytes);
                            for (int i = 0; i < availableBytes; i++){
                                byte b = packagedBytes[i];
                                char character = (char)b;
                                if(character == '&'){
                                    addBytes = true;
                                    bufferData += character;
                                } else if(character == '|'){
                                    addBytes = false;
                                    bufferData += character;
                                } else if(addBytes){
                                    bufferData += character;
                                }
                            }
                        }
                        if(bufferData.startsWith("&") && bufferData.endsWith("|")){
                            if(callback  !=  null)
                                callback.onReceive(bufferData);
                            bufferData = "";
                        }
                    } catch (Exception e) {

                    }
                }
                isListening = false;
            }
        }).start();
    }

    public void sendStatus() throws IOException {
        sendData("command=status|");
    }

    public void stopListening() throws IOException {
        sendData("command=stop|");
        isListening = false;

    }

    public boolean isListening() {
        return isListening;
    }

    public void sendData(String data) throws IOException {
        if(btSocket != null && btSocket.isConnected()){
            btSocket.getOutputStream().write(data.getBytes());
        }
    }

    public interface BluetoothCallback {
        void onReceive(String data);
    }

    public BluetoothDevice getConnectedDevice(){
        if(btSocket != null && btSocket.isConnected())
            return btDevice;
        isListening = false;
        return null;
    }
}
