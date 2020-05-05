package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserToSearchSwitchPanel extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "userNameField", global = false)
  private StringDisplayField userNameField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "statusDisplayField", global = false)
  private StringDisplayField statusDisplayField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "switchToList", global = false)
  private Button switchToListButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "nextUser", global = false)
  private Button nextUserButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "prevUser", global = false)
  private Button prevUserButton;

  public StringDisplayField getUserNameField() {
    return userNameField;
  }

  public Button getSwitchToListButton() {
    return switchToListButton;
  }

  public Button getNextUserButton() {
    return nextUserButton;
  }

  public Button getPrevUserButton() {
    return prevUserButton;
  }

  public StringDisplayField getStatusDisplayField() {
    return statusDisplayField;
  }
}

