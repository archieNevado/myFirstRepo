package com.coremedia.blueprint.studio.styleguide.tabs.icons {
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.BeanFactoryImpl;

public class InformationAreaBase extends SwitchingContainer {

  private var activeVE:ValueExpression;

  public function InformationAreaBase(config:InformationAreaBase = null) {
    super(config);
  }

  protected function getActiveVE():ValueExpression {
    if (!activeVE) {
      BeanFactoryImpl.initBeanFactory();
      activeVE = ValueExpressionFactory.createFromValue(InformationArea.START_INFORMATION_ITEM_ID);
    }
    return activeVE;
  }
}
}
