package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = "com.coremedia.livecontext.studio.config.commerceCategoryWorkAreaTab")
@Scope("prototype")
public class CommerceCategoryWorkAreaTab extends CommerceWorkAreaTab {

  private static final String CONTENT_TAB_ITEM_ID = "contentTab";
  private static final String PRODUCT_CONTENT_TAB_ITEM_ID = "pdpPageGridTab";
  private static final String STRUCTURE_TAB_ITEM_ID = "structureTab";

  @FindByExtJS(itemId = CONTENT_TAB_ITEM_ID)
  private CommerceCategoryContentForm commerceCategoryContentForm;

  @FindByExtJS(itemId = PRODUCT_CONTENT_TAB_ITEM_ID)
  private CommerceProductContentForm commerceProductContentForm;

  @FindByExtJS(itemId = STRUCTURE_TAB_ITEM_ID)
  private CommerceCategoryStructureForm commerceCategoryStructureForm;

  public CommerceCategoryContentForm getContentForm() {
    return commerceCategoryContentForm;
  }

  public CommerceProductContentForm getProductContentForm() {
    return commerceProductContentForm;
  }

  public CommerceCategoryStructureForm getStructureForm() {
    return commerceCategoryStructureForm;
  }
}
