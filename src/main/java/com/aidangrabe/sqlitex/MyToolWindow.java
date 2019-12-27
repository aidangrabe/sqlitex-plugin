package com.aidangrabe.sqlitex;

import com.aidangrabe.sqlitex.android.DeviceOption;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

/**
 * This class needs to be in Java as the GUI Designer cannot bind to Kotlin classes yet:
 * https://youtrack.jetbrains.com/issue/KT-6660
 */
public class MyToolWindow {
    private JPanel myToolWindowContent;
    private JTable resultsTable;
    private JTextArea queryField;
    private JComboBox<DeviceOption> devicePicker;
    private JComboBox<String> processPicker;
    private JComboBox<String> databasePicker;

    public MyToolWindow(ToolWindow toolWindow) {
        MainWindowViewHolder viewHolder = new MainWindowViewHolder(
                queryField, resultsTable, devicePicker, processPicker, databasePicker);

        new SqlitexMainWindow(viewHolder);
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

}
