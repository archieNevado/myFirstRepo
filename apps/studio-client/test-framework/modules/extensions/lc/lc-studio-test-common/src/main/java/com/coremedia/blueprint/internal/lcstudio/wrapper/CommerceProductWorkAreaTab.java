package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = "com.coremedia.livecontext.studio.config.commerceProductWorkAreaTab")
@Scope("prototype")
public class CommerceProductWorkAreaTab extends CommerceWorkAreaTab {

  public static final String CONTENT_TAB_ITEM_ID = "contentTab";
  public static final String STRUCTURE_TAB_ITEM_ID = "structureTab";

  @FindByExtJS(itemId = CONTENT_TAB_ITEM_ID)
  private CommerceProductContentForm commerceProductContentForm;

  @FindByExtJS(itemId = STRUCTURE_TAB_ITEM_ID)
  private CommerceProductStructureForm commerceProductStructureForm;

  public CommerceProductContentForm getContentForm() {
    return commerceProductContentForm;
  }

  public CommerceProductStructureForm getStructureForm() {
    return commerceProductStructureForm;
  }


}
