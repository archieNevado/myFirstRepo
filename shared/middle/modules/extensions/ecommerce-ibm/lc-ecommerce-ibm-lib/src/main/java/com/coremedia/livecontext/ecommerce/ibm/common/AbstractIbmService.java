package com.coremedia.livecontext.ecommerce.ibm.common;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public abstract class AbstractIbmService {

  private IbmCommerceIdProvider commerceIdProvider;

  @Autowired
  public void setCommerceIdProvider(IbmCommerceIdProvider commerceIdProvider) {
    this.commerceIdProvider = commerceIdProvider;
  }

  protected IbmCommerceIdProvider getCommerceIdProvider() {
    return commerceIdProvider;
  }
}
