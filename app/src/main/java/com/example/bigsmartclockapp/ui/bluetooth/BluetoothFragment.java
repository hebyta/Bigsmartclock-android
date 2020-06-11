package com.example.bigsmartclockapp.ui.bluetooth;

import androidx.lifecycle.ViewModelProviders;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bigsmartclockapp.R;
import com.example.bigsmartclockapp.core.bluetooth.BluetoothManager;
import com.example.bigsmartclockapp.core.config.AppConfig;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BluetoothFragment extends Fragment {

    private static final int BLUETOOTH_REQUEST_CODE = 1;
    private static final int BLUETOOTH_SEARCH_CODE = 2;

    private View fragmentView;
    private BluetoothManager btManager = BluetoothManager.getInstance();
    private BluetoothViewModel mViewModel;
    private ArrayList<BluetoothDevice> savedBluetooths = new ArrayList<BluetoothDevice>();
    private RecyclerView lst_savedDevices;
    private TextView lbl_connectedStatus;
    private ProgressBar spn_connecting;

    public static BluetoothFragment newInstance() {
        return new BluetoothFragment();
    }

    final GestureDetector mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
        @Override public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        lst_savedDevices = fragmentView.findViewById(R.id.lst_savedDevices);
        lbl_connectedStatus = fragmentView.findViewById(R.id.lbl_connected_status);
        spn_connecting = fragmentView.findViewById(R.id.spn_connecting);
        lst_savedDevices.setLayoutManager(new LinearLayoutManager(fragmentView.getContext()));
        lst_savedDevices.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent motionEvent) {
                int position = 0;
                try {
                    View child = lst_savedDevices.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                        spn_connecting.setVisibility(View.VISIBLE);
                        position = lst_savedDevices.getChildAdapterPosition(child);
                        connectToBluetooth(position);
                        return true;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        initBluetooth(true);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BluetoothViewModel.class);
        FloatingActionButton fab = getActivity().findViewById(R.id.btn_sync);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivityForResult(intentOpenBluetoothSettings, BLUETOOTH_SEARCH_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_REQUEST_CODE) {
            if (resultCode != 0)
                initBluetooth(false);
        } else if(requestCode == BLUETOOTH_SEARCH_CODE){
            initBluetooth(false);
        }
    }

    public void initBluetooth(boolean checkEnabled) {
        if(!btManager.isAvailable())
            return;
        if (!btManager.isEnabled() && checkEnabled) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_REQUEST_CODE);
        }
        savedBluetooths = btManager.getPairedDevices();
        BluetoothDataAdapter adapter = new BluetoothDataAdapter(savedBluetooths);
        lst_savedDevices.setAdapter(adapter);
        BluetoothDevice btConnected = BluetoothManager.getInstance().getConnectedDevice();
        if(btConnected != null){
            lbl_connectedStatus.setText(getString(R.string.bluetooth_connected_to) + " " + btConnected.getName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void connectToBluetooth(final int position){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    boolean connected = BluetoothManager.getInstance().connectToDevice(savedBluetooths.get(position));
                    if(connected){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spn_connecting.setVisibility(View.INVISIBLE);
                                Toast.makeText(getActivity(), getString(R.string.bluetooth_connected_to) + " " + savedBluetooths.get(position).getName(), Toast.LENGTH_SHORT).show();;
                                lbl_connectedStatus.setText(getString(R.string.bluetooth_connected_to) + " " + savedBluetooths.get(position).getName());
                                AppConfig.setLastConnectedDevice(getActivity(), savedBluetooths.get(position).getAddress());
                            }
                        });
                    } else {
                        spn_connecting.setVisibility(View.INVISIBLE);
                        Toast.makeText(getActivity(), getString(R.string.bluetooth_cant_connect) + " " + savedBluetooths.get(position).getName(), Toast.LENGTH_SHORT).show();
                        lbl_connectedStatus.setText(getString(R.string.bluetooth_not_connected));
                    }
                } catch (Exception e){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spn_connecting.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), getString(R.string.bluetooth_cant_connect) + " " + savedBluetooths.get(position).getName(), Toast.LENGTH_SHORT).show();;
                            lbl_connectedStatus.setText(getString(R.string.bluetooth_not_connected));
                        }
                    });

                }
            }
        }).start();
    }
}
