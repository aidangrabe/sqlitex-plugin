package com.aidangrabe.sqlitex.model

import com.intellij.ide.util.PropertiesComponent

data class DatabaseSession(
        val deviceId: String,
        val process: String,
        val database: String
) {
    //    private val propertiesComponent: PropertiesComponent = PropertiesComponent.getInstance()
//
//    init {
//        propertiesComponent.setValue(KEY_PROCESS, process)
//        propertiesComponent.setValue(KEY_DATABASE, database)
//    }
//

    fun persist(properties: PropertiesComponent) {
        properties.setValue(KEY_PROCESS, process)
        properties.setValue(KEY_DATABASE, database)

        println("Persisted: $this")
    }

    companion object {
        private val PACKAGE = DatabaseSession::class.java.`package`
        private val KEY_PROCESS = "$PACKAGE.key_process"
        private val KEY_DATABASE = "$PACKAGE.key_database"

        fun from(properties: PropertiesComponent): DatabaseSession {
            val process = properties.getValue(KEY_PROCESS, "")
            val database = properties.getValue(KEY_DATABASE, "")

            val session = DatabaseSession("", process, database)
            println("Loaded: $session")
            return session
        }

    }

}