package com.coremedia.blueprint.studio.uitest.wrappers.moderation.labs;

import net.joala.condition.Condition;
import net.joala.expression.AbstractExpression;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * <p>
 *   Expression to determine if one web-element contains another as specified by
 *   the locator.
 * </p>
 * @since 2013-02-25
 */
public class WebElementContainsExpression extends AbstractExpression<Boolean> {
  private final Condition<WebElement> elementCondition;
  private final By locator;

  // Developer note: Constructors for already found WebElements or WebElement-Expressions could be
  // added.

  public WebElementContainsExpression(final Condition<WebElement> elementCondition, final By locator) {
    this.elementCondition = elementCondition;
    this.locator = locator;
  }

  @Override
  public Boolean get() {
    final WebElement element = elementCondition.get();
    try {
      element.findElement(locator);
    } catch (NoSuchElementException ignored) {
      return false;
    }
    return true;
  }
}
