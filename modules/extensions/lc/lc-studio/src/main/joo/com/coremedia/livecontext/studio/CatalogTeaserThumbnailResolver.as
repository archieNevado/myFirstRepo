package com.coremedia.livecontext.studio {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.util.ImageLinkListRenderer;

/**
 * Catalog content thumbnails not necessarily based on blobs but could be
 * external URLs too.
 */
public class CatalogTeaserThumbnailResolver extends CatalogThumbnailResolver {

  public function CatalogTeaserThumbnailResolver(docType:String) {
    super(docType);
  }

  override public function getThumbnail(model:Object, operations:String):Object {
    return renderLiveContextProductTeaserPreview(model as Content);
  }

  private function renderLiveContextProductTeaserPreview(content:Content):Object {
    var result:String = editorContext.getThumbnailUri(content, ImageLinkListRenderer.DEFAULT_CROPPING);
    if(result === undefined) {
      return undefined;
    }

    if(!result){
      return renderLiveContextPreview(content);
    }

    return result;
  }
}
}