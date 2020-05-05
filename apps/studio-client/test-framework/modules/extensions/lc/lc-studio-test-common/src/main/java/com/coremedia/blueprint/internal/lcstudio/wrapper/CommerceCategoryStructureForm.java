package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.ui.components.SwitchingContainer;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = CommerceCategoryStructureForm.XTYPE)
@Scope("prototype")
public class CommerceCategoryStructureForm extends Container {
  public static final String XTYPE = "com.coremedia.livecontext.studio.config.commerceCategoryStructureForm";

  @FindByExtJS(itemId = "parentCategory", global = false)
  private Panel parentCategoryContainer;

  @FindByExtJS(itemId = "subcategories", global = false)
  private Panel subCategoriesContainer;

  @FindByExtJS(itemId = "products", global = false)
  private Panel productsContainer;


  public SwitchingContainer getParentCategoryContainer() {
    return parentCategoryContainer.find(SwitchingContainer.class, ExtJSBy.xtype(SwitchingContainer.XTYPE));
  }

  public SwitchingContainer getSubCategoriesContainer() {
    return subCategoriesContainer.find(SwitchingContainer.class, ExtJSBy.xtype(SwitchingContainer.XTYPE));
  }

  public SwitchingContainer getProductsContainer() {
    return productsContainer.find(SwitchingContainer.class, ExtJSBy.xtype(SwitchingContainer.XTYPE));
  }
}
