package com.coremedia.blueprint.studio.uitest.wrappers.moderation.contribution.base;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ui.ExtendedDisplayField;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ModerationDetailViewBase extends Panel {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-detail-statusbar-user-button", global = false)
  private Button userInformationContainerButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-detail-status-field-user", global = false)
  private ExtendedDisplayField contributionStatus;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-contribution-approve-button", global = false)
  private Button approveButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-contribution-reject-button", global = false)
  private Button rejectButton;

  public Condition<String> contributionStatus() {
    contributionStatus.getValidationState().isDefault().assertTrue();
    return contributionStatus.value();
  }

  public Condition<String> contributionErrorStatus() {
    contributionStatus.getValidationState().isError().assertTrue();
    return contributionStatus.value();
  }

  public Button userInformationContainerButton() {
    return userInformationContainerButton;
  }

  public Button getApproveButton() {
    return approveButton;
  }

  public Button getRejectButton() {
    return rejectButton;
  }

}
