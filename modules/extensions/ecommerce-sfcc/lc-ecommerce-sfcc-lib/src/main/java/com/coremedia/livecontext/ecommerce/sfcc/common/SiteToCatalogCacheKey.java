package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CatalogDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.SiteDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CatalogsResource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class SiteToCatalogCacheKey extends CacheKey<Map<String, String>> {

  private final String id;
  private final CatalogsResource catalogsResource;
  private final int cacheForSeconds;

  SiteToCatalogCacheKey(String id, CatalogsResource catalogsResource, int cacheForSeconds) {
    this.id = id;
    this.cacheForSeconds = cacheForSeconds;
    this.catalogsResource = catalogsResource;
  }

  @Override
  public Map<String, String> evaluate(Cache cache) {
    Cache.cacheFor(cacheForSeconds, TimeUnit.SECONDS);
    Cache.disableDependencies();

    try {
      Map<String, String> siteToCatalogMap = new HashMap<>();
      List<CatalogDocument> catalogDocuments = catalogsResource.getCatalogs();
      for (CatalogDocument catalogDocument : catalogDocuments) {
        String catalogId = catalogDocument.getId();
        List<SiteDocument> assignedSites = catalogDocument.getAssignedSites();
        if (assignedSites != null) {
          for (SiteDocument siteDocument : assignedSites) {
            siteToCatalogMap.put(siteDocument.getId(), catalogId);
          }
        }
      }

      return siteToCatalogMap;
    } finally {
      Cache.enableDependencies();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return id.equals(((SiteToCatalogCacheKey) o).id);
  }

  @Override
  public int hashCode() {
    return 31 * id.hashCode() + cacheForSeconds;
  }
}
