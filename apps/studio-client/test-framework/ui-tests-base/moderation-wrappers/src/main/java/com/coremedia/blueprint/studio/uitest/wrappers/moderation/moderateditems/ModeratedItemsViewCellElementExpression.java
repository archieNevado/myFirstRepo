package com.coremedia.blueprint.studio.uitest.wrappers.moderation.moderateditems;

import com.coremedia.elastic.core.api.models.Model;
import net.joala.expression.AbstractExpression;
import net.joala.expression.ExpressionEvaluationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static java.lang.String.format;

/**
 * <p>
 * Expression to locate a cell in the moderated items view list. Identified
 * by the containing model (user or comment) and the column id.
 * </p>
 *
 * @since 2013-02-25
 */
class ModeratedItemsViewCellElementExpression extends AbstractExpression<WebElement> {

  private final ModeratedItemsView view;
  private final int column;
  private final Model model;
  private final By by;

  ModeratedItemsViewCellElementExpression(final ModeratedItemsView view, final Model model, final int column, final By by) {
    this.view = view;
    this.column = column;
    this.model = model;
    this.by = by;
  }

  @Override
  public WebElement get() {
    final Long row = view.getStore().position(model).get();
    if (row == null || row < 0) {
      throw new ExpressionEvaluationException(format("Cannot find model in store: %s", model));
    }
    return view.getView().cellElement(row, column).get().findElement(by);
  }
}
