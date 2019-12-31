package com.aidangrabe.sqlitex.data

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HtmlTableParser {

    /**
     * Parse the given Table HTML. This HTML should be the raw contents of the table itself, excluding the `<table>` tag.
     */
    fun parse(html: String): TableData {
        val document = Jsoup.parse("<html><body><table>$html</table></body></html>")

        val rows = parseRowsFromOutput(document)
        val columnsNames = parseColumnNamesFromOutput(document)

        return TableData(columnNames = columnsNames, rows = rows)
    }

    private fun parseRowsFromOutput(document: Document): List<List<String>> {
        val rowsOfCells = ArrayList<List<String>>()

        document.getElementsByTag("tr")
                .drop(1)    // skip the first row, it's the headers
                .forEach {
                    rowsOfCells.add(ArrayList(it.getElementsByTag("td").map { it.text() }))
                }

        return rowsOfCells
    }

    private fun parseColumnNamesFromOutput(document: Document): List<String> {
        return document.getElementsByTag("th").map { it.text() }
    }

}

data class TableData(
        val columnNames: List<String>,
        val rows: List<List<String>>
)