package com.coremedia.livecontext.fragment.links;

import com.coremedia.livecontext.ecommerce.link.StorefrontRefKey;

/**
 * Enumeration of well-known {@link StorefrontRefKey}s.
 */
public enum CommerceLinkTemplateTypes implements StorefrontRefKey {
  EXTERNAL_PAGE_SEO,
  EXTERNAL_PAGE_NON_SEO,
  LOGIN,
  LOGOUT,
  SEARCH_REDIRECT,
  CHECKOUT_REDIRECT;

  private final String value = name().toLowerCase().replace("_", "");

  @Override
  public String value() {
    return value;
  }
}
