package com.coremedia.livecontext.studio.pbe {
import com.coremedia.cms.editor.sdk.components.breadcrumb.BreadcrumbElement;
import com.coremedia.cms.editor.sdk.config.breadcrumbElement;
import com.coremedia.cms.editor.sdk.preview.MetadataHelper;
import com.coremedia.cms.editor.sdk.preview.metadata.MetadataTreeNode;
import com.coremedia.ecommerce.studio.model.Store;

import ext.Component;
import ext.Plugin;

public class DisableStoreNodePlugin implements Plugin {
  public function init(component:Component):void {
    var element:BreadcrumbElement = component as BreadcrumbElement;
    if (!element) {
      throw new Error("unsupported component type: " + component.xtype);
    }
    var elementConfig:breadcrumbElement = breadcrumbElement(element.initialConfig);
    if (!element.disabled && elementConfig.disableElementStrategy) {
      var metadataNode:MetadataTreeNode = elementConfig.treeModel.getNodeModel(elementConfig.breadcrumbElementId) as MetadataTreeNode;
      if (metadataNode) {
        var isStore:Boolean = MetadataHelper.getBeanMetadataValue(metadataNode) is Store;
        if (isStore) {
          element.disable();
        }
      }
    }
  }

}
}