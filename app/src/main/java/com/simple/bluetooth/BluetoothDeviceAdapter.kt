package com.simple.bluetooth

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simple.bluetooth.databinding.ItemDeviceBinding

class BluetoothDeviceAdapter(
    var deviceList: List<String>
) : RecyclerView.Adapter<BluetoothDeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDeviceBinding.inflate(layoutInflater, parent, false)
        return BluetoothDeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        val currentItem = deviceList[position]
        holder.bindView(currentItem)
    }

    override fun getItemCount(): Int = deviceList.size
}