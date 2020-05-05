package com.coremedia.blueprint.studio.uitest.wrappers.moderation.custom;

import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import org.springframework.context.annotation.Scope;

import static org.hamcrest.CoreMatchers.containsString;

@ExtJSObject
@Scope("prototype")
public class ValidatableTextField extends TextField {

  public void invalid() {
    activeError().assertThat(containsString("li"));
  }

  public void isValid() {
    String error = activeError().get();
    assert (error == null || !error.contains("li")) : "Textfield is valid";
  }
}
