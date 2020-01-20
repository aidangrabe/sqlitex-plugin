package com.aidangrabe.sqlitex.android

class SqliteContext(
        private val packageName: String,
        private val databaseName: String
) {

    private val splitTableNamesRegex by lazy { "\\s+".toRegex() }

    /** List all tables for the current database. */
    fun listTables(): List<String> {
        val tables = exec(".tables")
        return parseTableNamesFromConsoleOutput(tables)
    }

    /**
     * Execute the given [command] which can be an SQLite3 command or SQL query.
     *
     * @throws SqliteException when the issued command returns an error.
     */
    fun exec(command: String): String {
        val sqliteCommand = Sqlite3Command(command)
        val commandOutput = executeAdbCommand(sqliteCommand)

        if (isError(commandOutput)) throw SqliteException(sqliteCommand, commandOutput)

        return commandOutput
    }

    private fun executeAdbCommand(command: Sqlite3Command): String {
        println("Executing: '$command' for package: '$packageName' and database: '$databaseName'")

        return Adb.exec(
                "exec-out", "run-as", packageName,
                "sqlite3", "-html", "-header", "databases/$databaseName",
                command.getFormattedQueryOrCommand()
        )
    }

    private fun isError(output: String): Boolean = output.startsWith("Error: ")

    private fun parseTableNamesFromConsoleOutput(output: String): List<String> {
        // error with package name
        if (output.startsWith("run-as: Package ")) {
            return listOf("Error: Package name does not exist")
        }

        return output.split(splitTableNamesRegex)
                .filter(String::isNotEmpty)
    }

}

class Sqlite3Command(input: String) {

    val input = input.trim()

    fun isCommand(): Boolean = input.startsWith(".")

    fun isQuery(): Boolean = !isCommand()

    fun getFormattedQueryOrCommand(): String =
            if (isQuery()) "$input;" else input

}

/**
 * An [Exception] thrown when an error occurs wile executing an SQLite3 command.
 */
class SqliteException(val command: Sqlite3Command, output: String) : Exception(output)