package com.aidangrabe.sqlitex

import com.aidangrabe.sqlitex.android.*
import com.aidangrabe.sqlitex.data.TableParser
import javax.swing.table.DefaultTableModel

class SqlitexMainWindow(
        private val viewHolder: MainWindowViewHolder
) {

    private var process: String = ""
    private var database: String = ""

    init {
        with(viewHolder) {
            submitQueryListener = {
                val sqliteOutput = SqliteContext(process, database).exec("$it;")

                val parser = TableParser()
                val tableData = parser.parse(sqliteOutput)

                val tableModel = DefaultTableModel()

                println("column names: ${tableData.columnNames}")
                println("rows: ${tableData.rows}")

                tableData.columnNames.forEach { tableModel.addColumn(it) }
                tableData.rows.forEach { tableModel.addRow(it.toTypedArray()) }

                resultsTable.model = tableModel
            }

            deviceChangedListener = {
                if (it !is NoDevice) {
                    onDeviceSelected(it.toDevice())
                }
            }

            processChangedListener = {
                process = it
                setAvailableDatabases(getAvailableDatabases(process))
            }

            databaseChangedListener = {
                database = it
            }

            val devices = getAvailableDevices()
            setAvailableDevices(devices)

            if (devices.size == 1) {
                onDeviceSelected(devices.first().toDevice())
            }
        }
    }

    private fun onDeviceSelected(device: Device) {
        Adb.currentDevice = device
        viewHolder.setAvailableProcesses(getAvailableProcesses())
    }

    private fun getAvailableDevices(): List<DeviceOption> {
        val devices = Adb.listDevices()

        return if (devices.isEmpty()) listOf(NoDevice) else devices
    }

    private fun getAvailableProcesses(): List<String> {
        val processes = Adb.listPackages().map { it.name }
        return if (processes.isEmpty()) listOf("No processes") else processes
    }

    private fun getAvailableDatabases(process: String): List<String> {
        val databases = Adb.listDatabasesForPackage(AndroidPackage(process))
        return if (databases.isEmpty()) listOf("No databases") else databases
    }

}

private fun DeviceOption.toDevice() = Device(name, type)