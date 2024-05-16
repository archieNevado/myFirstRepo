package com.coremedia.blueprint.cae.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cae")
public class BlueprintPageCaeContentBeansConfigurationProperties {
  /**
   * If set to true, JavaScript and CSS references of a Page are squashed into
   * one common link.
   */
  @Value("${contentbeans.merge-code-resources:false}")
  private boolean mergeCodeResources = false;

  public boolean isMergeCodeResources() {
    return mergeCodeResources;
  }

  public void setMergeCodeResources(boolean mergeCodeResources) {
    this.mergeCodeResources = mergeCodeResources;
  }
}
