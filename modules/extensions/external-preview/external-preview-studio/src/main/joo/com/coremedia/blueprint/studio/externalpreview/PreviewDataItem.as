package com.coremedia.blueprint.studio.externalpreview {
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;

import net.jangaroo.net.URIUtils;

public class PreviewDataItem {
  private var content:Content;
  private var active:Boolean = false;
  private var preview:Boolean = true;

  public function PreviewDataItem(content:Content) {
    this.content = content;
    this.preview = !isExcludedDocumentTypeWithoutPreview(content);
  }


  /**
   * Checks if the given content is in the list of excluded previewable documents.
   * @param content The content to check.
   * @return True, if the given document does not support a preview.
   */
  private static function isExcludedDocumentTypeWithoutPreview(content:Content):Boolean {
    var exclusions:Array = editorContext.getDocumentTypesWithoutPreview();
    for (var i:int = 0; i < exclusions.length; i++) {
      var type:String = exclusions[i];
      if (type === content.getType().getName()) {
        return true;
      }
    }
    return false;
  }

  public function setActive(b:Boolean):void {
    active = b;
  }

  public function asJSON():Object {
    var name:String = content.getName();
    var previewUrl:String = content.getPreviewUrl();
    if (previewUrl.indexOf("//") !== 0 && !URIUtils.parse(previewUrl).isAbsolute) {
      previewUrl = ExternalPreviewStudioPluginBase.CONTENT_PREVIEW_URL_PREFIX + content.getPreviewUrl();
    }
    if(previewUrl.indexOf("//") === 0) {
      previewUrl = window.location.protocol + previewUrl;
    }
    
    return {
      active:active,
      modificationDate:content.get('modificationDate'),
      name:name,
      preview:this.preview,
      id: IdHelper.parseContentId(content),
      previewUrl: previewUrl,
      lifecycleStatus: content.getLifecycleStatus()
    }
  }

  public function getContentId():int {
    return IdHelper.parseContentId(content);
  }
}
}
