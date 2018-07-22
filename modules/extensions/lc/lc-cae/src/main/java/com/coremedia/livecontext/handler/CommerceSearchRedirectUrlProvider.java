package com.coremedia.livecontext.handler;

import com.coremedia.livecontext.ecommerce.common.StoreContext;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.servlet.http.HttpServletRequest;

/**
 * Provider for search URLs pointing to the commerce system.
 */
public interface CommerceSearchRedirectUrlProvider {
  /**
   * Provide the commerce search URL for redirect.
   * @param term the optional search term
   * @param request the current request
   * @param storeContext the current store context
   * @return a commerce search URL
   */
  Object provideRedirectUrl(@Nullable String term, @NonNull HttpServletRequest request, @NonNull StoreContext storeContext);
}
