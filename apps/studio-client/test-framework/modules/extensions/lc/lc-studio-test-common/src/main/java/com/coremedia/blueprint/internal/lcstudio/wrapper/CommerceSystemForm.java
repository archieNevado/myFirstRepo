package com.coremedia.blueprint.internal.lcstudio.wrapper;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = CommerceSystemForm.XTYPE)
@Scope("prototype")
public class CommerceSystemForm extends Container {
  public static final String XTYPE = "com.coremedia.livecontext.studio.config.commerceSystemForm";


  @FindByExtJS(itemId = "catalog", global = false)
  private StringDisplayField catalogField;

  public StringDisplayField getCatalogField() {
    return catalogField;
  }
}
