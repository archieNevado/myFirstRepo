package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.ui.components.SwitchingContainer;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = CommerceProductStructureForm.XTYPE)
@Scope("prototype")
public class CommerceProductStructureForm extends Container {
  public static final String XTYPE = "com.coremedia.livecontext.studio.config.commerceProductStructureForm";


  @FindByExtJS(itemId = "category", global = false)
  private Panel categoryContainer;


  @FindByExtJS(itemId = "variants", global = false)
  private Panel variantsContainer;

  public SwitchingContainer getCategoryContainer() {
    return categoryContainer.find(SwitchingContainer.class, ExtJSBy.xtype(SwitchingContainer.XTYPE));
  }

  public SwitchingContainer getVariantsContainer() {
    return variantsContainer.find(SwitchingContainer.class, ExtJSBy.xtype(SwitchingContainer.XTYPE));
  }
}
