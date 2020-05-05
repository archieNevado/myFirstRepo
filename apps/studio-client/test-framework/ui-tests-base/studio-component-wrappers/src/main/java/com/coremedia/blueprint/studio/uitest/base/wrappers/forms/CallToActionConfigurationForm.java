package com.coremedia.blueprint.studio.uitest.base.wrappers.forms;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.Checkbox;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = "com.coremedia.blueprint.studio.config.callToActionConfigurationForm")
@Scope("prototype")
public class CallToActionConfigurationForm extends Container {

  @SuppressWarnings("unused")
  @FindByExtJS(xtype = "checkboxfield")
  private Checkbox checkbox;

  @SuppressWarnings("unused")
  @FindByExtJS(xtype = "textfield")
  private TextField textField;

  public CallToActionConfigurationForm() {
  }

  public Checkbox getCheckbox() {
    return checkbox;
  }

  public TextField getTextField() {
    return textField;
  }
}
