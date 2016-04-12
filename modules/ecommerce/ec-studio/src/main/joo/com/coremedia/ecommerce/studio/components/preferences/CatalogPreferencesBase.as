package com.coremedia.ecommerce.studio.components.preferences {
import com.coremedia.blueprint.studio.config.taxonomy.taxonomyPreferences;
import com.coremedia.cms.editor.sdk.preferences.PreferencePanel;
import com.coremedia.cms.editor.sdk.util.PreferencesUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Panel;

public class CatalogPreferencesBase extends Panel implements PreferencePanel {
  public static var PREFERENCE_SHOW_CATALOG_KEY:String = "showCatalogContent";

  internal var showCatalogValueExpression:ValueExpression;

  public function CatalogPreferencesBase(config:taxonomyPreferences = null) {
    super(config);
  }

  protected function getShowCatalogValueExpression():ValueExpression {
    if(!showCatalogValueExpression) {
      var value:Boolean = PreferencesUtil.getPreferencesProperty(PREFERENCE_SHOW_CATALOG_KEY);
      if(value === undefined) {
        value = false;
      }
      showCatalogValueExpression = ValueExpressionFactory.createFromValue(value);
    }
    return showCatalogValueExpression;
  }

  public function updatePreferences():void {
    var value:String = getShowCatalogValueExpression().getValue();
    PreferencesUtil.updatePreferencesJSONProperty(value, PREFERENCE_SHOW_CATALOG_KEY);
  }
}
}
