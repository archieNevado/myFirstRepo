package com.coremedia.livecontext.studio.forms.facets {
import com.coremedia.ecommerce.studio.model.Facet;

import mx.resources.ResourceManager;

public class FacetUtil {

  /**
   * Localizes several values inside the search fields editor.
   * The localized label requires the preview 'Facet_Dropdown_'.
   * @param label the label to localize
   * @return the localized label or the original 'label' value
   */
  public static function localizeFacetLabel(label:String):String {
    var key:String = label.replace(' ', '').replace(/\./g, "_");
    var localizedLabel:String = ResourceManager.getInstance().getString('com.coremedia.livecontext.studio.LivecontextStudioPlugin', 'Facet_Dropdown_' + key);
    if(localizedLabel) {
      return localizedLabel;
    }
    return label;
  }

  public static function findFacetIdForValue(facets:Array, facetValue:String):String {
    for each (var f:Facet in facets) {
      for each(var value:Object in f.getValues()) {
        if (value.query === facetValue) {
          return f.getKey();
        }
      }
    }
    return null;
  }

  public static function validateFacetId4Facets(facets:Array, facetId:String):Boolean {
    if (!facetId || !facets) {
      return true;
    }

    for each (var f:Facet in facets) {
      if (f.getKey() === facetId) {
        return true;
      }
    }
    return false;
  }
}
}
