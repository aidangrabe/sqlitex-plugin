package com.aidangrabe.sqlitex.swingx

import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class SimpleListCellRenderer<T>(
        private val classType: Class<T>,
        private val renderer: (T) -> String
) : DefaultListCellRenderer() {

    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        val text = if (classType.isInstance(value)) renderer(value as T) else value

        return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus)
    }

    companion object {

        inline fun <reified T> with(noinline renderer: (T) -> String): SimpleListCellRenderer<T> {
            return SimpleListCellRenderer(T::class.java, renderer)
        }

    }

}