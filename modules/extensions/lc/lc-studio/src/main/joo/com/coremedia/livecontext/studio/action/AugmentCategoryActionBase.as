package com.coremedia.livecontext.studio.action {
import com.coremedia.blueprint.base.components.util.ContentCreationUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.livecontext.studio.config.augmentCategoryAction;
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
  public function AugmentCategoryActionBase(config:augmentCategoryAction = null) {
    super(config);
  }

  override protected function handler():void {
    var catalogObject:CatalogObject = getCatalogObjects()[0];
    if (isCorrectType(catalogObject)) {
      //call CategoryAugmentationService
      var requestParameters:Object = makeRequestParameters(Category(catalogObject));
      var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod("livecontext/category/augment", 'POST', true);
      remoteServiceMethod.request(requestParameters, function (response:RemoteServiceMethodResponse):void {
        var content:Content = Content(response.getResponseJSON());
        content.load(ContentCreationUtil.initializeAndShow);
      });
    }
  }

  private function makeRequestParameters(category:Category):Object {
    return {
      categoryUri: category.getUriPath()
    };
  }
}
}
