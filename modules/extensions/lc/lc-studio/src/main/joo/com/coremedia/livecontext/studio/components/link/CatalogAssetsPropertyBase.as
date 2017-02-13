package com.coremedia.livecontext.studio.components.link {
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class CatalogAssetsPropertyBase extends SwitchingContainer {

  protected static const CATALOG_ASSET_PROPERTY_ITEM_ID:String = 'catalogAssets';
  protected static const CATALOG_EMPTY_LABEL_ITEM_ID:String = 'emptyLabelText';

  private var selectedExpression:ValueExpression;
  public function CatalogAssetsPropertyBase(config:CatalogAssetsProperty = null) {
    super(config);
  }

  internal function getActiveCatalogAssetPropertyValueExpression(config:CatalogAssetsProperty):ValueExpression {
    return ValueExpressionFactory.createFromFunction(getActiveCatalogAssetProperty, config);
  }

  private function getActiveCatalogAssetProperty(config:CatalogAssetsProperty):String {
    var valueExpression:ValueExpression = config.bindTo.extendBy(config.propertyName);
    var values:Array = valueExpression.getValue();
    if (values && values.length != 0) {
      return CATALOG_ASSET_PROPERTY_ITEM_ID;
    }
    return CATALOG_EMPTY_LABEL_ITEM_ID;
  }

  protected function getSelectedExpression():ValueExpression {
    if (!selectedExpression) {
      selectedExpression = ValueExpressionFactory.createFromValue([]);
    }
    return selectedExpression;
  }

}
}