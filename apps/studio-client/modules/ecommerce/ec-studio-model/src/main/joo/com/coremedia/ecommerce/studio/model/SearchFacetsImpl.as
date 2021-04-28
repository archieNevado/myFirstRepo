package com.coremedia.ecommerce.studio.model {
[RestResource(uriTemplate="livecontext/searchfacets/{siteId:[^/]+}/{catalogAlias:[^/]+}/{workspaceId:[^/]+}/{categoryId:.+}")]
public class SearchFacetsImpl extends CatalogObjectImpl implements Facets {
  private var facets:Array;

  public function SearchFacetsImpl(uri:String) {
    super(uri);
  }

  public function getFacets():* {
    if (!facets) {
      var facetObjects:Array = get(CatalogObjectPropertyNames.FACETS);
      if (facetObjects === undefined) {
        return undefined;
      }

      facets = [];
      for each (var facet:Object in facetObjects) {
        facets.push(new Facet(facet));
      }
    }
    return facets;
  }
}
}
