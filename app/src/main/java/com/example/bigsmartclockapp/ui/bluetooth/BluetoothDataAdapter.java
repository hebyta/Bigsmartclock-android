package com.example.bigsmartclockapp.ui.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigsmartclockapp.R;

import java.util.ArrayList;

public class BluetoothDataAdapter extends RecyclerView.Adapter<BluetoothDataAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> data;

    public BluetoothDataAdapter(ArrayList<BluetoothDevice> data) {
        this.data = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_adapter_holder, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BluetoothDevice btData = data.get(position);
        holder.lbl_bluetoothName.setText(btData.getName());
        holder.lbl_bluetoothAddress.setText(btData.getAddress());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView lbl_bluetoothName;
        TextView lbl_bluetoothAddress;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            lbl_bluetoothName = view.findViewById(R.id.lbl_bluetoothName);
            lbl_bluetoothAddress = view.findViewById(R.id.lbl_bluetoothAddress);
        }
    }


}
