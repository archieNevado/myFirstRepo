package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.profile;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.custom.ValidatableTextField;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.ModerationDetailViewBase;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base.dialog.ValidationWindow;
import com.coremedia.uitesting.ext3.wrappers.Component;
import com.coremedia.uitesting.ext3.wrappers.MessageBox;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import net.joala.condition.BooleanCondition;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject
@Scope("prototype")
public class ModerationProfileDetailView extends ModerationDetailViewBase {

  private static final String USER_PROFILE_IMAGE_NO_PIC_CLASS = "cm-elastic-social-userdetail-nopic";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-moderation-tab-userdetails-name", global = false)
  private ValidatableTextField userNameTextField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-moderation-tab-userdetails-givenname", global = false)
  private ValidatableTextField givenNameTextField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-moderation-tab-userdetails-surname", global = false)
  private ValidatableTextField surNameTextField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-moderation-tab-userdetails-email", global = false)
  private ValidatableTextField emailTextField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "image", global = false)
  private Component imageComponent;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "no-image", global = false)
  private Component noImageComponent;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "extension-tab-panel-profile", global = false)
  private ModerationProfileExtensionTabPanel extensionTabPanel;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private MessageBox messageBox;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private ValidationWindow validationWindow;

  public ValidatableTextField getUserNameTextField() {
    return userNameTextField;
  }

  public ValidatableTextField getGivenNameTextField() {
    return givenNameTextField;
  }

  public ValidatableTextField getSurNameTextField() {
    return surNameTextField;
  }

  public ValidatableTextField getEmailTextField() {
    return emailTextField;
  }

  public BooleanCondition profileUserImageEmpty() {
    return noImageComponent.visible();
  }

  public MessageBox getDeleteConfirmationMessageBox() {
    return messageBox.visible().await() && "Delete user\u2026".equals(messageBox.getDialog().title().get()) ? messageBox : null;
  }

  public ValidationWindow getInvalidUserDialog() {
    return validationWindow;
  }

  public ModerationProfileExtensionTabPanel getExtensionTabPanel() {
    return extensionTabPanel;
  }
}
