package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.TextArea;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserInternalNotesContainer extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "textArea", global = false)
  private TextArea textArea;

  public TextArea getTextArea() {
    return textArea;
  }
}
