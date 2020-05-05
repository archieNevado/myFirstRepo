package com.coremedia.blueprint.studio.uitest.wrappers.moderation.filter;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.uitesting.doctypes.EditorPreferences;
import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSBy;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.button.Button;
import com.coremedia.uitesting.ext3.wrappers.form.Checkbox;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.uapi.helper.UserUtils;
import net.joala.condition.ConditionFactory;
import net.joala.expression.AbstractExpression;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject
@Scope("prototype")
public class ContributionSearchFilterPanel extends Panel {

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  public static final String XTYPE = "com.coremedia.elastic.social.studio.config.contributionSearchFilterPanel";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "categorySelectorItemId", global = false)
  private ComboBoxField categorySelector;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "usersFilterItemId", global = false)
  private Checkbox includeUsersCheckbox;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "commentsFilterItemId", global = false)
  private Checkbox includeCommentsCheckbox;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "categoryFilterCtItemId", global = false)
  private Container categoryFilterContainer;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR - Suppress Avoid Duplicate Literals
  @FindByExtJS(itemId = "categoryElementsCtItemId", global = false)
  private Container categoryElementsContainer;

  @Inject
  private UserUtils userUtils;

  @Inject
  private ConditionFactory conditionFactory;

  public Container getCategoryFilterContainer() {
    return categoryFilterContainer;
  }

  public ComboBoxField getCategorySelector() {
    return categorySelector;
  }

  public Checkbox getIncludeUsersCheckbox() {
    return includeUsersCheckbox;
  }

  public Checkbox getIncludeCommentsCheckbox() {
    return includeCommentsCheckbox;
  }

  public void selectCommentCategory(String category) {
    categorySelector.select(category);
  }

  public void unselectCommentCategory(String category) {
    Button removeButton = categoryElementsContainer.find(Button.class, ExtJSBy.itemId(category));
    removeButton.click();
  }

  public void includeUsers(boolean include) {
    includeUsersCheckbox.setValue(include);
    includeUsersCheckbox.value().assertEquals(include);
    conditionFactory.booleanCondition(new AbstractExpression<Boolean>() {
      @Override
      public Boolean get() {
        return (Boolean) getModerationStruct().get("includeUsers");
      }
    }).assertEquals(include);
  }

  private Struct getModerationStruct() {
    Content preferences = userUtils.getPreferences();
    Struct settings = (Struct) preferences.get(EditorPreferences.P_DATA);
    return (Struct) settings.get("moderation");
  }

  public void includeComments(boolean include) {
    includeCommentsCheckbox.setValue(include);
    includeCommentsCheckbox.value().assertEquals(include);
    conditionFactory.booleanCondition(new AbstractExpression<Boolean>() {
      @Override
      public Boolean get() {
        return (Boolean) getModerationStruct().get("includeComments");
      }
    }).assertEquals(include);
  }
}
