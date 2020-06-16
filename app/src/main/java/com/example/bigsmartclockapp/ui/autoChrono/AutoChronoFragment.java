package com.example.bigsmartclockapp.ui.autoChrono;

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
import com.example.bigsmartclockapp.core.clock.AutoChrono;
import com.example.bigsmartclockapp.core.clock.ClockManager;
import com.example.bigsmartclockapp.core.clock.ClockOutput;
import com.example.bigsmartclockapp.core.clock.CountDown;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

public class AutoChronoFragment extends Fragment implements BluetoothManager.BluetoothCallback {

    private AutoChronoViewModel mViewModel;
    private NumberPicker pickerHours;
    private android.widget.NumberPicker pickerMinutes;
    private NumberPicker pickerSeconds;
    private NumberPicker loopsPicker;
    private TextView lbl_autoChronoValue;
    private TextView lbl_loopsCount;
    private TextView lbl_loopsTitle;
    private FloatingActionButton startStopButton;
    private boolean isRunning;
    private int hoursLimit = -1;
    private int minutesLimit = -1;
    private int secondsLimit = -1;
    private int maxLoops = -1;
    private Button activeMode;

    public static AutoChronoFragment newInstance() {
        return new AutoChronoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auto_chrono, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton reset = getActivity().findViewById(R.id.btn_resetAutoChrono);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BluetoothManager.getInstance().sendData("resetAutoChrono="+pickerHours.getValue()+":"+pickerMinutes.getValue()+":"+pickerSeconds.getValue()+":"+loopsPicker.getValue()+"|");
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
            pickerHours = getActivity().findViewById(R.id.autoChronoHoursPicker);
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
            pickerMinutes = getActivity().findViewById(R.id.autoChronoMinutesPicker);
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
            pickerSeconds = getActivity().findViewById(R.id.autoChronoSecondsPicker);
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
            loopsPicker = getActivity().findViewById(R.id.autoChronoMaxLoopsPicker);
            loopsPicker.setMinValue(1);
            loopsPicker.setMaxValue(99);
            loopsPicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if(value < 10)
                        return "0" + value;
                    else
                        return "" + value;
                }
            });
            startStopButton = getActivity().findViewById(R.id.btn_startStopAutoChrono);
            startStopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        BluetoothManager.getInstance().sendData("startStopAutoChrono=" + ((isRunning) ? "0" : "1") + "|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            lbl_autoChronoValue = getActivity().findViewById(R.id.lbl_autoChronoValue);
            lbl_loopsTitle = getActivity().findViewById(R.id.autoChronoLoopsTitle);
            lbl_loopsTitle.setText(getString(R.string.active_loop) + " / " + getString(R.string.max_loops));
            lbl_loopsCount = getActivity().findViewById(R.id.lbl_loopsCount);
            activeMode = getActivity().findViewById(R.id.btn_activeAutoChrono);
            activeMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        BluetoothManager.getInstance().sendData("changeMode=" + ClockManager.AUTO_CHRONO_MODE +  "|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return lbl_autoChronoValue != null && startStopButton != null && pickerSeconds != null && pickerMinutes != null && loopsPicker != null && lbl_loopsTitle != null && lbl_loopsCount != null && activeMode != null;
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
                    AutoChrono clock = (AutoChrono) output.clocks.get(ClockManager.AUTO_CHRONO_CODE);
                    lbl_autoChronoValue.setText(ClockOutput.formatTime(clock.getHours(), clock.getMinutes(), clock.getSeconds()));
                    lbl_loopsCount.setText(clock.getCurrentLoop() + " / " + clock.getMaxLoops());
                    isRunning = clock.isRunning();
                    if(hoursLimit != clock.getHoursLimit()){
                        hoursLimit = clock.getHoursLimit();
                        pickerHours.setValue(hoursLimit);
                    }
                    if(minutesLimit != clock.getMinutesLimit()){
                        minutesLimit = clock.getMinutesLimit();
                        pickerMinutes.setValue(minutesLimit);
                    }
                    if(secondsLimit != clock.getSecondsLimit()){
                        secondsLimit = clock.getSecondsLimit();
                        pickerSeconds.setValue(secondsLimit);
                    }
                    if(maxLoops != clock.getMaxLoops()){
                        maxLoops = clock.getMaxLoops();
                        loopsPicker.setValue(maxLoops);
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
