package com.coremedia.ecommerce.studio.model {
[RestResource(uriTemplate="livecontext/marketing/{siteId:[^/]+}/{workspaceId:[^/]+}")]
public class MarketingImpl extends CatalogObjectImpl implements Marketing {
  public function MarketingImpl(uri:String) {
    super(uri);
  }


  public function getChildrenData():Array {
    return get(CatalogObjectPropertyNames.CHILDREN_DATA);
  }

  public function getMarketingSpots():Array {
    return get(CatalogObjectPropertyNames.MARKETING_SPOTS);
  }
}
}
