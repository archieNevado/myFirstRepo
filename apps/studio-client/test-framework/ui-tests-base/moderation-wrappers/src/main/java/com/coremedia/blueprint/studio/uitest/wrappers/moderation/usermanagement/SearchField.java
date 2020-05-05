package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.data.Store;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import org.springframework.context.annotation.Scope;

import javax.inject.Named;

@ExtJSObject
@Scope("prototype")
@Named("usermanagement.SearchField")
public class SearchField extends ComboBoxField {

  @FindByExtJS(expression = "self.getStore()", global = false)
  private Store store;

  @Override
  public Store getStore() {
    return store;
  }
}
