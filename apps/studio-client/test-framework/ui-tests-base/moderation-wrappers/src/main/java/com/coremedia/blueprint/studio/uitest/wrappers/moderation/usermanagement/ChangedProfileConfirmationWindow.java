package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ChangedProfileConfirmationWindow extends Panel {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-userdetail-confirm-changes-window-apply-button", global = false)
  private Button applyButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-userdetail-confirm-changes-window-discard-button", global = false)
  private Button discardButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-userdetail-confirm-changes-window-back-button", global = false)
  private Button backButton;

  public Button getApplyButton() {
    return applyButton;
  }

  public Button getDiscardButton() {
    return discardButton;
  }

  public Button getBackButton() {
    return backButton;
  }
}
