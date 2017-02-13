package com.coremedia.livecontext.studio.action {
import com.coremedia.blueprint.base.components.util.ContentCreationUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;

/**
 * This action is intended to be used from within EXML, only.
 *
 */
public class AugmentCategoryActionBase extends CreateCatalogObjectDocumentAction {

  /**
   * @param config the configuration object
   */
  public function AugmentCategoryActionBase(config:AugmentCategoryAction = null) {
    super(config);
  }

  override protected function myHandler():void {
    var catalogObject:CatalogObject = getCatalogObjects()[0];
    if (isCorrectType(catalogObject)) {
      //call CategoryAugmentationService
      var augmentCategoryUri:String = catalogObject.getStore().getUriPath() + "/augment";
      var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod(augmentCategoryUri, 'POST', true);
      remoteServiceMethod.request({$Ref: catalogObject.getUriPath()}, function (response:RemoteServiceMethodResponse):void {
        var content:Content = Content(response.getResponseJSON());
        content.load(ContentCreationUtil.initializeAndShow);
      });
    }
  }
}
}
