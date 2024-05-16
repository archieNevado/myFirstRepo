package com.coremedia.livecontext.p13n.include;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.DynamicContainerStrategy;
import com.coremedia.blueprint.personalization.include.AbstractP13nContainerPredicate;

import javax.annotation.PostConstruct;

public class P13NNavigationPredicate extends AbstractP13nContainerPredicate {

  private static final String NAVIGATION_VIEW = "navigation";

  private DynamicContainerStrategy dynamicContainerStrategy;

  public void setDynamicContainerStrategy(DynamicContainerStrategy dynamicContainerStrategy) {
    this.dynamicContainerStrategy = dynamicContainerStrategy;
  }

  @PostConstruct
  void initialize() {
    if (dynamicContainerStrategy == null) {
      throw new IllegalStateException("Required property not set: dynamicContainerStrategy");
    }
  }

  @Override
  protected boolean isViewMatching(String view) {
    return view != null && view.equals(NAVIGATION_VIEW);
  }

  @Override
  protected boolean isBeanMatching(Object bean) {
    if (!(bean instanceof Page)) {
      return false;
    }
    Page page = (Page) bean;
    CMNavigation cmNavigation = page.getNavigation().getRootNavigation();
    return dynamicContainerStrategy.isEnabled(cmNavigation)
            && dynamicContainerStrategy.isDynamic(cmNavigation.getChildren());
  }
}
