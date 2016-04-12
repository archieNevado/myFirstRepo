package com.coremedia.blueprint.studio.forms.containers {
import com.coremedia.blueprint.studio.config.pictureDocumentForm;
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.cms.editor.sdk.util.LinkListUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class PictureDocumentFormBase extends CollapsibleFormPanel {
  private var gridEmptyValueExpression:ValueExpression;

  public function PictureDocumentFormBase(config:pictureDocumentForm = null) {
    super(config);
  }

  public function getGridEmptyValueExpression(config:pictureDocumentForm):ValueExpression {
    if (!gridEmptyValueExpression) {
      gridEmptyValueExpression = ValueExpressionFactory.createFromFunction(
              function ():Boolean {
                return LinkListUtil.getFreeCapacity(
                                config.bindTo,
                                config.picturePropertyName,
                                config.maxCardinality) < 1;
              }
      )
    }
    return gridEmptyValueExpression;
  }
}
}
