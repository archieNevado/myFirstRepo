package com.acme.coremedia.studio.preview {
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.preview.PreviewUrlServiceUtil;

public class PreviewPluginBase extends StudioPlugin {
  public function PreviewPluginBase(config:PreviewPlugin = null) {
    super(config);
  }

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);
    PreviewUrlServiceUtil.isPreviewVariantsEnabled = function():Boolean {return false};
  }
}
}
