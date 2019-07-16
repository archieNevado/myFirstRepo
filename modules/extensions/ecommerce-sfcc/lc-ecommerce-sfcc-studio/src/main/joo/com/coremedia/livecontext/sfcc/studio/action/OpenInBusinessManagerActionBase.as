package com.coremedia.livecontext.sfcc.studio.action {
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.MarketingSpot;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin_properties;
import com.coremedia.livecontext.studio.action.LiveContextCatalogObjectAction;

import js.Window;

import mx.resources.ResourceManager;

/**
 * This action is intended to be used from within EXML, only.
 */
[ResourceBundle('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin')]
public class OpenInBusinessManagerActionBase extends LiveContextCatalogObjectAction {

  public static const BM_WINDOW_NAME:String = "business manager";

  /**
   * @param config the configuration object
   */
  public function OpenInBusinessManagerActionBase(config:OpenInBusinessManagerActionBase = null) {
    super(OpenInBusinessManagerAction(ActionConfigUtil.extendConfiguration(ResourceManager.getInstance().getResourceBundle(null, 'com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin').content, config, 'openInBusinessManager', {handler: doExecute})));
  }

  override protected function isDisabledFor(catalogObjects:Array):Boolean {
    //the action should be enabled only if there is only one catalog object and it is a product
    if (catalogObjects.length !== 1) {
      return true;
    }
    var catalogObject:CatalogObject = catalogObjects[0];
    if (catalogObject is Product || catalogObject is Category || catalogObject is MarketingSpot) {
      if (catalogObject.getState().exists) {
        if (catalogObject is Category) {
          if (Category(catalogObject).getParent() === null) {
            //for the root category we don't have any view.
            return true;
          }
        }
        return false;
      }
    }
    return true;
  }

  override protected function isHiddenFor(catalogObjects:Array):Boolean {
    //we need a catalog object to access the store object.
    if (catalogObjects.length === 0) {
      return false;
    }
    var catalogObject:CatalogObject = catalogObjects[0] as CatalogObject;
    if (!catalogObject) {
      return true;
    }

    var store:Store = catalogObject.getStore();
    if (!store) {
      return true;
    }

    return store.getVendorName() !== "Salesforce" || super.isHiddenFor(catalogObjects);
  }

  private function doExecute():void {
    var catalogObjects:Array = getCatalogObjects();
    if (catalogObjects && catalogObjects.length > 0) {
      var catalogObject:CatalogObject = catalogObjects[0];

      var bmLink:String = catalogObject.getCustomAttribute("c_bm_link") as String;
      if (bmLink) {
        var wnd:Window = window.open(bmLink, BM_WINDOW_NAME);
        if (wnd) {
          // Focus the opened window
          wnd.focus();
        } else {
          // Focus the opened window
          MessageBoxUtil.showWarn(
                  EcommerceSfccStudioPlugin_properties.INSTANCE.Action_openInBusinessManager_messageBox_warn_title,
                  EcommerceSfccStudioPlugin_properties.INSTANCE.Action_openInBusinessManager_messageBox_warn_text);
        }
      }
    }
  }
}
}
