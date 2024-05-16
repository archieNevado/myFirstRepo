package com.coremedia.ecommerce.studio.rest.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("studio.rest.invalidation-source.commerce")
public class StudioCommerceCacheConfigurationProperties {

  /**
   * The capacity of the commerce cache invalidation source
   */
  private int capacity = 10_000;

  public int getCapacity() {
    return capacity;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }
}
