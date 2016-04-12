package com.coremedia.ecommerce.studio.components.link {
import com.coremedia.cap.struct.Struct;
import com.coremedia.ecommerce.studio.config.categoryAndProductLinksPropertyField;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;

public class CategoryAndProductLinksPropertyFieldBase extends CatalogLinkPropertyField{

  private static const PROPERTIES:String = 'properties';
  private static const LOCAL_SETTINGS_STRUCT_NAME:String = 'localSettings';
  private static const COMMERCE_STRUCT_NAME:String = 'commerce';
  private static const PRODUCTS_LIST_NAME:String = CatalogHelper.REFERENCES_LIST_NAME;
  public static const PROPERTY_NAME:String = LOCAL_SETTINGS_STRUCT_NAME + '.' + COMMERCE_STRUCT_NAME + '.' + PRODUCTS_LIST_NAME;

  private var bindTo:ValueExpression;

  public function CategoryAndProductLinksPropertyFieldBase(config:categoryAndProductLinksPropertyField = null) {
    super(config);
    bindTo = config.bindTo;
  }

  internal function createStructs():void {
    var localSettingsStructExpression:ValueExpression = bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME);
    localSettingsStructExpression.loadValue(function():void {
      var localSettingsStruct:Struct = localSettingsStructExpression.getValue();
      RemoteBean(localSettingsStruct).load(function():void {
        if (!localSettingsStruct.get(COMMERCE_STRUCT_NAME)) {
          localSettingsStruct.getType().addStructProperty(COMMERCE_STRUCT_NAME);
        }

        var commerceStruct:Struct = localSettingsStruct.get(COMMERCE_STRUCT_NAME);
        var productsStruct:Struct = commerceStruct.get(PRODUCTS_LIST_NAME);
        if (!productsStruct) {
          commerceStruct.getType().addStringListProperty(PRODUCTS_LIST_NAME, 1000000);
        }
      });
    });
  }

}
}