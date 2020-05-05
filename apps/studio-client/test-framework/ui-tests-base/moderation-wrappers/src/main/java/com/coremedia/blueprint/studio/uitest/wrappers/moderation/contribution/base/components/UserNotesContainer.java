package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.components;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.TextArea;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserNotesContainer extends Container {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "textarea", global = false)
  private TextArea notesTextArea;

  public TextArea getNotesTextArea() {
    return notesTextArea;
  }
}
