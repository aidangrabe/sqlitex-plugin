package com.aidangrabe.sqlitex

import com.aidangrabe.sqlitex.android.DeviceOption
import com.aidangrabe.sqlitex.swingx.SimpleListCellRenderer
import javax.swing.*

data class MainWindowViewHolder(
        val queryField: JTextArea,
        val resultsTable: JTable,
        private val devicePicker: JComboBox<DeviceOption>,
        private val processPicker: JComboBox<String>,
        private val databasePicker: JComboBox<String>
) {

    var deviceChangedListener: ((DeviceOption) -> Unit)? = null

    init {
        devicePicker.renderer = SimpleListCellRenderer.with<DeviceOption> { it.name }

        devicePicker.addItemListener {
            println("Item selected: $it")
            deviceChangedListener?.invoke(it.item as DeviceOption)
        }
    }

    fun setAvailableDevices(devices: List<DeviceOption>) {
        devicePicker.setAvailableOptions(devices)
    }

    fun setAvailableProcesses(processes: List<String>) {
        processPicker.setAvailableOptions(processes)
    }

    fun setAvailableDatabases(databases: List<String>) {
        databasePicker.setAvailableOptions(databases)
    }

}

private inline fun <reified T> JComboBox<T>.setAvailableOptions(options: List<T>) {
    model = DefaultComboBoxModel(options.toTypedArray())
}