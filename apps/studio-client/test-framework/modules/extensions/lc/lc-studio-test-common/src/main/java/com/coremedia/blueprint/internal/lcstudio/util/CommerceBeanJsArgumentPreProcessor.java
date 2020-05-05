package com.coremedia.blueprint.internal.lcstudio.util;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.uitesting.webdriver.JsArgumentPreProcessorBase;
import com.coremedia.uitesting.webdriver.JsExpression;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * An argument pre-processor that wraps commerce beans as remote bean expressions.
 */
@Named
@Singleton
public class CommerceBeanJsArgumentPreProcessor extends JsArgumentPreProcessorBase<CommerceBean> {
  @Inject
  private JsCommerceBeanFactory jsCommerceBeanFactory;

  @Override
  public JsExpression process(final CommerceBean obj) {
    return jsCommerceBeanFactory.get(obj).getExpression();
  }
}
