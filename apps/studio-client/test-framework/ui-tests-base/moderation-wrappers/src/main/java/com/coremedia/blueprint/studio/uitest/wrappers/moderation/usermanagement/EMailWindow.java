package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.TextArea;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class EMailWindow extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "cm-elastic-social-email-window-mail-selector", global = false)
  private ComboBoxField emailSelector;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "cm-elastic-social-email-window-text-area", global = false)
  private TextArea textArea;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "cm-elastic-social-email-window-apply-button", global = false)
  private Button applyButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "cm-elastic-social-email-window-cancel-button", global = false)
  private Button cancelButton;

  public ComboBoxField getEmailSelector() {
    return emailSelector;
  }

  public TextArea getTextArea() {
    return textArea;
  }

  public Button getApplyButton() {
    return applyButton;
  }

  public Button getCancelButton() {
    return cancelButton;
  }
}
