package com.aidangrabe.sqlitex.swingx

import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox

/** Creates a new model for the [JComboBox] with the given [items]. */
inline fun <reified T> JComboBox<T>.setItems(items: List<T>) {
    model = DefaultComboBoxModel(items.toTypedArray())
}

/** Returns all items that are set on the current [JComboBox.getModel]. */
inline fun <reified T> JComboBox<T>.getItems(): List<T> {
    val size = model.size
    val items = mutableListOf<T>()

    for (i in 0 until size) {
        items.add(model.getElementAt(i))
    }

    return items.toList()
}