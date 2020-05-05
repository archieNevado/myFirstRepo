package com.coremedia.blueprint.studio.uitest.wrappers.moderation.model.impl;

import com.coremedia.uitesting.webdriver.JsProxy;
import net.joala.condition.BooleanCondition;
import net.joala.condition.Condition;

/**
 * @since 2013-02-21
 */
public abstract class AbstractContributionAdministration extends JsProxy {
  public static final String SORT_ASCENDING = "ascending";
  public static final String SORT_DESCENDING = "descending";

  public Condition<String> sorting() {
    return stringCondition("self.getSorting()");
  }

  public BooleanCondition sortingIs(final String sortOrder) {
    return booleanCondition("self.getSorting() == sortOrder", "sortOrder", sortOrder);
  }

  public BooleanCondition sortingIsAscending() {
    return sortingIs(SORT_ASCENDING);
  }

  public BooleanCondition sortingIsDescending() {
    return sortingIs(SORT_DESCENDING);
  }
}
