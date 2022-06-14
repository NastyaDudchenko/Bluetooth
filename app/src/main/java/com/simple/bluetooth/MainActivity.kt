package com.simple.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.simple.bluetooth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BluetoothPresenter {

    private lateinit var binding: ActivityMainBinding
    private val bluetoothDevicesList: MutableList<String> = mutableListOf()
    private val bluetoothManager: BluetoothManager by lazy { getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter: BluetoothAdapter? by lazy { bluetoothManager.adapter }
    private val adapter: BluetoothDeviceAdapter by lazy {
        BluetoothDeviceAdapter(
            bluetoothDevicesList
        )
    }
    private var buttonIsPressed: Boolean = false

    @SuppressLint("MissingPermission")
    private var requestBluetooth =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                bluetoothAdapter?.startDiscovery()
            }
        }

    @SuppressLint("MissingPermission")
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                bluetoothAdapter?.startDiscovery()
            }
        }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> searchBluetoothDevices(intent)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardView.setBackgroundResource(R.drawable.card_view_background)
        binding.presenter = this

        initListOfDevices()

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun initListOfDevices() {
        binding.listOfDevices.adapter = adapter
        binding.listOfDevices.layoutManager = LinearLayoutManager(this)
    }

    override fun enableScanPressed() {
        if (bluetoothAdapter?.isEnabled == false) {
            enableBluetooth()
        } else if (buttonIsPressed) {
            binding.searchDevicesDescription.visibility = View.VISIBLE
            binding.progress.visibility = View.VISIBLE
            binding.tapToSearchDescription.visibility = View.INVISIBLE
            checkPermission()
        } else {
            binding.searchDevicesDescription.visibility = View.INVISIBLE
            binding.progress.visibility = View.INVISIBLE
            binding.tapToSearchDescription.visibility = View.VISIBLE
            bluetoothAdapter?.cancelDiscovery()
            bluetoothDevicesList.clear()
        }
        buttonIsPressed = !buttonIsPressed
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    override fun enableBluetooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
            Toast.makeText(this, "Bluetooth is enabled", Toast.LENGTH_LONG).show()
        } else {
            bluetoothAdapter?.disable()
            Toast.makeText(this, "Bluetooth is disabled", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    private fun searchBluetoothDevices(intent: Intent) {
        val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        val deviceName = device?.name
        val deviceHardwareAddress = device?.address

        if (deviceName.isNullOrEmpty()) {
            bluetoothDevicesList.add(deviceHardwareAddress.toString())
        } else {
            bluetoothDevicesList.add(deviceName.toString())
        }
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    override fun paired() {
        bluetoothDevicesList.clear()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address

            if (deviceName.isNullOrEmpty()) {
                bluetoothDevicesList.add(deviceHardwareAddress.toString())
            } else {
                bluetoothDevicesList.add(deviceName.toString())
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }
}