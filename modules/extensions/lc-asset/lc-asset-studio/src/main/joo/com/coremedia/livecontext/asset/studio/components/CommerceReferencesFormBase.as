package com.coremedia.livecontext.asset.studio.components {
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.asset.studio.config.commerceReferencesForm;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class CommerceReferencesFormBase extends CollapsibleFormPanel{
  public function CommerceReferencesFormBase(config:commerceReferencesForm = null) {
    super(config);
  }

  internal function getShopExpression(config:commerceReferencesForm):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      var store:Store = Store(CatalogHelper.getInstance().getStoreForContentExpression(config.bindTo).getValue());
      return store && store.getName() && !CatalogHelper.getInstance().isCoreMediaStore(store);
    });
  }


}
}