package com.coremedia.livecontext.ibm.studio.action {
import com.coremedia.livecontext.studio.action.*;
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.MarketingSpot;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.livecontext.ibm.studio.mgmtcenter.ManagementCenterUtil;

import mx.resources.ResourceManager;

/**
 * This action is intended to be used from within EXML, only.
 *
 */
[ResourceBundle('com.coremedia.livecontext.studio.LivecontextStudioPlugin')]
public class OpenInManagementCenterActionBase extends LiveContextCatalogObjectAction {

  /**
   * @param config the configuration object
   */
  public function OpenInManagementCenterActionBase(config:OpenInManagementCenterAction = null) {
    super(OpenInManagementCenterAction(ActionConfigUtil.extendConfiguration(ResourceManager.getInstance().getResourceBundle(null, 'com.coremedia.livecontext.studio.LivecontextStudioPlugin').content, config, 'openInManagementCenter', {handler: doExecute})));
  }

  override protected function isDisabledFor(catalogObjects:Array):Boolean {
    if (!ManagementCenterUtil.isSupportedBrowser()) {
      return true;
    }

    //the action should be enabled only if there is only one catalog object and it is a product
    if (catalogObjects.length !== 1) {
      return true;
    }
    var catalogObject:CatalogObject = catalogObjects[0];
    if (catalogObject is Product || catalogObject is Category || catalogObject is MarketingSpot ) {
      if (catalogObject.getState().exists) {
        if (catalogObject is Category) {
          if (Category(catalogObject).getParent() === null) {
            //for the root category we don't have any view on the WCS.
            return true;
          }
        }
        return false;
      }
    }
    return true;
  }

  private function doExecute():void {
    if (!ManagementCenterUtil.isSupportedBrowser()) {
      return;
    }
    var catalogObject:CatalogObject = getCatalogObjects()[0];
    //currently we display only products and categories
    if (catalogObject is Product) {
      ManagementCenterUtil.openProduct(Product(catalogObject));
    }  else if (catalogObject is Category) {
      ManagementCenterUtil.openCategory(Category(catalogObject));
    } else if (catalogObject is MarketingSpot) {
      ManagementCenterUtil.openMarketingSpot(MarketingSpot(catalogObject));
    }
  }
}
}
