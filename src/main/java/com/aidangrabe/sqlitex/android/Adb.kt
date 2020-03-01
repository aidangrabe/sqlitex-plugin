package com.aidangrabe.sqlitex.android

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ScriptRunnerUtil
import org.jetbrains.android.sdk.AndroidSdkUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


interface DeviceOption {
    val name: String
    val type: String
}

data class Device(
        override val name: String,
        override val type: String
) : DeviceOption

object NoDevice : DeviceOption {
    override val name: String = "No Device"
    override val type: String = "N/A"
}

/**
 * Simple data class for representing an Android package. Ie. an app installed on the device
 */
data class AndroidPackage(val name: String)

private const val PATH_APP_DATABASES: String = "databases"
private const val PATH_DATABASES_FILE_EXTENSION: String = ".db"

object Adb {

    var currentDevice: Device? = null

    private val isInstalled by lazy { adbAvailable() }

    private val adb: File = AndroidSdkUtils.getAdb(null) ?: throw AdbNotInstalledException()

    fun listDevices() = parseDeviceList(exec("devices"))

    fun listDatabasesForPackage(androidPackage: AndroidPackage): List<String> {
        return listDirectoryContents(androidPackage, PATH_APP_DATABASES)
                .split("\n")
                .map(String::trim)
                .filter(CharSequence::isNotBlank)
                .filter { it.endsWith(PATH_DATABASES_FILE_EXTENSION) }
    }

    fun listPackages(): List<AndroidPackage> {
        val packageNameRegex = """package:(.*)""".toRegex()

        val packages = exec("shell", "pm", "list", "packages")
        return packageNameRegex.findAll(packages)
                .mapNotNull { it.groups[1]?.value?.trim() }
                .map { AndroidPackage(it) }
                .toList()
    }

    private fun listDirectoryContents(androidPackage: AndroidPackage, path: String): String =
            runAs(androidPackage, "ls", path)

    fun runAs(androidPackage: AndroidPackage, vararg args: String): String =
            exec("shell", "run-as", androidPackage.name, *args)

    fun exec(vararg command: String): String {
        if (!isInstalled) {
            throw AdbNotInstalledException()
        }

        val options = arrayListOf<String>()
        currentDevice?.let {
            with(options) {
                add("-s")
                add(it.name)
            }
        }

        val commandToExecute = arrayOf(adb.path) + options + command

        val generalCommandLine = GeneralCommandLine(*commandToExecute)
        generalCommandLine.charset = Charset.forName("UTF-8")

        return ScriptRunnerUtil.getProcessOutput(generalCommandLine)
    }

    private fun adbAvailable(): Boolean {
        return try {
            Runtime.getRuntime().exec(adb.path).waitFor(5, TimeUnit.SECONDS)
            true
        } catch (error: IOException) {
            false
        }
    }

    private fun parseDeviceList(deviceText: String): List<Device> {
        val regex = """([\w]+-\d+)\s+(\w+)""".toRegex()

        return regex.findAll(deviceText)
                .map {
                    val name = it.groups[1]?.value
                    val type = it.groups[2]?.value
                    if (name != null && type != null) {
                        Device(name, type)
                    } else {
                        null
                    }
                }
                .filterNotNull()
                .toList()

    }
}

class AdbNotInstalledException : Exception("adb command is not installed. Make sure the Android tools are installed and on your path")