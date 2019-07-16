package com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.resources;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.shop.OCShopApiConnector;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractShopResource {

  private OCShopApiConnector connector;

  protected OCShopApiConnector getConnector() {
    return connector;
  }

  @Autowired
  void setConnector(OCShopApiConnector sfccShopApiConnector) {
    this.connector = sfccShopApiConnector;
  }

}
