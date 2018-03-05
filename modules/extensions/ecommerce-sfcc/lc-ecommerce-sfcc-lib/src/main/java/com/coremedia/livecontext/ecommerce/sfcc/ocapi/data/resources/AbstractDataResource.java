package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.OCAPIConnector;
import org.springframework.beans.factory.annotation.Autowired;

abstract class AbstractDataResource {

  private OCAPIConnector connector;

  OCAPIConnector getConnector() {
    return connector;
  }

  @Autowired
  void setConnector(OCAPIConnector sfccDataApiConnector) {
    this.connector = sfccDataApiConnector;
  }

}
