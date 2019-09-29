package com.coremedia.livecontext.sfcc.studio.action {
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.StoreImpl;
import com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin_properties;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Action;
import ext.Component;

import js.Window;

import mx.resources.ResourceManager;

/**
 * This action is intended to be used from within EXML, only.
 */
[ResourceBundle('com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin')]
public class OpenBusinessManagerActionBase extends Action {

  public static const BM_WINDOW_NAME:String = "business manager";

  private var disabledExpression:ValueExpression;

  private var bmLink;

  /**
   * @param config the configuration object
   */
  public function OpenBusinessManagerActionBase(config:OpenBusinessManagerAction = null) {
    super(OpenBusinessManagerAction(ActionConfigUtil.extendConfiguration(ResourceManager.getInstance().getResourceBundle(null, 'com.coremedia.livecontext.sfcc.studio.EcommerceSfccStudioPlugin').content, config, 'openBusinessManager',
            {
              handler: function ():void {
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
            })));
    disabledExpression = ValueExpressionFactory.createFromFunction(updateStoreUrl);
    disabledExpression.addChangeListener(updateDisabledStatus);
  }

  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    //broadcast the disable state after the add of a component
    updateDisabledStatus();
  }

  private function updateDisabledStatus():void {
    var value:* = disabledExpression.getValue();
    var disabled:Boolean = value === undefined || value;
    setDisabled(disabled);
  }

  private function updateStoreUrl():Boolean {
    var store:StoreImpl = CatalogHelper.getInstance().getActiveStoreExpression().getValue() as StoreImpl;
    if (!store) {
      bmLink = undefined;
    }
    else {
      if (!store.isLoaded()) {
        store.load(updateStoreUrl);
      }
      if (store.getVendorName() !== "Salesforce") {
        bmLink = undefined;
      }
      else {
        bmLink = store.getVendorUrl();
      }
    }
    return !bmLink;
  }
}
}
