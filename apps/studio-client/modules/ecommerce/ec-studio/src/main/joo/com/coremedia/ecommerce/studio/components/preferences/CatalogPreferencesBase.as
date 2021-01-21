package com.coremedia.ecommerce.studio.components.preferences {
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.util.PreferencesUtil;
import com.coremedia.cms.studio.frame.components.preferences.PreferencePanel;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.panel.Panel;

public class CatalogPreferencesBase extends Panel implements PreferencePanel {
  public static var PREFERENCE_SHOW_CATALOG_KEY:String = "showCatalogContent";
  public static var SORT_CATEGORIES_BY_NAME_KEY:String = "sortCategoriesByName";

  internal var showCatalogValueExpression:ValueExpression;
  internal var sortCategoriesByNameExpression:ValueExpression;

  public function CatalogPreferencesBase(config:CatalogPreferences = null) {
    super(config);
  }

  protected function getShowCatalogValueExpression():ValueExpression {
    if(!showCatalogValueExpression) {
      var enabled:Boolean = ValueExpressionFactory.create(PREFERENCE_SHOW_CATALOG_KEY, editorContext.getPreferences()).getValue();
      showCatalogValueExpression = ValueExpressionFactory.createFromValue(enabled);
    }
    return showCatalogValueExpression;
  }

  protected function getSortCategoriesByNameExpression():ValueExpression {
    if(!sortCategoriesByNameExpression) {
      var enabled:String = ValueExpressionFactory.create(SORT_CATEGORIES_BY_NAME_KEY, editorContext.getPreferences()).getValue();
      sortCategoriesByNameExpression = ValueExpressionFactory.createFromValue(enabled);
    }
    return sortCategoriesByNameExpression;
  }

  public function updatePreferences():void {
    var showCatalogValue:Boolean = getShowCatalogValueExpression().getValue();
    var sortCategoriesByNameValue:Boolean = getSortCategoriesByNameExpression().getValue();
    PreferencesUtil.updatePreferencesJSONProperty(showCatalogValue, PREFERENCE_SHOW_CATALOG_KEY);
    PreferencesUtil.updatePreferencesJSONProperty(sortCategoriesByNameValue, SORT_CATEGORIES_BY_NAME_KEY);
  }
}
}
