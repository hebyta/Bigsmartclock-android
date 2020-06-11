package com.example.bigsmartclockapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.bigsmartclockapp.core.bluetooth.BluetoothManager;
import com.example.bigsmartclockapp.core.config.AppConfig;
import com.example.bigsmartclockapp.ui.bluetooth.BluetoothDataAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_clock,
                R.id.nav_chrono,
                R.id.nav_countdown,
                R.id.nav_auto_chrono,
                R.id.nav_auto_countdown,
                R.id.nav_settings,
                R.id.nav_help
        ).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        /*if(BluetoothManager.getInstance().getConnectedDevice() == null){
            String lastDevice = AppConfig.getLastConnectedDevice(this);
            if(lastDevice != null)
                connectToBluetooth(lastDevice);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            BluetoothManager.getInstance().disconect();
        } catch (IOException e) {
        }
    }

    public void connectToBluetooth(final String address){
        if(!BluetoothManager.getInstance().isAvailable())
            return;
        if (!BluetoothManager.getInstance().isEnabled()) {
           return;
        }
        if(BluetoothManager.getInstance().getConnectedDevice() != null)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    boolean connected = BluetoothManager.getInstance().connectByAddress(address);
                    if(connected){
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*try {
                                    if(!BluetoothManager.getInstance().isListening())
                                        BluetoothManager.getInstance().startListening();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/
                                AppConfig.setLastConnectedDevice(MainActivity.this, address);
                            }
                        });
                    } else {

                    }
                } catch (Exception e){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                }
            }
        }).start();
    }
}
