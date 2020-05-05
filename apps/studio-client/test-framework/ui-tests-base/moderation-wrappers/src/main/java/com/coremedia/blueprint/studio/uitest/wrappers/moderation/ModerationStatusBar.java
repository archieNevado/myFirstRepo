package com.coremedia.blueprint.studio.uitest.wrappers.moderation;

import com.coremedia.uitesting.ext3.wrappers.Container;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.form.field.StringDisplayField;
import com.coremedia.uitesting.webdriver.JsExpression;
import net.joala.condition.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

/**
 * @since 2013-02-20
 */
@ExtJSObject
@Scope("prototype")
public class ModerationStatusBar extends Container {
  private static final Logger LOG = LoggerFactory.getLogger(ModerationStatusBar.class);

  public static final String XTYPE = "com.coremedia.elastic.social.studio.config.moderationStatusBar";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(itemId = "cm-elastic-social-layout-statusbar-message", global = false)
  private StringDisplayField messageDisplayField;
  /*
  Examples for messages that might occur in the Status Bar:

  - In this session, you have approved 1 contribution. 0 remaining.
  - In this session, you have approved 0 contributions. 2 remaining.
  - In this session, you have approved 1 contribution. 1 remaining.
  - In this session, you have approved 0 contributions. More than 100 remaining.
   */
  private static final JsExpression MESSAGE_REGEX = new JsExpression("/^[^\\d]+(\\d+)[^\\d]+(\\d+).*$/");

  /**
   * Wrapper for the actual field whose value will contain the status bar text.
   *
   * @return wrapper for embedded display field
   */
  public StringDisplayField getMessageDisplayField() {
    return messageDisplayField;
  }

  /**
   * Get the number of contribution items moderated in this session.
   *
   * @return condition
   */
  public Condition<Long> sessionApprovedCount() {
    return messageDisplayField.longCondition("(function(){messageRegex.exec(self.getValue());return parseInt(RegExp.$1, 10);})()", "messageRegex", MESSAGE_REGEX);
  }

  /**
   * <p>
   * Condition for the remaining number of contributions to moderate.
   * </p>
   * <p>
   * <strong>Important:</strong> Current implementation will stop counting for 100 and more
   * contribution items. Thus using this condition will most likely result in fragile tests.
   * </p>
   *
   * @return condition
   */
  public Condition<Long> remainingCount() {
    LOG.warn("Using possibly fragile condition for remaining moderation count.");
    return messageDisplayField.longCondition("(function(){messageRegex.exec(self.getValue());return parseInt(RegExp.$2, 10);})()", "messageRegex", MESSAGE_REGEX);
  }
}
