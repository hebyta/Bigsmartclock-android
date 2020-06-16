package com.example.bigsmartclockapp.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.bigsmartclockapp.R;
import com.example.bigsmartclockapp.core.bluetooth.BluetoothManager;
import com.example.bigsmartclockapp.core.clock.AutoChrono;
import com.example.bigsmartclockapp.core.clock.AutoCountDown;
import com.example.bigsmartclockapp.core.clock.BaseClock;
import com.example.bigsmartclockapp.core.clock.Chrono;
import com.example.bigsmartclockapp.core.clock.ClockManager;
import com.example.bigsmartclockapp.core.clock.ClockOutput;
import com.example.bigsmartclockapp.core.clock.CountDown;

import java.io.IOException;

public class HomeFragment extends Fragment implements BluetoothManager.BluetoothCallback {

    private HomeViewModel homeViewModel;
    private TextView lbl_activeStatus;
    private TextView lbl_currentLoop;
    private TextView lbl_maxLoops;
    private TextView lbl_limit;
    private TextView input_activeTimeValue;
    private TextView input_activeTimeLimit;
    private TextView lbl_redColor;
    private TextView lbl_greenColor;
    private TextView lbl_blueColor;
    private TextView lbl_brightness;
    private LinearLayout colorView;
    private CardView card_ledStatus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        findIds();
        try {
            startListeningBluetooth();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    private boolean findIds(){
        try{
            lbl_activeStatus = getActivity().findViewById(R.id.lbl_activeStatus);
            input_activeTimeValue = getActivity().findViewById(R.id.lbl_clockValue);
            lbl_currentLoop = getActivity().findViewById(R.id.lbl_currentLoop);
            lbl_maxLoops = getActivity().findViewById(R.id.lbl_maxLoops);
            lbl_limit = getActivity().findViewById(R.id.lbl_limit);
            input_activeTimeLimit = getActivity().findViewById(R.id.input_activeTimeLimit);
            lbl_redColor = getActivity().findViewById(R.id.lbl_redColor);
            lbl_blueColor = getActivity().findViewById(R.id.lbl_blueColor);
            lbl_greenColor = getActivity().findViewById(R.id.lbl_greenColor);
            lbl_brightness = getActivity().findViewById(R.id.lbl_brightness);
            colorView = getActivity().findViewById(R.id.colorView);
            card_ledStatus = getActivity().findViewById(R.id.card_ledStatus);
            return lbl_activeStatus != null && input_activeTimeValue != null && lbl_currentLoop != null && lbl_maxLoops != null && input_activeTimeLimit != null &&
                    lbl_limit != null && lbl_redColor != null && lbl_greenColor != null && lbl_blueColor != null && lbl_brightness != null && colorView != null && card_ledStatus != null;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                    BaseClock activeClock = output.clocks.get(output.activeMode);
                    input_activeTimeValue.setVisibility(View.VISIBLE);
                    input_activeTimeValue.setText(ClockOutput.formatTime(activeClock.getHours(), activeClock.getMinutes(), activeClock.getSeconds()));
                    lbl_redColor.setText(getString(R.string.red) + ": " + String.valueOf(output.redColor));
                    lbl_greenColor.setText(getString(R.string.green) + ": " + String.valueOf(output.greenColor));
                    lbl_blueColor.setText(getString(R.string.blue) + ": " + String.valueOf(output.blueColor));
                    lbl_brightness.setText(getString(R.string.brightness) + ": " + String.valueOf(output.brightness));
                    card_ledStatus.setVisibility(View.VISIBLE);
                    colorView.setBackgroundColor(Color.rgb(output.redColor, output.greenColor, output.blueColor));
                    if(activeClock instanceof AutoChrono){
                        lbl_activeStatus.setText(getString(R.string.menu_auto_chrono));
                        lbl_currentLoop.setVisibility(View.VISIBLE);
                        lbl_maxLoops.setVisibility(View.VISIBLE);
                        lbl_currentLoop.setText(getString(R.string.active_loop) + ": " + String.valueOf(((AutoChrono) activeClock).getCurrentLoop()));
                        lbl_maxLoops.setText(getString(R.string.max_loops) + ": " + String.valueOf(((AutoChrono) activeClock).getMaxLoops()));
                        lbl_limit.setVisibility(View.VISIBLE);
                        lbl_limit.setText(getString(R.string.limit));
                        input_activeTimeLimit.setVisibility(View.VISIBLE);
                        input_activeTimeLimit.setText(ClockOutput.formatTime(((AutoChrono) activeClock).getHoursLimit(), ((AutoChrono) activeClock).getMinutesLimit(), ((AutoChrono) activeClock).getSecondsLimit()));
                    } else if(activeClock instanceof AutoCountDown){
                        lbl_activeStatus.setText(getString(R.string.menu_auto_countdown));
                        lbl_currentLoop.setVisibility(View.VISIBLE);
                        lbl_maxLoops.setVisibility(View.VISIBLE);
                        lbl_currentLoop.setText(getString(R.string.active_loop) + ": " + String.valueOf(((AutoCountDown) activeClock).getCurrentLoop()));
                        lbl_maxLoops.setText(getString(R.string.max_loops) + ": " + String.valueOf(((AutoCountDown) activeClock).getMaxLoops()));
                        lbl_limit.setVisibility(View.VISIBLE);
                        lbl_limit.setText(getString(R.string.initial_time));
                        input_activeTimeLimit.setVisibility(View.VISIBLE);
                        input_activeTimeLimit.setText(ClockOutput.formatTime(((AutoCountDown) activeClock).getInitialHours(), ((AutoCountDown) activeClock).getInitialMinutes(), ((AutoCountDown) activeClock).getInitialSeconds()));
                    } else if(activeClock instanceof Chrono){
                        lbl_activeStatus.setText(getString(R.string.menu_chrono));
                        lbl_currentLoop.setVisibility(View.GONE);
                        lbl_maxLoops.setVisibility(View.GONE);
                        lbl_limit.setVisibility(View.GONE);
                        input_activeTimeLimit.setVisibility(View.GONE);
                    } else if(activeClock instanceof CountDown){
                        lbl_activeStatus.setText(getString(R.string.menu_countdown));
                        lbl_currentLoop.setVisibility(View.GONE);
                        lbl_maxLoops.setVisibility(View.GONE);
                        lbl_limit.setVisibility(View.VISIBLE);
                        lbl_limit.setText(getString(R.string.initial_time));
                        input_activeTimeLimit.setVisibility(View.VISIBLE);
                        input_activeTimeLimit.setText(ClockOutput.formatTime(((CountDown) activeClock).getInitialHours(), ((CountDown) activeClock).getInitialMinutes(), ((CountDown) activeClock).getInitialSeconds()));
                    } else if(activeClock != null){
                        lbl_activeStatus.setText(getString(R.string.menu_clock));
                        lbl_currentLoop.setVisibility(View.GONE);
                        lbl_maxLoops.setVisibility(View.GONE);
                        lbl_limit.setVisibility(View.GONE);
                        input_activeTimeLimit.setVisibility(View.GONE);
                        int hours = activeClock.getHours();
                        String letter = "";
                        if (!activeClock.isIs24HoursClock()) {
                            if (hours == 0)
                                hours = 12;
                            else if (hours > 12)
                                hours = hours - 12;
                            if(activeClock.getHours() >= 12)
                                letter = " PM";
                            else
                                letter = " AM";
                        }
                        input_activeTimeValue.setText(ClockOutput.formatTime(activeClock.getHours(), activeClock.getMinutes(), activeClock.getSeconds()) + letter);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
