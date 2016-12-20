package com.coremedia.livecontext.studio.pbe {
import com.coremedia.cms.editor.sdk.preview.PreviewIFrameToolbar;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.ValueHolder;
import com.coremedia.ui.plugins.AddItemsPlugin;

import ext.Component;

public class AddFragmentHighlightButtonBase extends AddItemsPlugin implements ValueHolder{

  private var toolbar:PreviewIFrameToolbar;

  public function AddFragmentHighlightButtonBase(config:AddFragmentHighlightButtonPlugin = null) {
    super(config);
  }

  override public function init(component:Component):void {
    super.init(component);
    var toolbar:PreviewIFrameToolbar = component as PreviewIFrameToolbar;
    setValue(toolbar);
  }

  protected function getSeperatorVisibilityVE():ValueExpression {
    return ValueExpressionFactory.createFromValueHolder(this);
  }

  public function getValue():* {
    return toolbar;
  }

  public function setValue(value:*):Boolean {
    if (toolbar !== value) {
      toolbar = value;
      if (toolbar.itemCollection.getCount() > 1) {
        return true;
      }
    }
    return false;
  }

}
}
