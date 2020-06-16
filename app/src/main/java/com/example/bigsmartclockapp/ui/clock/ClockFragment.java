package com.example.bigsmartclockapp.ui.clock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bigsmartclockapp.R;
import com.example.bigsmartclockapp.core.bluetooth.BluetoothManager;
import com.example.bigsmartclockapp.core.clock.BaseClock;
import com.example.bigsmartclockapp.core.clock.ClockManager;
import com.example.bigsmartclockapp.core.clock.ClockOutput;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Calendar;

public class ClockFragment extends Fragment implements BluetoothManager.BluetoothCallback {

    private ClockViewModel clockViewModel;
    private TextView lbl_clockValue;
    private Button activeMode;
    private Switch is24Hours;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clockViewModel =
                ViewModelProviders.of(this).get(ClockViewModel.class);
        View root = inflater.inflate(R.layout.fragment_clock, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = getActivity().findViewById(R.id.btn_sync);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                String data = "" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
                try {
                    BluetoothManager.getInstance().sendData("sync=" + data +  "|");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        findIds();
        try {
            startListeningBluetooth();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean findIds(){
        try{
            lbl_clockValue = getActivity().findViewById(R.id.lbl_clockValue);
            activeMode = getActivity().findViewById(R.id.btn_activeClock);
            is24Hours = getActivity().findViewById(R.id.is24Hours);
            is24Hours.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        try {
                            if(is24Hours.isChecked())
                                BluetoothManager.getInstance().sendData("format=1|");
                            else
                                BluetoothManager.getInstance().sendData("format=0|");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            });
            activeMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        BluetoothManager.getInstance().sendData("changeMode=" + ClockManager.CLOCK_MODE +  "|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return lbl_clockValue != null && activeMode != null;
        } catch (Exception e){

        }
        return false;
    }

    private void startListeningBluetooth() throws IOException {
        if (!BluetoothManager.getInstance().isAvailable())
            return;
        if (!BluetoothManager.getInstance().isEnabled()) {
            return;
        }
        if (BluetoothManager.getInstance().getConnectedDevice() == null)
            return;
        BluetoothManager.getInstance().setCallback(this);
        if (!BluetoothManager.getInstance().isListening())
            BluetoothManager.getInstance().startListening();
        else
            BluetoothManager.getInstance().sendStatus();
    }

    @Override
    public void onReceive(final String data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean idsFound = findIds();
                ClockOutput output = ClockManager.processOutput(data);
                if(output != null && idsFound){
                    BaseClock clock = output.clocks.get(ClockManager.CLOCK_CODE);
                    is24Hours.setChecked(clock.isIs24HoursClock());
                    int hours = clock.getHours();
                    String letter = "";
                    if (!is24Hours.isChecked()) {
                        if (hours == 0)
                            hours = 12;
                        else if (hours > 12)
                            hours = hours - 12;
                        if(clock.getHours() >= 12)
                            letter = " PM";
                        else
                            letter = " AM";
                    }
                    lbl_clockValue.setText(ClockOutput.formatTime(hours, clock.getMinutes(), clock.getSeconds()) + letter);
                }
            }
        });
    }
}
