package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.MessageBox;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject
@Scope("prototype")
public class UserDetailsPanel extends Panel {
  private static final String DELETE_PROFILE_IMAGE_DIALOG_TITLE = "Delete Profile Image";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "switchPanel", global = false)
  private UserToSearchSwitchPanel userToSearchSwitchPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-user-details-data-panel", global = false)
  private UserDetailsDataPanel userDetailsDataPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-user-meta-data-panel", global = false)
  private UserMetadataPanel userMetadataPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-user-restrictions-panel", global = false)
  private UserRestrictionPanel userRestrictionPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "stateMessages", global = false)
  private StringDisplayField stateMessagesDisplayField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "resendRegistrationMessageDisplayField", global = false)
  private StringDisplayField resendRegistrationMessageDisplayField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "openEmailWindowButton", global = false)
  private Button openEmailWindowButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "deleteButton", global = false)
  private Button deleteButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "applyButton", global = false)
  private Button applyButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(property = "text", propertyValue = "Cancel", global = false)
  private Button cancelButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "cm-elastic-social-userdetail-confirm-changes-window", global = false)
  private ChangedProfileConfirmationWindow changedProfileConfirmationWindow;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "cm-elastic-social-email-window", global = false)
  private EMailWindow emailWindow;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private MessageBox messageBox;

  public UserToSearchSwitchPanel getUserToSearchSwitchPanel() {
    return userToSearchSwitchPanel;
  }

public UserDetailsDataPanel getUserDetailsDataPanel() {
  return userDetailsDataPanel;
  }

  public UserMetadataPanel getUserMetadataPanel() {
    return userMetadataPanel;
  }

  public UserRestrictionPanel getUserRestrictionPanel() {
    return userRestrictionPanel;
  }

  public StringDisplayField getStateMessagesDisplayField() {
    return stateMessagesDisplayField;
  }

  public Button getOpenEmailWindowButton() {
    return openEmailWindowButton;
  }

  public Button getDeleteButton() {
    return deleteButton;
  }

  public Button getApplyButton() {
    return applyButton;
  }

  public Button getCancelButton() {
    return cancelButton;
  }

  public MessageBox getDeleteConfirmationMessageBox() {
    return messageBox.visible().await() && "Delete user\u2026".equals(messageBox.getDialog().title().get()) ? messageBox : null;
  }

  public ChangedProfileConfirmationWindow getChangedProfileConfirmationWindow() {
    return changedProfileConfirmationWindow;
  }

  public StringDisplayField getResendRegistrationMessageDisplayField() {
    return resendRegistrationMessageDisplayField;
  }

  public EMailWindow getEmailWindow() {
    return emailWindow;
  }

  public MessageBox getDeleteUserImageConfirmationMessageBox() {
    return messageBox.visible().await() && DELETE_PROFILE_IMAGE_DIALOG_TITLE.equals(messageBox.getDialog().title().get()) ? messageBox : null;
  }
}
