// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.intellij.diff.tools.combined.search

import com.intellij.diff.tools.combined.CombinedDiffViewer
import com.intellij.openapi.editor.Editor
import javax.swing.JComponent

/**
 * Provides "search in multiple editors" functionality for [CombinedDiffViewer]
 *
 *  @see [CombinedDiffSearchEditorActionHandler]
 *  @see [com.intellij.diff.tools.combined.CombinedDiffModelListener.onRequestsLoaded]
 */
interface CombinedDiffSearchProvider {

  fun installSearch(viewer: CombinedDiffViewer)
}

/**
 * Controller to update search in particular [com.intellij.diff.tools.combined.CombinedDiffMainUI]
 */
interface CombinedDiffSearchController {

  val searchComponent: JComponent

  fun update(editors: List<List<Editor>>)
}
