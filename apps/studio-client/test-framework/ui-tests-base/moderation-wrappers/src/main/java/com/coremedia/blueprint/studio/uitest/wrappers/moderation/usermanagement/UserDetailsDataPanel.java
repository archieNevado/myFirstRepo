package com.coremedia.blueprint.studio.uitest.wrappers.moderation.usermanagement;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import com.coremedia.uitesting.ui.components.ImageComponent;
import net.joala.condition.Condition;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class UserDetailsDataPanel extends Container {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-user-detail-remove-image-button", global = false)
  private Button userDetailsDeleteImageButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "image", global = false)
  private ImageComponent userDetailsImage;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "no-image", global = false)
  private ImageComponent defaultImage;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-userdetails-panel-name", global = false)
  private TextField userDetailsNameField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-userdetails-panel-givenname", global = false)
  private TextField userDetailsGivenNameField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-userdetails-panel-surname", global = false)
  private TextField userDetailsSurNameField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-userdetails-panel-email", global = false)
  private TextField userDetailsEmailField;

  public Button getUserDetailsDeleteImageButton() {
    return userDetailsDeleteImageButton;
  }

  public ImageComponent getUserDetailsImage() {
    return userDetailsImage;
  }

  public ImageComponent getDefaultImage() {
    return defaultImage;
  }

  public TextField getUserDetailsNameField() {
    return userDetailsNameField;
  }

  public Condition<String> getUserDetailsNameFieldValue() {
    return userDetailsNameField.value();
  }

  public TextField getUserDetailsGivenNameField() {
    return userDetailsGivenNameField;
  }

  public Condition<String> getUserDetailsGivenNameFieldValue() {
    return userDetailsGivenNameField.value();
  }

  public TextField getUserDetailsSurNameField() {
    return userDetailsSurNameField;
  }

  public Condition<String> getUserDetailsSurNameFieldValue() {
    return userDetailsSurNameField.value();
  }

  public TextField getUserDetailsEmailField() {
    return userDetailsEmailField;
  }

  public Condition<String> getUserDetailsEmailFieldValue() {
    return userDetailsEmailField.value();
  }
}
