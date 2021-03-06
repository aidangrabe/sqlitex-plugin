package com.aidangrabe.sqlitex

import com.aidangrabe.sqlitex.android.*
import com.aidangrabe.sqlitex.data.HtmlTableParser
import com.aidangrabe.sqlitex.data.TableData
import com.aidangrabe.sqlitex.extensions.startsWithAnyOf
import com.aidangrabe.sqlitex.model.DatabaseSession
import com.intellij.ide.util.PropertiesComponent

class SqlitexMainWindow(
        private val viewHolder: MainWindowViewHolder
) {

    private val appProperties: PropertiesComponent = PropertiesComponent.getInstance()

    private var databaseSession: DatabaseSession = DatabaseSession.from(appProperties)
        set(value) {
            // no need to persist the new value if it's the same
            if (value == field) return

            field = value
            databaseSession.persist(appProperties)
        }

    init {
        // set up the listeners
        with(viewHolder) {
            submitQueryListener = { onSqlQuerySubmit(it) }
            deviceChangedListener = { onSelectedDeviceChanged(it) }
            onDevicePickerOpened = { onDevicePickerOpened() }
            processChangedListener = { onSelectedProcessChanged(it) }
            databaseChangedListener = { onSelectedDatabaseChanged(it) }
        }

        loadAvailableDevices()
    }

    private fun onDevicePickerOpened() {
        loadAvailableDevices()
    }

    private fun loadAvailableDevices() {
        val devices = getAvailableDevices()
        viewHolder.setAvailableDevices(devices)

        if (devices.size == 1) {
            onDeviceSelected(devices.first().toDevice())
        }
    }

    private fun onSelectedDatabaseChanged(database: String) {
        databaseSession = databaseSession.copy(database = database)
    }

    private fun onSelectedProcessChanged(process: String) {
        databaseSession = databaseSession.copy(process = process)

        // reset the selected databases as they will have changed
        viewHolder.setAvailableDatabases(getAvailableDatabases(process), selectFirst = true)
    }

    private fun onSelectedDeviceChanged(device: DeviceOption) {
        if (device !is NoDevice) {
            onDeviceSelected(device.toDevice())
        }
    }

    private fun onSqlQuerySubmit(query: String) {
        val tableData: TableData = try {
            val output = SqliteContext(databaseSession.process, databaseSession.database).exec(query)

            when {
                output.command.isCommand() -> {
                    val rows = output.output.split("\\s+".toRegex())
                            .map { listOf(it) }
                    TableData(listOf("Command result"), rows)
                }
                output.isEmpty() -> {
                    TableData(listOf("0 Results"), listOf(listOf("No results matched the given query")))
                }
                else -> {
                    val parser = HtmlTableParser()
                    parser.parse(output.output)
                }
            }
        } catch (e: SqliteException) {
            val errorMessage = e.message!!
            TableData(listOf("Error"), listOf(listOf(errorMessage)))
        }

        viewHolder.updateTableResults(tableData)
    }

    private fun onDeviceSelected(device: Device) {
        Adb.currentDevice = device
        databaseSession = databaseSession.copy(deviceId = device.name)

        val currentProcesses = viewHolder.getAvailableProcesses()
        val availableProcesses = getAvailableProcesses()
        val preselectedProcess = databaseSession.process

        viewHolder.setAvailableProcesses(availableProcesses)

        if (currentProcesses.isEmpty() && preselectedProcess.isNotEmpty() && availableProcesses.contains(preselectedProcess)) {
            viewHolder.setSelectedProcess(preselectedProcess)
        }
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