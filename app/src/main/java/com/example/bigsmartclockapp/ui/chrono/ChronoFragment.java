package com.example.bigsmartclockapp.ui.chrono;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.bigsmartclockapp.R;
import com.example.bigsmartclockapp.core.bluetooth.BluetoothManager;
import com.example.bigsmartclockapp.core.clock.BaseClock;
import com.example.bigsmartclockapp.core.clock.Chrono;
import com.example.bigsmartclockapp.core.clock.ClockManager;
import com.example.bigsmartclockapp.core.clock.ClockOutput;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Calendar;

public class ChronoFragment extends Fragment implements BluetoothManager.BluetoothCallback{

    private ChronoViewModel chronoViewModel;
    private  TextView lbl_chronoValue;
    private FloatingActionButton startStopButton;
    private boolean isRunning = false;
    private Button activeMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chronoViewModel =
                ViewModelProviders.of(this).get(ChronoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chrono, container, false);
        FloatingActionButton reset = root.findViewById(R.id.btn_resetChrono);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BluetoothManager.getInstance().sendData("resetChrono=1|");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findIds();
        try {
            startListeningBluetooth();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean findIds(){
        try{
            lbl_chronoValue = getActivity().findViewById(R.id.lbl_chronoValue);
            startStopButton = getActivity().findViewById(R.id.btn_startStopChrono);
            startStopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        BluetoothManager.getInstance().sendData("startStopChrono=" + ((isRunning) ? "0" : "1") + "|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            activeMode = getActivity().findViewById(R.id.btn_activeChrono);
            activeMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        BluetoothManager.getInstance().sendData("changeMode=" + ClockManager.CHRONO_MODE +  "|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return lbl_chronoValue != null && startStopButton != null && activeMode != null;
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
                    try {
                        BluetoothManager.getInstance().sendStatus();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Chrono clock = (Chrono) output.clocks.get(ClockManager.CHRONO_CODE);
                    lbl_chronoValue.setText(ClockOutput.formatTime(clock.getHours(), clock.getMinutes(), clock.getSeconds()));
                    isRunning = clock.isRunning();
                    if(clock.isRunning())
                        startStopButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                    else
                        startStopButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }
            }
        });
    }
}
