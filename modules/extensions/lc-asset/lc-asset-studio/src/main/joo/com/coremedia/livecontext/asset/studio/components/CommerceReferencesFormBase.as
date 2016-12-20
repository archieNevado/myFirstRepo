package com.coremedia.livecontext.asset.studio.components {
import com.coremedia.cms.editor.sdk.premular.PropertyFieldGroup;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class CommerceReferencesFormBase extends PropertyFieldGroup{
  public function CommerceReferencesFormBase(config:CommerceReferencesForm = null) {
    super(config);
  }

  internal function getShopExpression(config:CommerceReferencesForm):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      var store:Store = Store(CatalogHelper.getInstance().getStoreForContentExpression(config.bindTo).getValue());
      return store && store.getName() && !CatalogHelper.getInstance().isCoreMediaStore(store);
    });
  }


}
}