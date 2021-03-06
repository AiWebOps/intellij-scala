package org.jetbrains.plugins.scala.externalHighlighters

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.scala.util.CompilationId

/**
 * Project state generated by compiler.
 * Using for storing highlightings.
 */
private object CompilerGeneratedStateManager {

  def get(project: Project): CompilerGeneratedState =
    mutableState(project).state

  def update(project: Project, newState: CompilerGeneratedState): CompilerGeneratedState = {
    mutableState(project).state = newState
    newState
  }

  private def mutableState(project: Project): MutableState =
    ServiceManager.getService(project, classOf[MutableState])

  private class MutableState {
    var state: CompilerGeneratedState = CompilerGeneratedState(Map.empty, 1.0, Set.empty)
  }
}
