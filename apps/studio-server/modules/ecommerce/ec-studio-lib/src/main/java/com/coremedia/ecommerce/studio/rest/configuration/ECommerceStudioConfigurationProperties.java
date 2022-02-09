package com.coremedia.ecommerce.studio.rest.configuration;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Properties to configure studio commerce integration.
 */
@ConfigurationProperties(prefix = "studio.commerce")
@DefaultAnnotation(NonNull.class)
@Validated
public class ECommerceStudioConfigurationProperties {

  /**
   * All child categories are fully preloaded in Studio by default to determine if they are virtual or not.
   * Set this property to {@link PreloadChildCategories#ALL_EXCEPT_TOP_LEVEL} if the top level
   * categories shouldn't be preloaded when loading the root category. It can be useful if
   * there is a huge number of top level categories and you are sure about that they cannot
   * be virtual. In commerce systems where no physical root category exists it can't be any
   * other way. Moreover, if you are sure that there is no virtual category at all you can
   * use {@link PreloadChildCategories#NONE}. In case a child category is not preloaded
   * its virtual state is always false.
   */
  private PreloadChildCategories preloadChildCategories = PreloadChildCategories.ALL;

  public PreloadChildCategories getPreloadChildCategories() {
    return preloadChildCategories;
  }

  public void setPreloadChildCategories(PreloadChildCategories preloadChildCategories) {
    this.preloadChildCategories = preloadChildCategories;
  }
}
