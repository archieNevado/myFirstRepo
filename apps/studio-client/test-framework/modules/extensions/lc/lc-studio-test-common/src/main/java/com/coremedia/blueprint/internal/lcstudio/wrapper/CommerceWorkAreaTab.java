package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.cms.editor.components.premular.PreviewPanel;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.tab.TabPanel;
import com.coremedia.uitesting.webdriver.IdleIndicators;

import javax.inject.Inject;

abstract class CommerceWorkAreaTab extends Container {

  public static final String SYSTEM_TAB_ITEM_ID = "systemTab";
  public static final String EXPAND_BUTTON_ITEM_ID = "expandButton";

  @FindByExtJS(itemId = "tabs")
  private TabPanel tabs;

  @FindByExtJS(xtype = PreviewPanel.XTYPE, global = false)
  private PreviewPanel previewPanel;

  @FindByExtJS(itemId = EXPAND_BUTTON_ITEM_ID)
  private Panel expandButton;

  @FindByExtJS(itemId = SYSTEM_TAB_ITEM_ID)
  private CommerceSystemForm commerceSystemForm;

  @Inject
  private IdleIndicators idleIndicators;

  public PreviewPanel getPreviewPanel() {
    return previewPanel;
  }

  public CommerceSystemForm getSystemForm() {
    return commerceSystemForm;
  }

  public Panel getExpandButton() {
    return expandButton;
  }

  public void setActiveTab(final int index) {
    tabs.setActiveTab(index);
  }

}
