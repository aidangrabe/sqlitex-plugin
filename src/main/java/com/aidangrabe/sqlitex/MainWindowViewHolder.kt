package com.aidangrabe.sqlitex

import com.aidangrabe.sqlitex.android.DeviceOption
import com.aidangrabe.sqlitex.data.TableData
import com.aidangrabe.sqlitex.swingx.SimpleListCellRenderer
import com.aidangrabe.sqlitex.swingx.getItems
import com.aidangrabe.sqlitex.swingx.setItems
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener
import javax.swing.table.DefaultTableModel

data class MainWindowViewHolder(
        private val queryField: JTextArea,
        private val resultsTable: JTable,
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
    @Volatile
    var onDevicePickerOpened: (() -> Unit)? = null

    init {
        queryField.addKeyListener(QueryFieldKeyListener())

        devicePicker.renderer = SimpleListCellRenderer.with<DeviceOption> { it.name }

        devicePicker.addItemListener {
            deviceChangedListener?.invoke(it.item as DeviceOption)
        }

        devicePicker.addPopupMenuListener(DevicePickerPopUpListener())

        processPicker.addItemListener {
            processChangedListener?.invoke(it.item.toString())
        }

        databasePicker.addItemListener {
            invokeDatabaseChangedListener(it.item.toString())
        }
    }

    fun setSelectedProcess(process: String) {
        processPicker.model.selectedItem = process
    }

    fun getAvailableProcesses(): List<String> = processPicker.getItems()

    fun setAvailableDevices(devices: List<DeviceOption>) {
        devicePicker.setItems(devices)
    }

    fun setAvailableProcesses(processes: List<String>) {
        processPicker.setItems(processes)
    }

    fun setAvailableDatabases(databases: List<String>) {
        databasePicker.setItems(databases)

        if (databases.size == 1) {
            invokeDatabaseChangedListener(databases.first())
        }
    }

    fun updateTableResults(tableData: TableData) {
        val tableModel = DefaultTableModel()

        tableData.columnNames.forEach { tableModel.addColumn(it) }
        tableData.rows.forEach { tableModel.addRow(it.toTypedArray()) }

        resultsTable.model = tableModel
    }

    private fun invokeDatabaseChangedListener(database: String) {
        val listener = databaseChangedListener ?: return
        val cleanedDatabase = database.trim()

        if (cleanedDatabase.isNotBlank()) {
            listener(cleanedDatabase)
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
                val caretPosition = queryField.caretPosition
                queryField.insert("\n", caretPosition)
            } else {
                invokeOnSubmitListener()
                event.consume()
            }
        }

    }

    private inner class DevicePickerPopUpListener: PopupMenuListener {
        override fun popupMenuWillBecomeInvisible(e: PopupMenuEvent) {
        }

        override fun popupMenuCanceled(e: PopupMenuEvent) {
        }

        override fun popupMenuWillBecomeVisible(e: PopupMenuEvent) {
            onDevicePickerOpened?.invoke()
        }
    }

}