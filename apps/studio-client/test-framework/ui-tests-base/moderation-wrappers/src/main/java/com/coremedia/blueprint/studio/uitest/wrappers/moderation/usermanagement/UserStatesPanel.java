package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.Radio;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserStatesPanel extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "IGNORED", global = false)
  private Radio userStateIgnoredRadioButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "BLOCKED", global = false)
  private Radio userStateBlockedRadioButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "ACTIVATED", global = false)
  private Radio userStateActivatedRadioButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "MODERATION_REQUIRED", global = false)
  private Radio userStateModerationRequiredRadioButton;

  public Radio getUserStateIgnoredRadioButton() {
    return userStateIgnoredRadioButton;
  }

  public Condition<Boolean> getUserStateIgnoredRadioButtonValue() {
    return getUserStateIgnoredRadioButton().value();
  }

  public Radio getUserStateBlockedRadioButton() {
    return userStateBlockedRadioButton;
  }

  public Condition<Boolean> getUserStateBlockedRadioButtonValue() {
    return getUserStateBlockedRadioButton().value();
  }

  public Radio getUserStateActivatedRadioButton() {
    return userStateActivatedRadioButton;
  }

  public Condition<Boolean> getUserStateActivatedRadioButtonValue() {
    return getUserStateActivatedRadioButton().value();
  }

  public Radio getUserStateModerationRequiredRadioButton() {
    return userStateModerationRequiredRadioButton;
  }

  public Condition<Boolean> getUserStateModerationRequiredRadioButtonValue() {
    return getUserStateModerationRequiredRadioButton().value();
  }
}
