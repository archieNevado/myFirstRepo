package com.coremedia.blueprint.studio.uitest.base.wrappers.forms;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.NumberField;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = "com.coremedia.blueprint.studio.config.fixedIndexConfigurationForm")
@Scope("prototype")
public class IndexConfigurationForm extends Container {

  @SuppressWarnings("unused")
  @FindByExtJS(xtype = "numberfield")
  private NumberField index;

  public IndexConfigurationForm() {
  }

  public NumberField getFixedIndexField() {
    return index;
  }
}
