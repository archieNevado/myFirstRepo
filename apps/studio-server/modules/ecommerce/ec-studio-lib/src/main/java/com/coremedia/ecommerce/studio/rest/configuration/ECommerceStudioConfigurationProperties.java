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

  // Note, this javadoc is copied to the deployment-en manual. Please keep the manual in sync.

  /**
   * The default behavior of the Studio library catalog tree is to load the next level of categories
   * no matter if they are displayed.
   * This is done to determine if a child category is virtual or not.
   * All occurrences of a category that are not in the primary location in the catalog tree are considered as virtual.
   * Set this property to {@link PreloadChildCategories#ALL_EXCEPT_TOP_LEVEL} if top level
   * categories should be excluded from pre-loading.
   * It can be useful if there is a huge number of top level categories
   * and if you are sure they are not virtual.
   * In commerce systems where no physical root category exists it must be this way.
   * Moreover, if you are sure there is no virtual category at all you can
   * use the value {@link PreloadChildCategories#NONE}.
   * If a child category is not pre-loaded, its state is assumed to be non-virtual.
   */
  private PreloadChildCategories preloadChildCategories = PreloadChildCategories.ALL;

  public PreloadChildCategories getPreloadChildCategories() {
    return preloadChildCategories;
  }

  public void setPreloadChildCategories(PreloadChildCategories preloadChildCategories) {
    this.preloadChildCategories = preloadChildCategories;
  }
}
