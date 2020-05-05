package com.coremedia.blueprint.studio.uitest.base.wrappers.project;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ui.IconButton;

import javax.inject.Singleton;

@ExtJSObject(xtype = "com.coremedia.blueprint.studio.config.controlroom.projectQuickCreateLinklistMenu")
@Singleton
public class ProjectToolbarCreateContentButton extends IconButton {

  public void select(String docType) {
    getMenu().clickItem(ExtJSBy.itemId(docType));
  }
}
