package com.coremedia.blueprint.cae.web.i18n;

import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;

import java.util.ResourceBundle;

public interface PageResourceBundleFactory {
  /**
   * Returns the ResourceBundle for the page.
   */
  ResourceBundle resourceBundle(Page page);

  /**
   * Returns the ResourceBundle for the Navigation.
   */
  ResourceBundle resourceBundle(Navigation navigation);
}
