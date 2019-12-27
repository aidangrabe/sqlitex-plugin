package com.aidangrabe.sqlitex

import com.aidangrabe.sqlitex.android.Adb
import com.aidangrabe.sqlitex.android.Device
import com.aidangrabe.sqlitex.android.DeviceOption
import com.aidangrabe.sqlitex.android.NoDevice
import com.aidangrabe.sqlitex.swingx.addTextChangedListener

class SqlitexMainWindow(
        viewHolder: MainWindowViewHolder
) {

    init {
        with (viewHolder) {
            // resultsTable.model = tableModel
            queryField.addTextChangedListener { println("Query changed: $it") }

            deviceChangedListener = {
                Adb.currentDevice = Device(it.name, it.type)
                setAvailableProcesses(getAvailableProcesses())
            }

            setAvailableDevices(getAvailableDevices())
        }
    }

    private fun getAvailableDevices(): List<DeviceOption> {
        val devices = Adb.listDevices()

        return if (devices.isEmpty()) listOf(NoDevice) else devices
    }

    private fun getAvailableProcesses(): List<String> {
        val processes = Adb.listPackages().map { it.name }
        return if (processes.isEmpty()) listOf("No devices") else processes
    }

}