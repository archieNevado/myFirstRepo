package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserRestrictionPanel extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "userStates", global = false)
  private UserStatesPanel userStatesPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "resendRegistration", global = false)
  private Button resendRegistrationButton;

  public UserStatesPanel getUserStatesPanel() {
    return userStatesPanel;
  }

  public Button getResendRegistrationButton() {
    return resendRegistrationButton;
  }
}
