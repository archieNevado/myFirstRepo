package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.dialog;

import com.coremedia.uitesting.ext3.wrappers.Window;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;

import javax.inject.Singleton;

@ExtJSObject(xtype = "com.coremedia.elastic.social.studio.config.validationMessageBox")
@Singleton
public class ValidationWindow extends Window {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-userdetail-validation-message-box-correct-button", global = false)
  private Button correctButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-userdetail-validation-message-box-discard-button", global = false)
  private Button discardButton;

  public Button getDiscardButton() {
    return discardButton;
  }

  public Button getCorrectButton() {
    return correctButton;
  }
}
