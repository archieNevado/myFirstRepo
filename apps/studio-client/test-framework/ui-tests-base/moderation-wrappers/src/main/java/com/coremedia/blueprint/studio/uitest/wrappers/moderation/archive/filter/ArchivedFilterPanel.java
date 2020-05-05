package com.coremedia.blueprint.studio.uitest.wrappers.moderation.archive.filter;

import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.ext3.wrappers.form.field.DateField;
import com.coremedia.uitesting.ext3.wrappers.form.field.TextField;
import org.springframework.context.annotation.Scope;

@ExtJSObject
@Scope("prototype")
public class ArchivedFilterPanel extends Panel {
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "search-type-combo", global = false)
  private ComboBoxField typeCombo;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "search-field", global = false)
  private TextField searchTermTextField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "magnifier", global = false)
  private Button submitSearchButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "state-combo", global = false)
  private ComboBoxField stateCombo;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "author-textfield", global = false)
  private TextField authorTextField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "from-datefield", global = false)
  private DateField fromDateField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "reset-from-datefield", global = false)
  private Button resetFromDateButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "to-datefield", global = false)
  private DateField toDateField;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "reset-to-datefield", global = false)
  private Button resetToDateButton;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "reset-filter", global = false)
  private Button resetFilterButton;

  public ComboBoxField getTypeCombo() {
    return typeCombo;
  }

  public TextField getSearchTermTextField() {
    return searchTermTextField;
  }

  public Button getSubmitSearchButton() {
    return submitSearchButton;
  }

  public ComboBoxField getStateCombo() {
    return stateCombo;
  }

  public TextField getAuthorTextField() {
    return authorTextField;
  }

  public Button getResetFilterButton() {
    return resetFilterButton;
  }

  public DateField getFromDateField() {
    return fromDateField;
  }

  public DateField getToDateField() {
    return toDateField;
  }

  public Button getResetFromDateButton() {
    return resetFromDateButton;
  }

  public Button getResetToDateButton() {
    return resetToDateButton;
  }
}
