package com.coremedia.blueprint.studio.uitest.base.wrappers.forms;

import com.coremedia.uitesting.cms.editor.components.premular.fields.DateTimePropertyField;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import org.springframework.context.annotation.Scope;

@ExtJSObject(xtype = "com.coremedia.blueprint.studio.config.visibilityConfigurationForm")
@Scope("prototype")
public class VisibilityConfigurationForm extends Container {

  @SuppressWarnings("unused")
  @FindByExtJS(itemId = "visibleFrom")
  private DateTimePropertyField visibleFrom;

  @SuppressWarnings("unused")
  @FindByExtJS(itemId = "visibleTo")
  private DateTimePropertyField visibleTo;

  public VisibilityConfigurationForm() {
  }

  public DateTimePropertyField getVisibleFrom() {
    return visibleFrom;
  }

  public DateTimePropertyField getVisibleTo() {
    return visibleTo;
  }
}
