package com.aidangrabe.sqlitex;

import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

public class MyToolWindow {
    private JPanel myToolWindowContent;
    private JTable resultsTable;
    private JTextArea queryField;

    public MyToolWindow(ToolWindow toolWindow) {
        System.out.println("Hello World!");

        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Column 1", "Column 2"}, 20);
        tableModel.addRow(new Object[]{"Value 1", "Value 2"});
        resultsTable.setModel(tableModel);

        queryField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("insert updated: " + getDocumentText(e.getDocument()));

            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                System.out.println("remove updated: " + getDocumentText(e.getDocument()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changed updated: " + getDocumentText(e.getDocument()));
            }
        });
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    private String getDocumentText(Document document) {
        try {
            return document.getText(0, document.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

}
