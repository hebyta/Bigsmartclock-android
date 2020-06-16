package com.example.bigsmartclockapp.ui.countdown;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.bigsmartclockapp.R;
import com.example.bigsmartclockapp.core.bluetooth.BluetoothManager;
import com.example.bigsmartclockapp.core.clock.Chrono;
import com.example.bigsmartclockapp.core.clock.ClockManager;
import com.example.bigsmartclockapp.core.clock.ClockOutput;
import com.example.bigsmartclockapp.core.clock.CountDown;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.IOException;

public class CountdownFragment extends Fragment implements BluetoothManager.BluetoothCallback {

    private CountdownViewModel mViewModel;
    private NumberPicker pickerHours;
    private NumberPicker pickerMinutes;
    private NumberPicker pickerSeconds;
    private TextView lbl_countDownValue;
    private FloatingActionButton startStopButton;
    private boolean isRunning;
    private int initialHours = -1;
    private int initialMinutes = -1;
    private int initialSeconds = -1;
    private Button activeMode;

    public static CountdownFragment newInstance() {
        return new CountdownFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_countdown, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton reset = getActivity().findViewById(R.id.btn_resetCowntDown);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BluetoothManager.getInstance().sendData("resetCountDown="+pickerHours.getValue()+":"+pickerMinutes.getValue()+":"+pickerSeconds.getValue()+"|");
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
            pickerHours = getActivity().findViewById(R.id.countDownHoursPicker);
            pickerHours.setMinValue(0);
            pickerHours.setMaxValue(23);
            pickerHours.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if(value < 10)
                        return "0" + value;
                    else
                        return "" + value;
                }
            });
            pickerMinutes = getActivity().findViewById(R.id.countDownMinutesPicker);
            pickerMinutes.setMinValue(0);
            pickerMinutes.setMaxValue(59);
            pickerMinutes.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if(value < 10)
                        return "0" + value;
                    else
                        return "" + value;
                }
            });
            pickerSeconds = getActivity().findViewById(R.id.countDownSecondsPicker);
            pickerSeconds.setMinValue(0);
            pickerSeconds.setMaxValue(59);
            pickerSeconds.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if(value < 10)
                        return "0" + value;
                    else
                        return "" + value;
                }
            });
            startStopButton = getActivity().findViewById(R.id.btn_startStopCountDown);
            startStopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        BluetoothManager.getInstance().sendData("startStopCountDown=" + ((isRunning) ? "0" : "1") + "|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            lbl_countDownValue = getActivity().findViewById(R.id.lbl_countdownValue);
            activeMode = getActivity().findViewById(R.id.btn_activeCountdown);
            activeMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        BluetoothManager.getInstance().sendData("changeMode=" + ClockManager.COUNTDOWN_MODE +  "|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return lbl_countDownValue != null && startStopButton != null && pickerSeconds != null && pickerMinutes != null && pickerHours != null && activeMode != null;
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
                    CountDown clock = (CountDown) output.clocks.get(ClockManager.COUNTDOWN_CODE);
                    lbl_countDownValue.setText(ClockOutput.formatTime(clock.getHours(), clock.getMinutes(), clock.getSeconds()));
                    isRunning = clock.isRunning();
                    if(initialHours != clock.getInitialHours()){
                        initialHours = clock.getInitialHours();
                        pickerHours.setValue(initialHours);
                    }
                    if(initialMinutes != clock.getInitialMinutes()){
                        initialMinutes = clock.getInitialMinutes();
                        pickerMinutes.setValue(initialMinutes);
                    }
                    if(initialSeconds != clock.getInitialSeconds()){
                        initialSeconds = clock.getInitialSeconds();
                        pickerSeconds.setValue(initialSeconds);
                    }
                    if(clock.isRunning())
                        startStopButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                    else
                        startStopButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }
            }
        });
    }
}
