package com.aidangrabe.sqlitex

import com.aidangrabe.sqlitex.android.*
import com.aidangrabe.sqlitex.data.HtmlTableParser

class SqlitexMainWindow(
        private val viewHolder: MainWindowViewHolder
) {

    private var process: String = ""
    private var database: String = ""

    init {
        // set up the listeners
        with(viewHolder) {
            submitQueryListener = { onSqlQuerySumbit(it) }

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
        }

        val devices = getAvailableDevices()
        viewHolder.setAvailableDevices(devices)

        if (devices.size == 1) {
            onDeviceSelected(devices.first().toDevice())
        }
    }

    private fun onSqlQuerySumbit(query: String) {
        val sqliteOutput = SqliteContext(process, database).exec("$query;")

        val parser = HtmlTableParser()
        val tableData = parser.parse(sqliteOutput)

        viewHolder.updateTableResults(tableData)
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
        val packagePrefixesToIgnore = listOf("com.android.", "com.google.")
        val processes = Adb.listPackages()
                .map { it.name }
                .filter { !it.startsWithAnyOf(packagePrefixesToIgnore) }
                .sorted()

        return if (processes.isEmpty()) listOf("No processes") else processes
    }

    private fun getAvailableDatabases(process: String): List<String> {
        val databases = Adb.listDatabasesForPackage(AndroidPackage(process))
        return if (databases.isEmpty()) listOf("No databases") else databases
    }

}

private fun DeviceOption.toDevice() = Device(name, type)

private fun String.startsWithAnyOf(prefixes: List<String>): Boolean {
    for (prefix in prefixes) {
        if (startsWith(prefix)) return true
    }
    return false
}