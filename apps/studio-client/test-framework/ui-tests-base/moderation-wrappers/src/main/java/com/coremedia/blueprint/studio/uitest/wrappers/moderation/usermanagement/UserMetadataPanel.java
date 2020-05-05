package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserMetadataPanel extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "numberOfContributions", global = false)
  private StringDisplayField numberOfContributionsField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "numberOfRejectedContributions", global = false)
  private StringDisplayField numberOfRejectedContributionsField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "lastLoginDate", global = false)
  private StringDisplayField lastLoginDateField;


  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "numberOfLogins", global = false)
  private StringDisplayField numberOfLoginsField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "localeLanguage", global = false)
  private StringDisplayField localeLanguageField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "userAnnotationContainer", global = false)
  private UserInternalNotesContainer userAnnotationContainer;

  public StringDisplayField getNumberOfContributionsField() {
    return numberOfContributionsField;
  }

  public Condition<String> getNumberOfContributionsFieldValue() {
    return numberOfContributionsField.value();
  }

  public StringDisplayField getNumberOfRejectedContributionsField() {
    return numberOfRejectedContributionsField;
  }

  public Condition<String> getNumberOfRejectedContributionsFieldValue() {
    return numberOfRejectedContributionsField.value();
  }

  public StringDisplayField getLastLoginDateField() {
    return lastLoginDateField;
  }

  public Condition<String> getLastLoginDateFieldValue() {
    return lastLoginDateField.value();
  }

  public StringDisplayField getNumberOfLoginsField() {
    return numberOfLoginsField;
  }

  public Condition<String> getNumberOfLoginsFieldValue() {
    return numberOfLoginsField.value();
  }

  public StringDisplayField getLocaleLanguageField() {
    return localeLanguageField;
  }

  public Condition<String> getLocaleLanguageFieldValue() {
    return localeLanguageField.value();
  }

public UserInternalNotesContainer getUserAnnotationContainer() {
    return userAnnotationContainer;
  }
}
