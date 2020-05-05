package com.coremedia.blueprint.studio.uitest.base.wrappers.forms;

import com.coremedia.uitesting.cms.editor.components.premular.PropertyFieldGroup;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.Checkbox;
import com.coremedia.uitesting.ext3.wrappers.form.CheckboxGroup;
import com.coremedia.uitesting.ext3.wrappers.form.DefaultCheckboxGroup;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = "com.coremedia.blueprint.studio.config.playersettingspropertygroup")
@Scope("prototype")
public class PlayerSettingsPropertyGroup extends PropertyFieldGroup {

  @SuppressWarnings("unused")
  @FindByExtJS(xtype = "checkboxgroup")
  private DefaultCheckboxGroup checkboxGroup;

  public PlayerSettingsPropertyGroup() {
  }

  public CheckboxGroup<Checkbox> getCheckboxGroup() {
    return checkboxGroup;
  }
}
