package com.aidangrabe.sqlitex

import com.aidangrabe.sqlitex.android.DeviceOption
import com.aidangrabe.sqlitex.swingx.SimpleListCellRenderer
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*

data class MainWindowViewHolder(
        val queryField: JTextArea,
        val resultsTable: JTable,
        private val devicePicker: JComboBox<DeviceOption>,
        private val processPicker: JComboBox<String>,
        private val databasePicker: JComboBox<String>
) {

    @Volatile
    var deviceChangedListener: ((DeviceOption) -> Unit)? = null
    @Volatile
    var processChangedListener: ((String) -> Unit)? = null
    @Volatile
    var databaseChangedListener: ((String) -> Unit)? = null
    @Volatile
    var submitQueryListener: ((String) -> Unit)? = null

    init {
        queryField.addKeyListener(QueryFieldKeyListener())

        devicePicker.renderer = SimpleListCellRenderer.with<DeviceOption> { it.name }

        devicePicker.addItemListener {
            deviceChangedListener?.invoke(it.item as DeviceOption)
        }

        processPicker.addItemListener {
            processChangedListener?.invoke(it.item.toString())
        }

        databasePicker.addItemListener {
            invokeDatabaseChangedListener(it.item.toString())
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

        if (databases.size == 1) {
            invokeDatabaseChangedListener(databases.first())
        }
    }

    private fun invokeDatabaseChangedListener(database: String) {
        val listener = databaseChangedListener ?: return

        if (database.isNotBlank()) {
            listener(database)
        }
    }

    private fun invokeOnSubmitListener() {
        val listener = submitQueryListener ?: return
        val query = queryField.text

        if (query.isNotBlank()) {
            listener(query)
        }
    }

    private inner class QueryFieldKeyListener : KeyListener {

        override fun keyTyped(e: KeyEvent) {
        }

        override fun keyPressed(e: KeyEvent) {
            when (e.keyCode) {
                KeyEvent.VK_ENTER -> handleEnterKey(e)
            }
        }

        override fun keyReleased(e: KeyEvent) {
        }

        private fun handleEnterKey(event: KeyEvent) {
            if (event.isShiftDown) {
                // TODO should insert at current position, not append
                queryField.append("\n")
            } else {
                invokeOnSubmitListener()
            }
        }

    }

}

private inline fun <reified T> JComboBox<T>.setAvailableOptions(options: List<T>) {
    model = DefaultComboBoxModel(options.toTypedArray())
}