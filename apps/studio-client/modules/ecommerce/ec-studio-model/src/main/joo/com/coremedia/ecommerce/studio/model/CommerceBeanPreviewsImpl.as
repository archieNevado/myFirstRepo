package com.coremedia.ecommerce.studio.model {
import com.coremedia.ui.data.impl.PreviewsImpl;

[RestResource(uriTemplate="livecontext/previews/{resourceType:[^/]+}/{siteId:[^/]+}/{catalogAlias:[^/]+}/{workspaceId:[^/]+}/{externalId:.+}")]
public class CommerceBeanPreviewsImpl extends PreviewsImpl {
  /**
   * Do not invoke directly. Used by the bean factory to create content issues objects.
   *
   * @param uri the bean's URI
   */
  public function CommerceBeanPreviewsImpl(uri:String) {
    super(uri);
  }
}
}
