package com.aidangrabe.sqlitex.swingx

import javax.swing.JTextArea
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.BadLocationException
import javax.swing.text.Document

class DocumentTextChangeListener(
        private val listener: (String) -> Unit
): DocumentListener {

    override fun changedUpdate(e: DocumentEvent) = notifyListener(e)

    override fun insertUpdate(e: DocumentEvent) = notifyListener(e)

    override fun removeUpdate(e: DocumentEvent) = notifyListener(e)

    private fun notifyListener(e: DocumentEvent) {
        val text = e.document.tryGetText()
        listener(text)
    }

}

fun JTextArea.addTextChangedListener(listener: (String) -> Unit) {
    document.addDocumentListener(DocumentTextChangeListener(listener))
}

fun Document.tryGetText(): String {
    return try {
        getText(0, length)
    } catch (e: BadLocationException) {
        e.printStackTrace()
        ""
    }
}