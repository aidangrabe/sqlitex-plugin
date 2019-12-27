package com.aidangrabe.sqlitex

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class WindowFactory : ToolWindowFactory {

    // Create the tool window content.
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainToolWindow = MyToolWindow(toolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(mainToolWindow.content, "", false)

        toolWindow.contentManager.addContent(content)
    }

}
