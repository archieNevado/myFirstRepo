package com.coremedia.ecommerce.studio.helper {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtil;
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ecommerce.studio.augmentation.augmentationService;
import com.coremedia.ecommerce.studio.catalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import mx.resources.ResourceManager;

[ResourceBundle('com.coremedia.ecommerce.studio.ECommerceStudioPlugin')]
public class AugmentationUtil {
  /**
   * Checks if a given category has child categories.
   * @param bindTo The category as content
   * @return
   */
  public static function hasChildCategoriesExpression(bindTo:ValueExpression):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      if (bindTo.getValue() is Content) {
        var childrenExpression:ValueExpression = bindTo.extendBy("properties").extendBy("children");
        var children:Array = childrenExpression.getValue();
        return children && children.length > 0;
      }
      return false;
    });
  }

  /**
   * Converts the given content to a catalog object.
   * @param bindTo
   * @return
   */
  public static function toCatalogObjectExpression(bindTo:ValueExpression):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Object {
      var content:Content = bindTo.getValue() as Content;
      if (content) {
        return augmentationService.getCategory(content);
      }
      return bindTo.getValue();
    });
  }

  /**
   *
   * @param contentExpression expression pointing to the content augmenting a catalog object
   * @return expression pointing to the catalog object
   */
  public static function getCatalogObjectExpression(contentExpression:ValueExpression):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():RemoteBean {
      var content:Content = contentExpression.getValue();
      if (content === undefined) {
        return undefined;
      }
      return augmentationService.getCatalogObject(content);
    });
  }

  public static function getTypeLabel(catalogObject:CatalogObject):String {
    // if the catalog object is an augmented category
    // take the type label of the augmenting content
    var categoryContent:Content = augmentationService.getContent(catalogObject);
    if (categoryContent && categoryContent.getType() && categoryContent.getType().getName()) {
      return ContentLocalizationUtil.localizeDocumentTypeName(categoryContent.getType().getName());
    }
    var catalogType:String = catalogHelper.getType(catalogObject);
    return ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', catalogType + '_label');
  }

  public static function getTypeCls(catalogObject:CatalogObject):String {
    var catalogType:String = catalogHelper.getType(catalogObject);
    //if a catalog object is augmented show a different icon
    if (augmentationService.getContent(catalogObject)) {
      if (catalogType === CatalogModel.TYPE_CATEGORY) {
        return ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'AugmentedCategory_icon');
      } else if (catalogType === CatalogModel.TYPE_PRODUCT) {
        return ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', 'AugmentedProduct_icon');
      }
    }
    return ResourceManager.getInstance().getString('com.coremedia.ecommerce.studio.ECommerceStudioPlugin', catalogType + '_icon');
  }
}
}