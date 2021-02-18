package com.coremedia.blueprint.cae.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@ConfigurationProperties(prefix = "contentbeans")
@PropertySource("classpath:/META-INF/deprecated-delivery.properties")
public class BlueprintPageCaeContentBeansConfigurationProperties {
  /**
   * If set to true, JavaScript and CSS references of a Page are squashed into
   * one common link.
   */
  private boolean mergeCodeResources = false;

  public boolean isMergeCodeResources() {
    return mergeCodeResources;
  }

  public void setMergeCodeResources(boolean mergeCodeResources) {
    this.mergeCodeResources = mergeCodeResources;
  }
}
