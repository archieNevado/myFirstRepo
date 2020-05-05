package com.acme.coremedia.studio.pluginperms {

import com.coremedia.cms.editor.sdk.EditorPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;

/**
 * An editor plugin that does nothing.
 */
public class EmptyEditorPlugin implements EditorPlugin {
  public function init(editorContext:IEditorContext):void {}
}
}
