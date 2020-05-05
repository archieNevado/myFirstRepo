package com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems;

import com.coremedia.blueprint.studio.uitest.wrappers.moderation.labs.WebElementContainsExpression;
import com.coremedia.blueprint.studio.uitest.wrappers.moderation.model.impl.ModerationContributionAdministration;
import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.uitesting.ext3.wrappers.access.ExtJSObject;
import com.coremedia.uitesting.ext3.wrappers.access.FindByExtJS;
import com.coremedia.uitesting.ext3.wrappers.grid.GridPanel;
import com.coremedia.uitesting.webdriver.IdleIndicators;
import com.coremedia.uitesting.webdriver.conditions.WebElementConditions;
import net.joala.condition.BooleanCondition;
import net.joala.condition.Condition;
import net.joala.condition.ConditionFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;

@ExtJSObject
@Scope("prototype")
public class ModeratedItemsView extends GridPanel<ModeratedItemsRowSelectionModel> {

  public static final String FIELD_AUTHOR_NAME = "authorName";
  public static final String FIELD_DETAILS_TEXT = "subject";
  public static final String FIELD_TYPE = "collection";

  private static final String PRIORITIZABLE_CLASS_NAME = "cm-moderation-item-prioritizable";
  private static final String PRIORITIZABLE_ENABLED_CLASS_NAME = "cm-moderation-item-prioritizable--enabled";

  private static final String DATE_SORT_HEADER = "cm-elastic-social-list-date-sort-header";

  @Inject
  private IdleIndicators idleIndicators;
  @Inject
  private ConditionFactory conditionFactory;
  @Inject
  private WebElementConditions webElementConditions;
  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @FindByExtJS(expression = "self.moderationContributionAdministration", global = false)
  private ModerationContributionAdministration moderationContributionAdministration;

  public ModeratedItemsRowSelectionModel getSelectionModel() {
    return getSelectionModel(ModeratedItemsRowSelectionModel.class);
  }

  public ModerationContributionAdministration getModerationContributionAdministration() {
    return moderationContributionAdministration;
  }

  @Override
  public ModeratedItemsViewStore getStore() {
    return super.getStore().evalJsProxyProxy(ModeratedItemsViewStore.class);
  }

  @Override
  public ModeratedItemsViewColumnModel getColumnModel() {
    return super.getColumnModel().evalJsProxyProxy(ModeratedItemsViewColumnModel.class);
  }

  public void toggleSortByDate() {
    final WebElement webElement = clickableElement().await();
    webElementConditions.elementExists(webElement, By.id(DATE_SORT_HEADER)).waitUntilEquals(true);
    assert webElement != null : "Element for ModeratedItemsView must not be null.";
    webElement.findElement(By.id(DATE_SORT_HEADER)).click();
    idleIndicators.idle()
            .withMessage("Waiting for events to idle after toggle sorting of contribution items in moderation view.")
            .waitUntilTrue();
  }

  public void prioritize(final Model model) {
    final WebElement cellElement = prioritizableButtonOf(model).await();
    assert cellElement != null;
    cellElement.click();
    idleIndicators.idle()
            .withMessage("Waiting for events to idle after prioritising in moderation view.")
            .waitUntilTrue();
    prioritized(model).waitUntilTrue();
  }

  public Condition<WebElement> prioritizableButtonOf(final Model model) {
    return conditionFactory.condition(new ModeratedItemsViewCellElementExpression(this, model, 4, By.className(PRIORITIZABLE_CLASS_NAME)));
  }

  private Condition<WebElement> cellElementOf(final Model model) {
    return conditionFactory.condition(new ModeratedItemsViewCellElementExpression(this, model, 4, By.tagName("div")));
  }

  public BooleanCondition prioritized(final Model model) {
    return conditionFactory.booleanCondition(
            new WebElementContainsExpression(
                    cellElementOf(model),
                    By.className(PRIORITIZABLE_ENABLED_CLASS_NAME)
            )
    );
  }

}
