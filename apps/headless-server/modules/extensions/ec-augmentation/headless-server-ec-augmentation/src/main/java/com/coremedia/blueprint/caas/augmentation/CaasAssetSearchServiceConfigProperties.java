package com.coremedia.blueprint.caas.augmentation;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "caas.commerce.assetsearchservice")
@DefaultAnnotation(NonNull.class)
public class CaasAssetSearchServiceConfigProperties {

  /**
   * Result limit for searches by the underlying asset search service.
   * Defaults to -1 = unlimited.
   */
  private int limit = -1;

  /**
   * Time in seconds to cache search query results of the CaasAssetSearchService of lc-asset extension.
   * Defaults to 300 seconds (5 minutes)
   * Set it to "-1" to deactivate the search-query cache.
   */
  private int cacheSeconds = 500;

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getCacheSeconds() {
    return cacheSeconds;
  }

  public void setCacheSeconds(int cacheSeconds) {
    this.cacheSeconds = cacheSeconds;
  }

}
