package com.coremedia.blueprint.studio.uitest.base.wrappers.topicpageseditor;

import com.coremedia.cap.common.CapObject;
import com.coremedia.cap.content.Content;
import com.coremedia.uitesting.ext3.wrappers.MessageBox;
import com.coremedia.uitesting.ext3.wrappers.Panel;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.webdriver.CoreMediaWebDriverProvider;
import com.coremedia.uitesting.webdriver.conditions.WebElementConditions;
import net.joala.condition.Condition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.coremedia.cap.common.IdHelper.parseContentId;
import static java.lang.String.format;

/**
 * Wrapper for the filter and the grid.
 */
@ExtJSObject(id = "topicsPanel")
@Singleton
public class TopicPanel extends Panel {
  public static final String XTYPE = "com.coremedia.blueprint.studio.topicpages.config.topicsPanel";

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "topicPagesFilterPanel")
  private TopicPagesFilterPanel topicPagesFilterPanel;

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(id = "topicPagesGrid")
  private TopicsGridPanel grid;

  @Inject
  private CoreMediaWebDriverProvider driverProvider;

  @Inject
  private WebElementConditions webElementConditions;

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private MessageBox messageBox;

  public TopicsGridPanel getGrid() {
    return grid;
  }

  public TopicPagesFilterPanel getTopicPagesFilterPanel() {
    return topicPagesFilterPanel;
  }

  private static final String CREATE_BUTTON_ID_PATTERN = "topicpage-create-%d";
  private static final String DELETE_BUTTON_ID_PATTERN = "topicpage-delete-%d";

  public void clickOnCreate(final Content content) {
    createButton(content).await().click();
    webElementConditions.elementExists(driverProvider.get(), actionButtonLocator(content, CREATE_BUTTON_ID_PATTERN))
                        .withMessage(format("Create button for taxonomy %s should disappear after topic page creation.", content.getName()))
                        .waitUntilEquals(false);
  }

  private Condition<WebElement> createButton(final CapObject content) {
    return actionButton(content, CREATE_BUTTON_ID_PATTERN);
  }

  private Condition<WebElement> deleteButton(final CapObject content) {
    return actionButton(content, DELETE_BUTTON_ID_PATTERN);
  }

  private Condition<WebElement> actionButton(final CapObject content, final String pattern) {
    return webElementConditions.element(driverProvider.get(), actionButtonLocator(content, pattern));
  }

  private By actionButtonLocator(final CapObject content, final String pattern) {
    return By.id(format(pattern, parseContentId(content.getId())));
  }

  public void clickOnDelete(final Content content) {
    deleteButton(content).await().click();
    messageBox.visible().waitUntilTrue();
    messageBox.getBottomToolbar().getOkButton().click();
    webElementConditions.elementExists(driverProvider.get(), actionButtonLocator(content, DELETE_BUTTON_ID_PATTERN))
                        .withMessage(format("Delete button for taxonomy %s should disappear after topic page deletion.", content.getName()))
                        .waitUntilEquals(false);
  }
}
