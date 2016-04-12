package com.coremedia.livecontext.context;

import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.Category;

public interface LiveContextNavigation extends Navigation {

  /**
   * Returns the external id.
   * @return the external id
   */
  String getExternalId();

  /**
   * Returns the category.
   * @return the category
   */
  Category getCategory();

  /**
   * Returns the site.
   * @return the site.
   */
  Site getSite();
}
