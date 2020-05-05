package com.coremedia.blueprint.studio.uitest.base.wrappers.components;

import com.coremedia.cap.content.Content;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.form.field.ComboBoxField;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import net.joala.condition.BooleanCondition;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

/**
 * @since 6/8/12
 */
@ExtJSObject
@Scope("prototype")
public class FolderCombo extends ComboBoxField {
  public static final String XTYPE = "com.coremedia.blueprint.studio.config.components.folderCombo";

  @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
  @Inject
  private IdleIndicators idleIndicators;

  public void setContent(Content content) {
    idleIndicators.idle().waitUntilTrue();
    selectText();
    idleIndicators.idle().waitUntilTrue();
    writeString(content.getPath());
    blur();
    idleIndicators.idle().waitUntilTrue();
    value().waitUntilEquals(content.getPath());
    idleIndicators.idle().waitUntilTrue();
  }

  public BooleanCondition containsValue(final String value) {
    return booleanCondition("self.getAvailablePathsExpression().getValue().indexOf(value) >= 0", "value", value);
  }
}
