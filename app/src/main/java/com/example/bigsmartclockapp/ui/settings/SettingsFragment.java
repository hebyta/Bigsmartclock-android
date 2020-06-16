package com.example.bigsmartclockapp.ui.settings;

import androidx.lifecycle.ViewModelProviders;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.bigsmartclockapp.R;
import com.example.bigsmartclockapp.core.bluetooth.BluetoothManager;
import com.example.bigsmartclockapp.core.clock.Chrono;
import com.example.bigsmartclockapp.core.clock.ClockManager;
import com.example.bigsmartclockapp.core.clock.ClockOutput;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class SettingsFragment extends Fragment implements BluetoothManager.BluetoothCallback {

    private SettingsViewModel mViewModel;
    private LinearLayout oldColorPanel;
    private LinearLayout newColorPanel;
    private SeekBar redColorBar;
    private SeekBar greenColorBar;
    private SeekBar blueColorBar;
    private SeekBar brightnessBar;
    private Button changeColorbutton;
    private int oldRedColor;
    private int oldGreenColor;
    private int oldBlueColor;
    private int oldBrightness;
    private int newRedColor = -1;
    private int newGreenColor = -1;
    private int newBlueColor = -1;
    private int newBrightness = -1;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton fab = getActivity().findViewById(R.id.bluetooth_connect);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_settings_to_bluetoothFragment);
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
            oldColorPanel = getActivity().findViewById(R.id.oldColorPanel);
            newColorPanel = getActivity().findViewById(R.id.newColorPanel);
            redColorBar = getActivity().findViewById(R.id.redColorBar);
            redColorBar.setMax(255);
            redColorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    newColorPanel.setBackgroundColor(Color.rgb(redColorBar.getProgress(), greenColorBar.getProgress(), blueColorBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            greenColorBar = getActivity().findViewById(R.id.greennColorBar);
            greenColorBar.setMax(255);
            greenColorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    newColorPanel.setBackgroundColor(Color.rgb(redColorBar.getProgress(), greenColorBar.getProgress(), blueColorBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            blueColorBar = getActivity().findViewById(R.id.blueColorBar);
            blueColorBar.setMax(255);
            blueColorBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    newColorPanel.setBackgroundColor(Color.rgb(redColorBar.getProgress(), greenColorBar.getProgress(), blueColorBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            brightnessBar = getActivity().findViewById(R.id.brightnenssBar);
            brightnessBar.setMax(100);
            brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    newColorPanel.setBackgroundColor(Color.rgb(redColorBar.getProgress(), greenColorBar.getProgress(), blueColorBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            changeColorbutton = getActivity().findViewById(R.id.btn_changeColor);
            changeColorbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        BluetoothManager.getInstance().sendData("changeColor="+redColorBar.getProgress()+":"+greenColorBar.getProgress()+":"+blueColorBar.getProgress()+":"+brightnessBar.getProgress()+"|");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            newColorPanel.setBackgroundColor(Color.rgb(redColorBar.getProgress(), greenColorBar.getProgress(), blueColorBar.getProgress()));
            return oldColorPanel != null && newColorPanel != null && redColorBar!= null && greenColorBar != null && blueColorBar != null && brightnessBar != null && changeColorbutton != null;
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
                    if(oldRedColor != output.redColor){
                        oldRedColor = output.redColor;
                        redColorBar.setProgress(oldRedColor);
                    }
                    if(oldGreenColor != output.greenColor){
                        oldGreenColor = output.greenColor;
                        greenColorBar.setProgress(oldGreenColor);
                    }
                    if(oldBlueColor != output.blueColor){
                        oldBlueColor = output.blueColor;
                        blueColorBar.setProgress(oldBlueColor);
                    }
                    if(oldBrightness != output.brightness) {
                        oldBrightness = output.brightness;
                        brightnessBar.setProgress(oldBrightness);
                    }
                    oldColorPanel.setBackgroundColor(Color.rgb(oldRedColor, oldGreenColor, oldBlueColor));
                }
            }
        });
    }
}
