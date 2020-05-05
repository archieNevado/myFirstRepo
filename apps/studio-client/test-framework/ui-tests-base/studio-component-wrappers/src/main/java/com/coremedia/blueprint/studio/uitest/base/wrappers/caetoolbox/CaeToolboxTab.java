package com.coremedia.blueprint.studio.uitest.base.wrappers.caetoolbox;

import com.coremedia.blueprint.studio.uitest.base.wrappers.desktop.BlueprintFavoritesToolbar;
import com.coremedia.uitesting.cms.editor.components.desktop.WorkArea;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject(xtype = CaeToolboxTab.XTYPE)
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CaeToolboxTab extends Panel {
  public static final String XTYPE = "com.coremedia.caetools.plugin.config.caeToolboxTab";
  @Inject
  private CaeToolsTabPanel caeToolsTabPanel;
  @Inject
  private WorkArea workArea;
  @Inject
  private BlueprintFavoritesToolbar favoritesToolbar;

  public CaeToolsTabPanel getCaeToolsTabPanel() {
    return caeToolsTabPanel;
  }

  public void close() {
    visible().waitUntilTrue();
    workArea.removeActiveTab();
  }

  public void open() {
    favoritesToolbar.visible().waitUntilTrue();
    Button extensionsButton = favoritesToolbar.getExtensionsButton();
    extensionsButton.clickAndSelectFromMenu(ExtJSBy.itemId("caetoolboxItemId"));
    visible().waitUntilTrue();
  }

}
