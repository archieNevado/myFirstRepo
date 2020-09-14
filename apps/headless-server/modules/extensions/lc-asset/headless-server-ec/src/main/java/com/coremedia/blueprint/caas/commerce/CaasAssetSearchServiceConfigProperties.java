package com.coremedia.blueprint.caas.commerce;

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

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

}
