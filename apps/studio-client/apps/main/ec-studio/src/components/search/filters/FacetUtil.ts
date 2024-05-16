import Facet from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Facet";
import ECommerceStudioPlugin_properties from "../../../ECommerceStudioPlugin_properties";

class FacetUtil {

  /**
   * Localizes several values inside the search fields editor.
   * The localized label requires the preview 'Facet_Dropdown_'.
   * @param label the label to localize
   * @return the localized label or the original 'label' value
   */
  static localizeFacetLabel(label: string): string {
    const key = label.replace(" ", "").replace(/\./g, "_");
    const localizedLabel = ECommerceStudioPlugin_properties["Facet_Dropdown_" + key];
    if (localizedLabel) {
      return localizedLabel;
    }
    return label;
  }

  static findFacetForKey(facets: Array<any>, key: string): Facet {
    for (const f of facets as Facet[]) {
      if (f.getKey() === key) {
        return f;
      }
    }
    return null;
  }

  static findFacetIdForQuery(facets: Array<any>, facetValue: string): string {
    for (const f of facets as Facet[]) {
      for (const value of f.getValues()) {
        if (value.query === facetValue) {
          return f.getKey();
        }
      }
    }
    return null;
  }

  static validateFacetValue(facet: Facet, facetValue: string): boolean {
    for (const value of facet.getValues()) {
      if (value.query === facetValue) {
        return true;
      }
    }
    return false;
  }

  static validateFacetId4Facets(facets: Array<any>, facetId: string): boolean {
    if (!facetId || !facets) {
      return true;
    }

    for (const f of facets as Facet[]) {
      if (f.getKey() === facetId) {
        return true;
      }
    }
    return false;
  }
}

export default FacetUtil;
