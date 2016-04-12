package com.coremedia.livecontext.studio.desktop {
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.desktop.WorkAreaTab;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ecommerce.studio.helper.AugmentationUtil;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.CatalogObjectPropertyNames;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.livecontext.studio.config.commerceWorkAreaTab;

import ext.util.StringUtil;

public class CommerceWorkAreaTabBase extends WorkAreaTab {

  public function CommerceWorkAreaTabBase(config:commerceWorkAreaTab = null) {
    config.entity = config.entity || config.object;
    super(config);

    var catalogObject:CatalogObject = getCatalogObject();

    catalogObject && catalogObject.load(function():void {
      replaceTab(false);
      catalogObject.addPropertyChangeListener(CatalogObjectPropertyNames.CONTENT, replaceTab);
    });

    catalogObject && catalogObject.addValueChangeListener(reloadPreview);
  }

  override protected function calculateTitle():String {
    var catalogObject:CatalogObject = getCatalogObject();
    return catalogObject && CatalogHelper.getInstance().getDecoratedName(catalogObject);
  }

  override protected function calculateIcon():String {
    var catalogObject:CatalogObject = getCatalogObject();
    return catalogObject ? AugmentationUtil.getTypeCls(catalogObject) : super.calculateIcon();
  }

  private function getCatalogObject():CatalogObject {
    return getEntity() as CatalogObject;
  }

  private function replaceTab(showMessage:Boolean = true):void {
    var augmentingContent:Content = getCatalogObject().get(CatalogObjectPropertyNames.CONTENT) as Content;
    if (augmentingContent) { // the commerce object has been augmented
      editorContext.getWorkAreaTabManager().replaceTab(getCatalogObject(), augmentingContent);
      if (rendered && showMessage) { //show the message only for the already rendered tabs
        augmentingContent.load(function ():void {
          if (augmentingContent.getCreator() !== session.getUser()) { //don't show the message if the category is augmented by myself.
            showAugmentationMessage();
          }
        });
      }
    }
  }

  private function showAugmentationMessage():void {
    var title:String = LivecontextStudioPlugin_properties.INSTANCE.Category_augmentedMessage_title;
    var categoryName:String = CatalogHelper.getInstance().getDecoratedName(getCatalogObject());
    var text:String = StringUtil.format(LivecontextStudioPlugin_properties.INSTANCE.Category_augmentedMessage_text, categoryName);

    MessageBoxUtil.showInfo(title, text);
  }

  private function reloadPreview():void {
    var previewPanel:PreviewPanel = getComponent(commerceWorkAreaTab.PREVIEW_PANEL_ITEM_ID) as PreviewPanel;
    //TODO: the preview panel cannot be found sometimes
    previewPanel && previewPanel.reloadFrame();
  }


  override protected function beforeDestroy():void {
    getCatalogObject().removePropertyChangeListener(CatalogObjectPropertyNames.CONTENT, replaceTab);
    getCatalogObject().removeValueChangeListener(reloadPreview);
    super.beforeDestroy();
  }

}
}