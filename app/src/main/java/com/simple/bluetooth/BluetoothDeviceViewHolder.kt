package com.simple.bluetooth

import androidx.recyclerview.widget.RecyclerView
import com.simple.bluetooth.databinding.ItemDeviceBinding

class BluetoothDeviceViewHolder(
    private val binding: ItemDeviceBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindView(item: String) {
        binding.itemDeviceName.text = item
    }
}